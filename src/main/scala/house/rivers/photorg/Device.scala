/*
  This file is part of Picture Organizer for Pixel.

  Picture Organizer for Pixel is free software: you can redistribute it and/or modify it under the terms of
  the GNU General Public License as published by the Free Software Foundation,
  either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
  See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program.
  If not, see <https://www.gnu.org/licenses/>.
 */

package house.rivers.photorg

import cats.free.Free
import cats.free.Free.liftF
import cats.syntax.all.*
import cats.syntax.traverse


object Device:
  opaque type ErrorMessages = String
  opaque type DeviceId = String
  def fromStringId(deviceId: String): DeviceId = deviceId

enum MediaFile(name: String):
  case OriginalMediaFile(name: String) extends MediaFile(name)
  case PulledMediaFile(name: String) extends MediaFile(name)
  case PushedMediaFile(name: String) extends MediaFile(name)
  def getName: String = name

import MediaFile.{OriginalMediaFile, PulledMediaFile, PushedMediaFile}
enum AndroidDeviceA[A]:
  case GetMediaFiles(deviceId: Device.DeviceId, directory: String)
      extends AndroidDeviceA[List[OriginalMediaFile]]
  case GetFileSize(deviceId: Device.DeviceId, mediaFile: OriginalMediaFile)
      extends AndroidDeviceA[Long]
  case PullMediaFile(deviceId: Device.DeviceId, mediaFile: OriginalMediaFile)
      extends AndroidDeviceA[PulledMediaFile]
  case PushMediaFile(deviceId: Device.DeviceId, mediaFile: PulledMediaFile)
      extends AndroidDeviceA[PushedMediaFile]

type AndroidDevice[A] = Free[AndroidDeviceA, A]

import AndroidDeviceA.*

def getMediaFiles(
    deviceId: Device.DeviceId,
    directory: String
): AndroidDevice[List[OriginalMediaFile]] =
  liftF[AndroidDeviceA, List[OriginalMediaFile]](
    GetMediaFiles(deviceId, directory)
  )

def getFileSize(deviceId: Device.DeviceId)(
    mediaFile: OriginalMediaFile
): AndroidDevice[Long] =
  liftF[AndroidDeviceA, Long](GetFileSize(deviceId, mediaFile))

def pullMediaFile(deviceId: Device.DeviceId)(
    mediaFile: OriginalMediaFile
): AndroidDevice[PulledMediaFile] =
  liftF[AndroidDeviceA, PulledMediaFile](PullMediaFile(deviceId, mediaFile))

def pushMediaFile(deviceId: Device.DeviceId)(
    mediaFile: PulledMediaFile
): AndroidDevice[PushedMediaFile] =
  liftF[AndroidDeviceA, PushedMediaFile](PushMediaFile(deviceId, mediaFile))

def backupMediaFilesApp(
    sourceDeviceId: Device.DeviceId,
    targetDeviceId: Device.DeviceId,
    cameraPath: String
): AndroidDevice[List[MediaFile]] =
  val filePullingFunction = pullMediaFile(sourceDeviceId)
  val filePushingFunction = pushMediaFile(targetDeviceId)
  for
    mediaFiles <- getMediaFiles(sourceDeviceId, cameraPath)
    totalSize <- mediaFiles.map(getFileSize(sourceDeviceId)).sequence.map(_.sum)
    filesToBePulled <- mediaFiles.traverse(filePullingFunction)
    filesToBePushed <- filesToBePulled.traverse(filePushingFunction)
  yield filesToBePushed

def getOriginalFileSizes(
  sourceDeviceId: Device.DeviceId,
  cameraPath: String
): AndroidDevice[List[Long]] =
  val fileListingFunction=  pullMediaFile(sourceDeviceId)
  val getFileSizeFunction = getFileSize(sourceDeviceId)
  for
    mediaFiles <- getMediaFiles(sourceDeviceId, cameraPath)
    sizes <- mediaFiles.map(getFileSizeFunction).sequence
  yield sizes

import cats.arrow.FunctionK
import cats.{Id, ~>}
val onDeviceCommandnInterpreter: AndroidDeviceA ~> Id = new:
  import scala.sys.process.*
  val files: List[OriginalMediaFile] =
    List("Image1.jpg", "image2.CR2", "imag3.MOV", "image4.mp4").map(
      OriginalMediaFile.apply
    )
  def apply[A](fa: AndroidDeviceA[A]): Id[A] =
    fa match
      case GetMediaFiles(deviceId: Device.DeviceId, directory: String) =>
        cats.Id(
          s"adb -s $deviceId shell find '$directory' -type f".lazyLines
            .map[OriginalMediaFile](OriginalMediaFile.apply)
            .toList
        )
      case GetFileSize(deviceId, file) =>
        val size =
          s"adb -s $deviceId shell du '${file.name}'".lazyLines.headOption
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

def backup(
    sourceDeviceId: String,
    targetDeviceId: String,
    mediaDirectory: String
): List[PushedMediaFile] =
  List.empty

@main
def main(
    sourceDeviceId: String,
    targetDeviceId: String,
    mediaDirectory: String
): Unit =
  val backedupFiles =
    backupMediaFilesApp(Device.fromStringId(sourceDeviceId), Device.fromStringId(targetDeviceId), mediaDirectory)
  // .foldMap(onDeviceCommandnInterpreter)
  println(backedupFiles)
