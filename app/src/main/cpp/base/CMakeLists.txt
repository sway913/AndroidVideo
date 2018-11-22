
cmake_minimum_required(VERSION 3.4.1)

aux_source_directory(${CMAKE_CURRENT_SOURCE_DIR}  BASE_SRC_LIST)

include_directories(${CMAKE_CURRENT_SOURCE_DIR})

find_library( log-lib
            log )

find_library( android-lib
        android )

add_library(
             base
             SHARED
             ${BASE_SRC_LIST} )



target_link_libraries( base
                    ${log-lib}
                    ${android-lib}
                    )
