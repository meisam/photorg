# WORKSPACE
load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

skylib_version = "1.4.1"
http_archive(
    name = "bazel_skylib",
    sha256 = "b8a1527901774180afc798aeb28c4634bdccf19c4d98e7bdd1ce79d1fe9aaad7",
    type = "tar.gz",
    url = "https://mirror.bazel.build/github.com/bazelbuild/bazel-skylib/releases/download/{}/bazel-skylib-{}.tar.gz".format(skylib_version, skylib_version),
)

http_archive(
    name = "io_bazel_rules_scala",
    url = "https://github.com/meisam/rules_scala/releases/download/2023-02-20-scala3.2-support/rules_scala-5.0.0-2023-02-20-scala3.2-support.tar.gz",
    type = "tar.gz",
    sha256 = "c19bcd0f07ab64b5dc9ec2172d471746edb4b3990c9d401e9c9771ee40c6c21d",
)

load("@io_bazel_rules_scala//:scala_config.bzl", "scala_config")
scala_config(scala_version = "3.3.6")

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
RULES_JVM_EXTERNAL_TAG = "4.5"
RULES_JVM_EXTERNAL_SHA = "b17d7388feb9bfa7f2fa09031b32707df529f26c91ab9e5d909eb1676badd9a6"

http_archive(
    name = "rules_jvm_external",
    strip_prefix = "rules_jvm_external-%s" % RULES_JVM_EXTERNAL_TAG,
    sha256 = RULES_JVM_EXTERNAL_SHA,
    url = "https://github.com/bazelbuild/rules_jvm_external/archive/refs/tags/%s.zip" % RULES_JVM_EXTERNAL_TAG,
#    url = "https://github.com/bazelbuild/rules_jvm_external/archive/%s.zip" % RULES_JVM_EXTERNAL_TAG,
)

load("@rules_jvm_external//:repositories.bzl", "rules_jvm_external_deps")

rules_jvm_external_deps()

load("@rules_jvm_external//:setup.bzl", "rules_jvm_external_setup")

rules_jvm_external_setup()

load("@rules_jvm_external//:defs.bzl", "maven_install")

maven_install(
    artifacts = [
        "org.typelevel:cats-free_3:2.9.0",
        "org.typelevel:cats-core_3:2.9.0",
        "org.typelevel:cats-kernel_3:2.9.0",
        "org.typelevel:cats-kernel_3:2.9.0",
        "com.monovore:decline_3:2.4.1",
    ],
    repositories = [
        "https://repo1.maven.org/maven2",
    ],
)
