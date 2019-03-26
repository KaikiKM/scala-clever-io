package org.kaikikm.scala.clever.io.resources.notexisting

import java.net.URL

import org.apache.commons.io.FilenameUtils
import org.kaikikm.scala.clever.io.resources.existing.File.FileFormat
import org.kaikikm.scala.clever.io.resources.{FileResource, DirectoryResource, IOResource, IOResourceImpl}
import org.kaikikm.scala.clever.io.resources.existing.{File, Directory}

/** Trait that defines a generic IO resource  that not exists*/
sealed trait NotExistingResource extends IOResource

/** Trait that defines a directory that not already exists*/
sealed trait NotExistingDirectory extends NotExistingResource with DirectoryResource {
  /** Method creates not existing directory. The process fails if the resource needs super directorys
    * creation
    *
    * @return Created directory
    */
  def createDirectory(): Option[Directory]
}

/** Trait that defines a file that not already exists*/
sealed trait NotExistingFile extends NotExistingResource with FileResource {
  /** Method creates not existing file. The process fails if the resource needs super directorys
    * creation
    *
    * @return Created file
    */
  def createFile(): Option[File]
}

/** Trait that defines an undefined IO resource that not already exists*/
sealed trait UndefinedNotExistingResource extends NotExistingDirectory with NotExistingFile {
  def hasFileExtension(format: FileFormat): Boolean
}

class UndefinedNotExistingResourceImpl(path: URL) extends IOResourceImpl(path) with UndefinedNotExistingResource {
  require(!javaFile.exists())
  override def createFile(): Option[File] = {
    javaFile.createNewFile()
    Some(File(javaFile))
  }

  override def createDirectory(): Option[Directory] = {
    javaFile.mkdir()
    Some(Directory(javaFile))
  }

  override def hasFileExtension(format: FileFormat): Boolean = format.extensions.exists(ext => javaFile.getName.endsWith(ext))

  override def child(relativePath: String): IOResource =
    IOResource(FilenameUtils.concat(javaFile.getAbsolutePath, relativePath))
}