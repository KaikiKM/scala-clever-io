package org.kaikikm.scala.clever.io.resources.existing

import java.io.InputStream
import java.net.URL
import java.nio.charset.{Charset, StandardCharsets}

import org.apache.commons.io.FileUtils
import org.kaikikm.scala.clever.io.resources.FileResource

/** Trait that defines a file that already exists*/
trait File extends ExistingResource with FileResource {

  /**
    * @return File name
    */
  def name: String

  /**
    * @return File's input stream
    */
  def openInputStream(): InputStream

  /** The method writes text to file. Old content of file is overwritten
    *
    * @param content Text to write
    */
  def write(content: String, encoding: Charset)

  def writeLines(content: Seq[String], encoding: Charset)

  /** The method appends text to file
    *
    * @param content Text to write
    */
  def append(content: String, encoding: Charset)

  def copyTo(directory: Directory)

  def moveTo(directory: Directory)

  def readString(encoding: Charset): String

  def readLines(encoding: Charset): Seq[String]

  def readBytes(): Array[Byte]
}

/** Factory object for [[org.kaikikm.scala.clever.io.resources.existing.File]]*/
object File {

  /** Abstract class representing a file format*/
  trait FileFormat{def extensions: Seq[String]}
  /** Object containing default supported file extensions*/
  object FileFormats {
    case object YAML extends FileFormat{val extensions: Seq[String] = Seq(".yml", ".yaml")}
  }

  /** Creates [[org.kaikikm.scala.clever.io.resources.existing.File]] starting from its path as URL
    *
    * @param filePath File's URL
    * @return Required File
    */
  def apply(filePath: URL): File = new FileImpl(filePath)

  /** Creates [[org.kaikikm.scala.clever.io.resources.existing.File]] starting from its path as String
    *
    * @param filePath File's path as String
    * @return Required File
    */
  def apply(filePath: String): File = new FileImpl(new java.io.File(filePath).toURI.toURL)

  /** Creates [[org.kaikikm.scala.clever.io.resources.existing.File]] starting from a java file
    *
    * @param file Java file
    * @return Required File
    */
  def apply(file: java.io.File): File = new FileImpl(file.toURI.toURL)

  private class FileImpl(filePath: URL) extends ExistingResourceImpl(filePath) with File {
    override val name: String = javaFile.getName
    require(javaFile.isFile)

    override def openInputStream(): InputStream = FileUtils.openInputStream(javaFile)

    override def write(content: String, encoding: Charset): Unit = {
      FileUtils.writeStringToFile(this, content, encoding, false)
    }

    override def append(content: String, encoding: Charset): Unit = {
      FileUtils.writeStringToFile(this, content, encoding, true)
    }

    override def copyTo(directory: Directory): Unit = directory.copyInside(this)

    override def moveTo(directory: Directory): Unit = directory.moveInside(this)

    override def readString(encoding: Charset): String = FileUtils.readFileToString(this, encoding)

    override def readLines(encoding: Charset): Seq[String] =
      scala.collection.JavaConverters.asScalaBuffer(FileUtils.readLines(this, encoding))

    override def writeLines(content: Seq[String], encoding: Charset): Unit = write(content.mkString("\r\n"), encoding)

    override def readBytes(): Array[Byte] = FileUtils.readFileToByteArray(this)
  }
}