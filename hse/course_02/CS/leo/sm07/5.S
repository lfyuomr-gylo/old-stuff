#include "simpleio.S"
    .text
    .global main

main:
    xorl %esi, %esi
    xorl %edi, %edi

loop:    
    call readi32
    jc prres
    testl $1, %eax
    jz addeven
    jnz addodd

addeven:
    addl %eax, %esi
    jmp loop

addodd:
    addl %eax, %edi
    jmp loop

prres:
    movl %esi, %eax
    call writei32
    call nl
    movl %edi, %eax
    call writei32
    call nl
