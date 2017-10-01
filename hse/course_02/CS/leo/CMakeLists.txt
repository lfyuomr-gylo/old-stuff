cmake_minimum_required(VERSION 3.2)
project(leo)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++1y -Wall -pthread -O2")
set(SOURCES main.cpp sm17/1.cc sm17/2.cc sm17/3.cc sm17/4.cc)

add_executable(thith ${SOURCES} sm16/1.cc
                                sm16/2.cc
                                sm16/3.cc
                                sm15/2.cc
                                sm15/1.cc
                                sm14/4.cc
                                sm15/3.cc
                                sm15/4.cc
                                sm14/3.cc
                                sm14/2.cc
                                sm14/1.cc
                                sm13/6.cc
                                sm13/5.cc
                                sm13/4.c
                                sm13/3.c
                                sm13/2.c
                                sm13/1.cc
                                sm12/6.c
                                sm12/5.cc
                                sm12/4.c
                                sm12/3.c
                                sm12/2.c
                                sm12/1.c
                                sm11/2.c
                                sm11/1.cc
                                sm09/4.c
                                sm09/3.c
                                sm09/2.cc
                                sm09/1.cc
                                sm08/1.S
                                sm08/2.S
                                sm08/3.S
                                sm08/4.S
                                sm08/5.S
                                sm08/7.S
                                sm08/6.S
                                sm07/1.S
                                sm07/2.S
                                sm07/3.S
                                sm07/4.S
                                sm07/5.S
                                sm07/6.S
                                sm06/1.c
                                sm06/2.c
                                sm06/3.cc
                                sm06/4.c
                                sm05/1.cc
                                sm05/2.cc
                                sm05/3.cc
                                sm05/4.cc
                                sm05/4.c
                                sm04/1.cc
                                sm04/2.cc
                                sm04/3.cc
                                sm16/4.cc
                                sm14/5.cc
                                sm04/4.cc
                                sm18/1.cc
                                sm18/wrap.cc
                                sm18/2.cc
                                sm18/3.cc
                                sm18/4.cc sm19/2.cc sm19/3.cc sm20/1.cc  sm19/4.cc)
add_executable(20-2 sm20/2.cc)
