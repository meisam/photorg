# WORKSPACE
load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

skylib_version = "1.0.3"
http_archive(
    name = "bazel_skylib",
    sha256 = "1c531376ac7e5a180e0237938a2536de0c54d93f5c278634818e0efc952dd56c",
    type = "tar.gz",
    url = "https://mirror.bazel.build/github.com/bazelbuild/bazel-skylib/releases/download/{}/bazel-skylib-{}.tar.gz".format(skylib_version, skylib_version),
)

# rules_scala_version = "20220201"
# # rules_scala_version = "3dd5d8110d56cfc19722532866cbfc039a6a9612"
# http_archive(
#     name = "io_bazel_rules_scala",
#     sha256 = "77a3b9308a8780fff3f10cdbbe36d55164b85a48123033f5e970fdae262e8eb2",
#     strip_prefix = "rules_scala-%s" % rules_scala_version,
#     type = "zip",
#     url = "https://github.com/bazelbuild/rules_scala/archive/%s.zip" % rules_scala_version,
# )
local_repository(
    name = "io_bazel_rules_scala",
    path = "../rules_scala",
)

# Stores Scala version and other configuration
# 2.12 is a default version, other versions can be use by passing them explicitly:
load("@io_bazel_rules_scala//:scala_config.bzl", "scala_config")
scala_config(scala_version = "3.1.0")

load("@io_bazel_rules_scala//scala:scala.bzl", "scala_repositories")
scala_repositories()

load("@rules_proto//proto:repositories.bzl", "rules_proto_dependencies", "rules_proto_toolchains")
rules_proto_dependencies()
rules_proto_toolchains()

load("@io_bazel_rules_scala//scala:toolchains.bzl", "scala_register_toolchains")
# register_toolchains("//toolchains:scala3_jvm_toolchain")
scala_register_toolchains()

# # optional: setup ScalaTest toolchain and dependencies
# load("@io_bazel_rules_scala//testing:scalatest.bzl", "scalatest_repositories", "scalatest_toolchain")
# scalatest_repositories()
# scalatest_toolchain()

# Maven dependencies
RULES_JVM_EXTERNAL_TAG = "4.2"
RULES_JVM_EXTERNAL_SHA = "cd1a77b7b02e8e008439ca76fd34f5b07aecb8c752961f9640dea15e9e5ba1ca"

http_archive(
    name = "rules_jvm_external",
    strip_prefix = "rules_jvm_external-%s" % RULES_JVM_EXTERNAL_TAG,
    sha256 = RULES_JVM_EXTERNAL_SHA,
    url = "https://github.com/bazelbuild/rules_jvm_external/archive/%s.zip" % RULES_JVM_EXTERNAL_TAG,
)

load("@rules_jvm_external//:repositories.bzl", "rules_jvm_external_deps")

rules_jvm_external_deps()

load("@rules_jvm_external//:setup.bzl", "rules_jvm_external_setup")

rules_jvm_external_setup()

load("@rules_jvm_external//:defs.bzl", "maven_install")

maven_install(
    artifacts = [
        "org.typelevel:cats-free_3:2.8.0",
        "org.typelevel:cats-core_3:2.8.0",
    ],
    repositories = [
        "https://repo1.maven.org/maven2",
    ],
)
