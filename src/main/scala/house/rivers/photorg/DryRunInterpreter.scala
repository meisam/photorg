package house.rivers.photorg

import cats.free.Free
import cats.free.Free.liftF
import cats.syntax.all.*
import cats.syntax.traverse

import cats.arrow.FunctionK
import cats.{Id, ~>}
import MediaFile.{OriginalMediaFile, PulledMediaFile, PushedMediaFile}
import AndroidDeviceA.*

def dryRunCommandInterpreter(fileNames: List[String]): AndroidDeviceA ~> Id = new:
  val files: List[OriginalMediaFile] =
    fileNames.map(
      OriginalMediaFile.apply
    )
  def apply[A](fa: AndroidDeviceA[A]): Id[A] =
    fa match
      case GetMediaFiles(deviceId: DeviceId, directory: String) =>
        files
      case GetFileSize(deviceId, file) =>
        val size = file.name.size.toLong
        size
      case PullMediaFile(deviceId, mediaFile) =>
        PulledMediaFile(mediaFile.name)
      case PushMediaFile(deviceId, mediaFile) =>
        PushedMediaFile(mediaFile.name)
