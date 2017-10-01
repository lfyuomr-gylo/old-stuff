#include "simpleio.S"
    .text
    .global main

main:
    call readi32
    movl %eax, %ebx
    call readi32
    imull %ebx
    movl %eax, %ebx
    call readi32
    movl %eax, %esi
    call readi32
    imull %esi
    subl %eax, %ebx
    movl %ebx, %eax
    call writei32
    call nl
    call finish
