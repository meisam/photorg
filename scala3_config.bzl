load("//scala:scala_cross_version.bzl", "extract_major_version")

def _default_scala_version():
    """return the scala version for use in maven coordinates"""
    return "3.2.0"

def _store_config(repository_ctx):
    scala_version = repository_ctx.os.environ.get(
        "SCALA_VERSION",
        repository_ctx.attr.scala_version,
    )

    scala_major_version = extract_major_version(scala_version)

    config_file_content = "\n".join([
        "SCALA_VERSION='" + scala_version + "'",
        "SCALA_MAJOR_VERSION='" + scala_major_version + "'",
    ])

    repository_ctx.file("config.bzl", config_file_content)
    repository_ctx.file("BUILD")

_config_repository = repository_rule(
    implementation = _store_config,
    attrs = {
        "scala_version": attr.string(
            mandatory = True,
        ),
    },
    environ = ["SCALA_VERSION"],
)

def scala_config(scala_version = _default_scala_version()):
    _config_repository(
        name = "io_bazel_rules_scala_config",
        scala_version = scala_version,
    )