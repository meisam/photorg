load("@rules_scala//scala:scala.bzl", "scala_binary", "scala_library", "scala_test")

scala_binary(
    name = "photorg",
    main_class = "io.github.meisam.photorg.freeMonadRun",
    deps = [
        ":photorg_lib",
    ],
)

scala_library(
    name = "photorg_lib",
    srcs = [
        "src/main/scala/io/github/meisam/photorg/Device.scala",
    ],
    deps = [
        ":scala_cats_deps",
    ],
)

scala_library(
    name = "scala_cats_deps",
    exports = [
        "@maven//:com_monovore_decline_3_2_4_1",
        "@maven//:org_typelevel_cats_core_3_2_9_0",
        "@maven//:org_typelevel_cats_free_3_2_9_0",
        "@maven//:org_typelevel_cats_kernel_3_2_9_0",
    ],
)

scala_library(
    name = "dry_run_interpreter_lib",
    srcs = [
        "src/main/scala/io/github/meisam/photorg/DryRunInterpreter.scala",
    ],
    deps = [
        ":photorg_lib",
        ":scala_cats_deps",
    ],
)

scala_test(
    name = "mock_command_interpreter_test",
    srcs = ["src/test/scala/io/github/meisam/photorg/MockCommandInterpreter.scala"],
    deps = [
        ":dry_run_interpreter_lib",
        ":photorg_lib",
        ":scala_cats_deps",  # MockCommandInterpreter uses cats
        ":scala_test_deps",
    ],
)

scala_library(
    name = "scala_test_deps",
    exports = [
        "@maven//:org_scalacheck_scalacheck_3_1_18_1",
        "@maven//:org_scalatest_scalatest_3_3_2_19",
        "@maven//:org_scalatestplus_scalacheck_1_18_3_3_2_19_0",
    ],
)
