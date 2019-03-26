package org.kaikikm.scala.clever.io.resources

import org.apache.commons.io.FilenameUtils
import org.kaikikm.threadresloader.ResourceLoader
import org.scalatest.WordSpec

class TestResourceMatching extends WordSpec {
  private val exampleFileURL = ResourceLoader.getResource("org/kaikikm/scala/clever/io/resources/test.txt")
  private val exampleDirURL = ResourceLoader.getResource("org/kaikikm/scala/clever/io/resources/testDir")
  private val notExistURL = new java.io.File(FilenameUtils.concat(exampleDirURL.getPath, "hello")).toURI.toURL
  println(notExistURL)
  "The IOResources factory" must {
    "identify correctly an IO resource" when {
      "resource exists" when {
        "a String is given as input" in {
          IOResource(exampleFileURL.getPath) match {
            case _: IOResource =>
            case _ => fail()
          }
        }
        "an URL is given as input" in {
          IOResource(exampleFileURL) match {
            case _: IOResource =>
            case _ => fail()
          }
        }
      }
      "resource not exists" when {
        "a String is given as input" in {
          IOResource(notExistURL.getPath) match {
            case _: IOResource =>
            case _ => fail()
          }
        }
        "an URL is given as input" in {
          IOResource(notExistURL) match {
            case _: IOResource =>
            case _ => fail()
          }
        }
      }
    }

    "identify correctly a File Resource" when {
      "resource exists" when {
        "a String is given as input" in {
          IOResource(exampleFileURL.getPath) match {
            case _: FileResource =>
            case _ => fail()
          }
        }
        "an URL is given as input" in {
          IOResource(exampleFileURL) match {
            case _: FileResource =>
            case _ => fail()
          }
        }
      }
      "resource not exists" when {
        "a String is given as input" in {
          IOResource(notExistURL.getPath) match {
            case _: FileResource =>
            case _ => fail()
          }
        }
        "an URL is given as input" in {
          IOResource(notExistURL) match {
            case _: FileResource =>
            case _ => fail()
          }
        }
      }
    }

    "identify correctly a Directory Resource" when {
      "resource exists" when {
        "a String is given as input" in {
          IOResource(exampleDirURL.getPath) match {
            case _: DirectoryResource =>
            case _ => fail()
          }
        }

        "an URL is given as input" in {
          IOResource(notExistURL) match {
            case _: DirectoryResource =>
            case _ => fail()
          }
        }
      }
      "resource not exists" when {
        "a String is given as input" in {
          IOResource(exampleDirURL.getPath) match {
            case _: DirectoryResource =>
            case _ => fail()
          }
        }

        "an URL is given as input" in {
          IOResource(notExistURL) match {
            case _: DirectoryResource =>
            case _ => fail()
          }
        }
      }
    }
  }
}
