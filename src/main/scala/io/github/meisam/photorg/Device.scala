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

package io.github.meisam.photorg

import cats.free.Free
import cats.free.Free.liftF
import cats.syntax.all.*
import cats.syntax.traverse

type ErrorMessages = String
type DeviceId = String
enum MediaFile(name: String):
  case OriginalMediaFile(name: String) extends MediaFile(name)
  case PulledMediaFile(name: String) extends MediaFile(name)
  case PushedMediaFile(name: String) extends MediaFile(name)

import MediaFile.{OriginalMediaFile, PulledMediaFile, PushedMediaFile}
enum AndroidDeviceA[A]:
  case GetMediaFiles(deviceId: DeviceId, directory: String)
      extends AndroidDeviceA[List[OriginalMediaFile]]
  case GetFileSize(deviceId: DeviceId, mediaFile: OriginalMediaFile)
    extends AndroidDeviceA[Long]
  case PullMediaFile(deviceId: DeviceId, mediaFile: OriginalMediaFile)
      extends AndroidDeviceA[PulledMediaFile]
  case PushMediaFile(deviceId: DeviceId, mediaFile: PulledMediaFile)
      extends AndroidDeviceA[PushedMediaFile]

type AndroidDevice[A] = Free[AndroidDeviceA, A]

import AndroidDeviceA.*

def getMediaFiles(
    deviceId: DeviceId,
    directory: String
): AndroidDevice[List[OriginalMediaFile]] =
  liftF[AndroidDeviceA, List[OriginalMediaFile]](GetMediaFiles(deviceId, directory))

def getFileSize(deviceId: DeviceId)(
  mediaFile: OriginalMediaFile
): AndroidDevice[Long] = 
  liftF[AndroidDeviceA, Long](GetFileSize(deviceId, mediaFile))
def pullMediaFile(deviceId: DeviceId)(
    mediaFile: OriginalMediaFile
): AndroidDevice[PulledMediaFile] =
  liftF[AndroidDeviceA, PulledMediaFile](PullMediaFile(deviceId, mediaFile))

def pushMediaFile(deviceId: DeviceId)(
    mediaFile: PulledMediaFile
): AndroidDevice[PushedMediaFile] =
  liftF[AndroidDeviceA, PushedMediaFile](PushMediaFile(deviceId, mediaFile))

def backupMediaFilesApp(
    sourceDeviceId: DeviceId,
    targetDeviceId: DeviceId,
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

import cats.arrow.FunctionK
import cats.{Id, ~>}
val mockCompiler: AndroidDeviceA ~> Id = new:
  val files: List[OriginalMediaFile] = List("Image1.jpg", "image2.CR2", "imag3.MOV", "image4.mp4").map(OriginalMediaFile.apply)
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

@main
def freeMonadRun =
  val backedupFiles = backupMediaFilesApp("1234", "abcd", "/DCIM").foldMap(mockCompiler)
  println(backedupFiles)
