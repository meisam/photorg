load("@rules_scala//scala:scala.bzl", "scala_binary", "scala_library", "scala_test")

scala_binary(
    name = "photorg",
    main_class = "house.rivers.photorg.freeMonadRun",
    deps = [
        ":core_lib",
    ],
)

scala_library(
    name = "core_lib",
    srcs = [
        "src/main/scala/house/rivers/photorg/Core.scala",
    ],
    deps = [
        ":scala_cats_deps",
    ],
)


scala_library(
    name = "scala_cats_deps",
    exports = [
        "@maven//:org_typelevel_cats_core_3_2_13_0",
        "@maven//:org_typelevel_cats_free_3_2_13_0",
        "@maven//:org_typelevel_cats_kernel_3_2_13_0",
    ],
)

scala_library(
    name="on_device_interpreter_lib",
    srcs = [
        "src/main/scala/house/rivers/photorg/OnDeviceInterpreter.scala"
    ],
    deps = [
        ":core_lib",
        ":scala_cats_deps",
    ]
)

scala_library(
    name = "dry_run_interpreter_lib",
    srcs = [
        "src/main/scala/house/rivers/photorg/DryRunInterpreter.scala",
    ],
    deps = [
        ":core_lib",
        ":scala_cats_deps",
    ],
)

scala_test(
    name = "dry_run_interpreter_test",
    srcs = ["src/test/scala/house/rivers/photorg/DryRunInerpreterSpec.scala"],
    deps = [
        ":dry_run_interpreter_lib",
        ":core_lib",
        ":scala_cats_deps",
    ],
)
