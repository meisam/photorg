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

type ErrorMessages = String
type DeviceId = String
type MediaFile = String

enum AndroidDeviceA[A]:
  case GetMediaFiles(deviceId: DeviceId, directory: String)
      extends AndroidDeviceA[List[MediaFile]]()
  case PullMediaFile(deviceId: DeviceId, mediaFile: MediaFile)
      extends AndroidDeviceA[MediaFile]
  case PushMediaFile(deviceId: DeviceId, mediaFile: MediaFile)
      extends AndroidDeviceA[MediaFile]

type AndroidDevice[A] = Free[AndroidDeviceA, A]

import AndroidDeviceA.*

def getMediaFiles(
    deviceId: DeviceId,
    directory: String
): AndroidDevice[List[MediaFile]] =
  liftF[AndroidDeviceA, List[MediaFile]](GetMediaFiles(deviceId, directory))

def pullMediaFile(deviceId: DeviceId)(
    mediaFile: MediaFile
): AndroidDevice[MediaFile] =
  liftF[AndroidDeviceA, MediaFile](PullMediaFile(deviceId, mediaFile))

def pushMediaFile(deviceId: DeviceId)(
    mediaFile: MediaFile
): AndroidDevice[MediaFile] =
  liftF[AndroidDeviceA, MediaFile](PushMediaFile(deviceId, mediaFile))

extension (list: List[AndroidDevice[MediaFile]])
  def sequence: AndroidDevice[List[MediaFile]] =
    val zero = Free.pure[AndroidDeviceA, List[MediaFile]](List.empty)
    list.foldLeft(zero)((accum, file) =>
      accum.flatMap(tail => file.map(g => g :: tail))
    )

def program(
    sourceDeviceId: DeviceId,
    targetDeviceId: DeviceId,
    cameraPath: String
): AndroidDevice[List[MediaFile]] =
  val zero: AndroidDevice[List[MediaFile]] =
    Free.pure[AndroidDeviceA, List[MediaFile]](List.empty)
  val pulledFiles: AndroidDevice[List[MediaFile]] =
    getMediaFiles(sourceDeviceId, cameraPath)
      .map(files => files.map(pullMediaFile(sourceDeviceId)))
      .flatMap(_.sequence)
  val pushedFiles: AndroidDevice[List[MediaFile]] =
    pulledFiles
      .map(files => files.map(pushMediaFile(targetDeviceId)))
      .flatMap(_.sequence)
  pushedFiles

import cats.arrow.FunctionK
import cats.{Id, ~>}
import scala.collection.mutable

val impureCompiler: AndroidDeviceA ~> Id = new:
  var files = List("Image1.jpg", "image2.CR2", "imag3.MOV", "image4.mp4")
  def apply[A](fa: AndroidDeviceA[A]): Id[A] =
    fa match
      case GetMediaFiles(deviceId: DeviceId, directory: String) =>
        println(f"GetMedia is called: $deviceId")
        files
      case PullMediaFile(deviceId: DeviceId, mediaFile: MediaFile) =>
        println(f"PullMediaFile is called: $mediaFile")
        mediaFile
      case PushMediaFile(deviceId: DeviceId, mediaFile: MediaFile) =>
        println(f"PushMediaFile is called: $mediaFile")
        mediaFile

@main
def freeMonadRun =
  val x = program("1234", "abcd", "/DCIM").foldMap(impureCompiler)
