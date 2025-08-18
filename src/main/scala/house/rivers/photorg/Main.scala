package house.rivers.photorg

@main
def main(
    sourceDeviceId: String,
    targetDeviceId: String,
    mediaDirectory: String
): Unit =
  val backedupFiles =
    backupMediaFilesApp(Device.fromStringId(sourceDeviceId), Device.fromStringId(targetDeviceId), mediaDirectory)
  .foldMap(onDeviceCommandnInterpreter)
  println(backedupFiles)
