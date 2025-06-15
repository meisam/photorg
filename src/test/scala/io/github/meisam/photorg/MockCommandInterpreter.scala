package io.github.meisam.photorg

import cats.free.Free
import cats.free.Free.liftF
import cats.syntax.all.*
import cats.syntax.traverse

import cats.arrow.FunctionK
import cats.{Id, ~>}
import MediaFile.{OriginalMediaFile, PulledMediaFile, PushedMediaFile}
import AndroidDeviceA.*

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MockCommandInterpreterSpec extends AnyFlatSpec with Matchers:
  "backupMediaFilesApp" should "find all files on the Device using the mock interpreter" in {
    val backedupFiles =
      backupMediaFilesApp("DEVICE_ID_ABCD", "xxxxx", "/sdcard/DCIM/Camera/")
        .foldMap(dryRunCommandInterpreter)
    backedupFiles.size should be(4)
    backedupFiles shouldEqual
      List("Image1.jpg", "image2.CR2", "imag3.MOV", "image4.mp4")
        .map(PushedMediaFile.apply)
  }
