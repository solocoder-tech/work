# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.
# 设置cmake最小版本
cmake_minimum_required(VERSION 3.4.1)
SET(CMAKE_C_COMPILER g++)
SET(CMAKE_BUILD_TYPE "Release")
add_compile_options(-std=c++11)


# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

#file(GLOB native_srcs "src/main/cpp/*.cpp")
# 配置so库信息
add_library( # Sets the name of the library.
             # 设置编译出来的so包的名字. 不需要添加lib前缀
             native-ld-video

             # Sets the library as a shared library.
             # 设置为共享链接库. 有SHARED，STATIC两种可选
             # STATIC:静态库，为目标文件的归档文件，在链接其他目标的时候使用
             # SHARED:动态库，会被动态链接，在运行时被加载
             SHARED

             # Provides a relative path to your source file(s).
             # 设置源文件的相对路径，可将多个源文件进行编译
             #${native_srcs}
             src/main/cpp/dewarpFishEyeImg.cpp
             src/main/cpp/native-lib-video.cpp
              )

# src/main/cpp/_ContourFinder.cpp
#src/main/cpp/_hough.cpp
#src/main/cpp/map_cut.cpp
#src/main/cpp/native-lib.cpp

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              # 设置外部引用库.这个库支持你在c/c++中打印log，例如查找系统中的log库liblog.so
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              # 外部引用库的名称
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
                       # 指定被链接的库.
                       native-ld-video

                       # Links the target library to the log library
                       # 链接log-lib到native-lib
                       # included in the NDK.
                       ${log-lib} )