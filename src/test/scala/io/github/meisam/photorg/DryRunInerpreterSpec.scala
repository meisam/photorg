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

class DryRunInterpreterSpec extends AnyFlatSpec with Matchers:
  "getMediaFiles" should "find all files on the Device using the mock interpreter" in:
    val backedupFiles: List[OriginalMediaFile] =
      getMediaFiles("SOURCE_DEVICE_ID", "/sdcard/DCIM/Camera/")
        .foldMap(dryRunCommandInterpreter(
      List("Image1.jpg", "IMG.CR2", "Video2.MOV", "VID2.mp4")
        ))
    backedupFiles.size should be(4)
    backedupFiles shouldEqual
      List("Image1.jpg", "IMG.CR2", "Video2.MOV", "VID2.mp4")
        .map(OriginalMediaFile.apply)

  "getFileSize" should "find get the file size correctly" in:
    val fileSizes =
      getOriginalFileSizes("DEVICE_ID", "/sdcard/DCIM/Camera/")
      .foldMap(dryRunCommandInterpreter(List("Image1.jpg", "IMG.CR2", "video1.MOV", "VID2.mp4")))
    fileSizes shouldEqual List(10L, 7L, 10L, 8L)

  "backupMediaFilesApp" should "find get the file name correctly" in:
    val backedupFiles =
      backupMediaFilesApp("SOURCE_DEVICE_ID", "TARGET_DEVICE_ID", "/sdcard/DCIM/Camera/")
      .foldMap(dryRunCommandInterpreter(List("Image1.jpg", "IMG.CR2", "video1.MOV", "VID2.mp4")))
    backedupFiles shouldEqual List("Image1.jpg", "IMG.CR2", "video1.MOV", "VID2.mp4").map(PushedMediaFile.apply)
