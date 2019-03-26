package org.kaikikm.scala.clever.io.resources

import java.net.URL

import org.kaikikm.scala.clever.io.resources.existing.{Directory, ExistingResource, File}
import org.kaikikm.scala.clever.io.resources.notexisting.{NotExistingDirectory, NotExistingFile, UndefinedNotExistingResourceImpl}

/** Trait that defines a generic IO resource*/
trait IOResource {
  /**
    * @return Parent. It is a directory, that can exists or not
    */
  def parent: Option[DirectoryResource]

  /**
    * @return Resource name
    */
  def name: String
}

/** Trait that defines a directory IO resource, that can exist or not*/
trait DirectoryResource extends IOResource {
  /** Method returns existing directory corresponding to this directory resource. If the directory not exists, it is created.
    * The creation process fails if the resource needs super directorys creation
    *
    * @return Required directory
    */
  def getOrCreateDirectory(): Option[Directory] = this match {
    case f: NotExistingDirectory =>
      f.createDirectory()
    case f: Directory =>
      Some(f)
  }

  /** Obtain child resource with relative path starting from this directory as input
    *
    * @param relativePath Child's relative path
    * @return Child IOResource that can be matched to check child status (File, Directory, ecc...)
    */
  def child(relativePath: String): IOResource
}

/** Trait that defines a file IO resource, that can exist or not*/
trait FileResource extends IOResource {

  /** Method returns existing file corresponding to this directory resource. If the file not exists, it is created.
    * The creation process fails if the resource needs super directorys creation
    *
    * @return Required file
    */
  def getOrCreateFile(): Option[File] = this match {
    case f: NotExistingFile =>
      f.createFile()
    case f: File =>
      Some(f)
  }
}

/** Factory object to create IO resources starting from various representations*/
object IOResource {

  /** Creates the IO resource corresponding to given representation. Result can be matched to identify resource status
    *
    * @param path Resource's path
    * @return Resource
    */
  def apply(path: String): IOResource = {
    apply(new java.io.File(path).toURI.toURL)
  }

  /** Creates the IO resource corresponding to given representation. Result can be matched to identify resource status
    *
    * @param path Resource's URL
    * @return Resource
    */
  def apply(path: URL): IOResource = {
    val file: java.io.File = new java.io.File(path.toURI)
    if(file.exists()) {
      if(file.isFile) {
        File(path)
      } else {
        Directory(path)
      }
    } else {
      new UndefinedNotExistingResourceImpl(path)
    }
  }

}

abstract class IOResourceImpl(path: URL) extends IOResource {
  protected val url: URL = path
  require(url != null)
  protected val javaFile: java.io.File = new java.io.File(url.toURI)

  def parent: Option[DirectoryResource] = {
    if(javaFile.getParent == null)
      None
    else
      IOResource(javaFile.getParent) match {
        case r: DirectoryResource =>
          Some(r)
        case _ =>
          throw new IllegalStateException()
      }
  }

  def name: String = javaFile.getName

  def canEqual(other: Any): Boolean = other.isInstanceOf[IOResourceImpl]

  override def equals(other: Any): Boolean = other match {
    case that: IOResourceImpl =>
      (that canEqual this) &&
        url.equals(that.url)
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(url)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }

  override def toString: String = url.toString
}