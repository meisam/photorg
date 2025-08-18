load("@rules_scala//scala:scala.bzl", "scala_binary", "scala_library", "scala_test")

scala_binary(
    name = "photorg",
    main_class = "house.rivers.photorg.freeMonadRun",
    deps = [
        ":photorg_lib",
    ],
)

scala_library(
    name = "photorg_lib",
    srcs = [
        "src/main/scala/house/rivers/photorg/Device.scala",
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
    name = "dry_run_interpreter_lib",
    srcs = [
        "src/main/scala/house/rivers/photorg/DryRunInterpreter.scala",
    ],
    deps = [
        ":photorg_lib",
        ":scala_cats_deps",
    ],
)

scala_test(
    name = "dry_run_interpreter_test",
    srcs = ["src/test/scala/house/rivers/photorg/DryRunInerpreterSpec.scala"],
    deps = [
        ":dry_run_interpreter_lib",
        ":photorg_lib",
        ":scala_cats_deps",
    ],
)
