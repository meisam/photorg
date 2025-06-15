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

val mockCommandInterpreter: AndroidDeviceA ~> Id = new:
  val files: List[OriginalMediaFile] =
    List("Image1.jpg", "image2.CR2", "imag3.MOV", "image4.mp4").map(
      OriginalMediaFile.apply
    )
  def apply[A](fa: AndroidDeviceA[A]): Id[A] =
    fa match
      case GetMediaFiles(deviceId: DeviceId, directory: String) =>
        println(f"GetMedia is called: $deviceId")
        files
      case GetFileSize(deviceId, file) =>
        val size = file.name.size.toLong
        println(f"GetFileSize is called: $file has size $size")
        size
      case PullMediaFile(deviceId, mediaFile) =>
        println(f"PullMediaFile is called: $mediaFile")
        PulledMediaFile(mediaFile.name)
      case PushMediaFile(deviceId, mediaFile) =>
        println(f"PushMediaFile is called: $mediaFile")
        PushedMediaFile(mediaFile.name)

class MockCommandInterpreterSpec extends AnyFlatSpec with Matchers:
  "backupMediaFilesApp" should "find all files on the Device using the mock interpreter" in {
    val backedupFiles =
      backupMediaFilesApp("DEVICE_ID_ABCD", "xxxxx", "/sdcard/DCIM/Camera/")
        .foldMap(mockCommandInterpreter)
    backedupFiles.size should be(4)
    backedupFiles shouldEqual
      List("Image1.jpg", "image2.CR2", "imag3.MOV", "image4.mp4")
        .map(PushedMediaFile.apply)
  }
