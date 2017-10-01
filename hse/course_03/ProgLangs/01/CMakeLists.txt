cmake_minimum_required(VERSION 3.5.1)
project(myQtBinding)

set(CMAKE_C_STANDARD 99)
set(CMAKE_CXX_STANDARD 11)

set(CMAKE_VERBOSE_MAKEFILE ON)

# ------------ Python
find_package(PythonLibs 3.5 REQUIRED)
include_directories(${PYTHON_INCLUDE_DIRS})
# ------------ /Python

# ------------ Qt5
set(CMAKE_INCLUDE_CURRENT_DIR ON)
set(CMAKE_AUTOMOC ON)

set(QT5_USED_MODULES Core Widgets)
find_package(Qt5 5.7 COMPONENTS ${QT5_USED_MODULES} REQUIRED)

# ------------ /Qt5

include_directories(headers)

set(WRAPPER_LIB_NAME widgets)
add_library(${WRAPPER_LIB_NAME} SHARED cxx/widgets.cc headers/widgets.h)
target_link_libraries(${WRAPPER_LIB_NAME} ${PYTHON_LIBRARIES})
qt5_use_modules(${WRAPPER_LIB_NAME} ${QT5_USED_MODULES})

add_executable(test01 tests/test1.c)
target_link_libraries(test01 ${WRAPPER_LIB_NAME})

add_executable(test02 tests/test2.c)
target_link_libraries(test02 ${WRAPPER_LIB_NAME})


set(PYLIB_NAME pywidgets)
add_library(${PYLIB_NAME} SHARED c/_pywidgets.c headers/_pywidgets.h)
target_link_libraries(${PYLIB_NAME} ${WRAPPER_LIB_NAME})

