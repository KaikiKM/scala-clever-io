package org.kaikikm.scala.clever.io.resources.existing

import java.io
import java.io.InputStream
import java.net.URL

import org.apache.commons.io.{FileUtils, FilenameUtils}
import org.kaikikm.scala.clever.io.resources.existing.File.FileFormat
import org.kaikikm.scala.clever.io.resources.{DirectoryResource, IOResource}

/** Trait that defines a directory that already exists*/
trait Directory extends ExistingResource with DirectoryResource {

  /**
    * @param fileFormats Optional file formats used as filter
    * @return Files contained in the directory
    */
  def files(fileFormats: FileFormat*): Seq[File]

  /**
    * @param fileFormats Optional file formats used as filter
    * @return Files contained in the directory as InputStream
    */
  def filesAsStream(fileFormats: FileFormat*): Seq[InputStream]

  def directorys(): Seq[Directory]

  /** Obtain child resource with relative path starting from this directory as input
    *
    * @param relativePath Child's relative path
    * @return An option containing child if exists
    */
  def existingChild(relativePath: String): Option[ExistingResource]

  def copyInside(file: File) //TODO for generic existing resource
}

/** Factory object for [[org.kaikikm.scala.clever.io.resources.existing.Directory]]*/
object Directory {

  /** Creates [[org.kaikikm.scala.clever.io.resources.existing.Directory]] starting from its path as URL
    *
    * @param directoryPath Directory's URL
    * @return Required Directory
    */
  def apply(directoryPath: URL): Directory = new DirectoryImpl(directoryPath)

  /** Creates [[org.kaikikm.scala.clever.io.resources.existing.Directory]] starting from its path as String
    *
    * @param directoryPath Directory's path
    * @return Required Directory
    */
  def apply(directoryPath: String): Directory = Directory(new io.File(directoryPath))

  /** Creates [[org.kaikikm.scala.clever.io.resources.existing.Directory]] starting from a java File
    *
    * @param file Java file
    * @return Required Directory
    */
  def apply(file: java.io.File): Directory = new DirectoryImpl(file.toURI.toURL)

  private[this] class DirectoryImpl(directoryPath: URL) extends ExistingResourceImpl(directoryPath) with Directory {
    require(javaFile.isDirectory)

    def getFiles: Seq[File] = javaFile.listFiles().filter(_.isFile).map(File(_)).toSeq

    def files(fileFormats: FileFormat*): Seq[File] = {
      var files = javaFile.listFiles().filter(_.isFile).map(File(_))
      if(fileFormats.nonEmpty) {
        files = files.filter(f => fileFormats.flatMap(_.extensions).exists(ext => f.name.endsWith(ext)))
      }
      files.toSeq
    }

    def filesAsStream(fileFormats: FileFormat*): Seq[InputStream] = convertToInputStream(files(fileFormats:_*))

    private def convertToInputStream(urls: Seq[File]): Seq[InputStream] = urls.map(_.rawFile).map(FileUtils.openInputStream)

    override def existingChild(relativePath: String): Option[ExistingResource] = {
      IOResource(FilenameUtils.concat(javaFile.getAbsolutePath, relativePath)) match {
        case r: ExistingResource =>
          Some(r)
        case _ =>
          None
      }
    }

    override def child(relativePath: String): IOResource =
      IOResource(FilenameUtils.concat(javaFile.getAbsolutePath, relativePath))

    override def copyInside(file: File): Unit = {
      FileUtils.copyFileToDirectory(file.rawFile, this.rawFile)
    }

    override def directorys(): Seq[Directory] = {
      javaFile.listFiles().filter(_.isDirectory).map(Directory(_)).toSeq
    }
  }
}


