package org.kaikikm.scala.clever.io.resources.existing

import java.net.URL

import org.kaikikm.scala.clever.io.resources.{IOResource, IOResourceImpl}

/** Trait that defines a generic IO resource that already exists*/
trait ExistingResource extends IOResource {
  /**
    * @return Java file corresponding to this resource
    */
  def rawFile: java.io.File


  /**
    * @return Parent directory
    */
  def parentDirectory: Option[Directory]
}

abstract class ExistingResourceImpl(path: URL) extends IOResourceImpl(path) with ExistingResource {
  require(javaFile.exists())
  val rawFile: java.io.File = javaFile

  override def parentDirectory: Option[Directory] = {
    parent match {
      case Some(r: Directory) =>
        Some(r)
      case _ =>
        throw new IllegalStateException()
    }
  }
}
