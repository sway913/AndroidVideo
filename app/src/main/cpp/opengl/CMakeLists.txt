
cmake_minimum_required(VERSION 3.4.1)

add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/../base base)

aux_source_directory(${CMAKE_CURRENT_SOURCE_DIR}  OPENGL_SRC_LIST)

include_directories(${CMAKE_CURRENT_SOURCE_DIR})
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/../base/include)

find_library( EGL-lib
        EGL )

find_library( GLESv2-lib
        GLESv2 )

add_library(
             openglcore
             SHARED
             ${OPENGL_SRC_LIST} )


target_link_libraries( openglcore
                        base
                        ${EGL-lib}
                        ${GLESv2-lib}
                    )
