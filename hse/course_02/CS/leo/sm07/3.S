#include "simpleio.S"
    .text
    .global main

main:
    xorl %ebx, %ebx
    xorl %eax, %eax

    call readi32
    jc output
loop:
    addl %eax, %ebx
    call readi32
    jnc loop
output:
    movl %ebx, %eax
    call writei32
    call nl
    call finish
