package house.rivers.photorg

import cats.arrow.FunctionK
import cats.{Id, ~>}

import AndroidDeviceA.*
import MediaFile.*

val onDeviceCommandnInterpreter: AndroidDeviceA ~> Id = new:
  import scala.sys.process.*
  def apply[A](fa: AndroidDeviceA[A]): Id[A] =
    fa match
      case GetMediaFiles(deviceId: Device.DeviceId, directory: String) =>
        cats.Id(
          s"adb -s $deviceId exec-out find '$directory' -type f".lazyLines
            .map[OriginalMediaFile](OriginalMediaFile.apply)
            .toList
        )
      case GetFileSize(deviceId, file) =>
        val size =
          s"adb -s $deviceId exec-out du '${file.name}'".lazyLines.headOption
            .map(_.takeWhile(_.isDigit).toLong)
            .getOrElse(-1L)
        println(f"GetFileSize is called: $file has size $size")
        size
      case PullMediaFile(deviceId, mediaFile) =>
        println(f"PullMediaFile is called: $mediaFile")
        PulledMediaFile(mediaFile.name)
      case PushMediaFile(deviceId, mediaFile) =>
        println(f"PushMediaFile is called: $mediaFile")
        PushedMediaFile(mediaFile.name)
