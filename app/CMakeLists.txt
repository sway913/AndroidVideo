
cmake_minimum_required(VERSION 3.4.1)


set(OPENGL_DIR ${CMAKE_SOURCE_DIR}/src/main/cpp/opengl)
add_subdirectory(${OPENGL_DIR})
include_directories(${OPENGL_DIR}/include)

add_library(
        native-lib
        SHARED
        src/main/cpp/native-lib.cpp)


target_link_libraries(
        native-lib
        openglcore
        )