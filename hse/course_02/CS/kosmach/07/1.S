#include "simpleio.S"
    .text
    .global main

main:
    calll readi32
    movl %eax, %ebx
    calll readi32
    addl %ebx, %eax
    calll writei32
    calll nl
    calll finish
