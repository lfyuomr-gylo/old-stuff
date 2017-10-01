#include "simpleio.S"
    .text
    .global main

main:
    xorl %ebx, %ebx
    xorl %esi, %esi

    xorl %edx, %edx
    xorl %eax, %eax

    call readi64
    jc output
loop:
    addl %edx, %esi
    addl %eax, %ebx
    jnc nooverflow
    incl %esi

nooverflow:
    call readi64
    jnc loop
output:
    movl %ebx, %eax
    movl %esi, %edx
    call writei64
    call nl
    call finish
