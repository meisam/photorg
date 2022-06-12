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
 
 val scala3Version = "3.1.2"

lazy val root = project
  .in(file("."))
  .settings(
    name := "Photorg",
    description := "Photo Organizer",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
        "org.scalameta" %% "munit" % "0.7.29" % Test,
        "org.typelevel" %% "cats-free" % "2.7.0",
    )
  )

scalacOptions ++=
  Seq(
    "-encoding",
    "UTF-8",
    "-feature",
    "-Xfatal-warnings",
    "-unchecked",
    "-deprecation",
    "-indent",
    "-new-syntax",
    "-source:future-migration"
  )