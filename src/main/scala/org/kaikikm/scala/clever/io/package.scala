package org.kaikikm.scala.clever

import org.kaikikm.scala.clever.io.resources.existing.File

package object io {
  implicit def myFileToJavaFile(file: File): java.io.File = file.rawFile
}
