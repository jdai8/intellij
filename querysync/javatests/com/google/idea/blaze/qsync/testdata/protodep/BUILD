load("@protobuf//bazel:java_lite_proto_library.bzl", "java_lite_proto_library")
load("@protobuf//bazel:proto_library.bzl", "proto_library")
load("@rules_java//java:defs.bzl", "java_library")

proto_library(
    name = "proto",
    srcs = ["testproto.proto"],
)

java_lite_proto_library(
    name = "proto_java_proto_lite",
    deps = [":proto"],
)

java_library(
    name = "protodep",
    srcs = ["TestClassProtoDep.java"],
    deps = [":proto_java_proto_lite"],
)
