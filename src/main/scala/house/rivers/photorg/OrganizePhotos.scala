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

import scala.annotation.targetName
import scala.sys.process.*
import scala.sys.process.given
import scala.util.Try
import java.nio.file.Paths

/** The imparative version of the Photorg. It uses adb (Android Debug Bridge) to
  * pull the files from the device. Don't use this file. It was just to
  * sketching ideas on how we may interact with a device.
  */

/** a pattern to match the lines in the output of the `adb devices` command.
  * Example intput: 123456AB789CD device
  */
//                               ________________ a group of non-space characters
//                              /
//                             /       ______ the word "device"
//                            /       /
//                         v---v   v----v
val deviceIdPattern = """\A(\S+)\s+device\Z""".r
//                       ^^     ^^       ^^
// begining of the line _/      /        /
//                             /        /
// spaces or tabs ____________/        /
//                                    /
// end of the line __________________/

val cameraPath = "/sdcard/DCIM/Camera/"

@main
def OrganizePhotos =

  val listDevicesCommand = Seq(
    "adb",
    "devices"
  )
  val listeeDevicesRawOutput = listDevicesCommand.lazyLines
  val deviceIds = listeeDevicesRawOutput
    .collect { case deviceIdPattern(deviceId) =>
      deviceId
    }

  val cameraFiles = (for
    deviceId <- deviceIds.headOption.toRight("Cannont find any deviceId")
    listFilesCommand = Seq(
      "adb",
      "-s",
      deviceId,
      "shell",
      "ls",
      cameraPath
    )
    filePaths <- Try(listFilesCommand.lazyLines).toEither
  yield filePaths)
    .map(_.filter(_.endsWith(".mp4")).toList)

  println(s"cameraFiles: $cameraFiles")

  val x = cameraFiles match
    case Left(error) => println(error)
    case Right(files) =>
      for

        deviceId <- deviceIds.headOption.toRight("Cannont find any deviceId")
        output: Any = for
          file <- files
          _ = println(f"checking $file")
          cpCommand = Seq(
            "adb",
            "-s",
            deviceId,
            "pull",
            "-a", // to preserve the creation time of files during copy
            Paths.get(cameraPath, file).toString,
            "/tmp/abc/"
          )
        yield (Try(cpCommand.lazyLines).toEither)
      yield ()
