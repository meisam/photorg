load("@rules_scala//scala:scala.bzl", "scala_library", "scala_binary")

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
        "@maven//:org_typelevel_cats_free_3_2_9_0",
        "@maven//:org_typelevel_cats_core_3_2_9_0",
        "@maven//:org_typelevel_cats_kernel_3_2_9_0",
        "@maven//:com_monovore_decline_3_2_4_1",
    ],
)