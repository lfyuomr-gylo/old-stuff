#include "simpleio.S"
    .text
    .global main

main:
    calll readi32
    movl %eax, %ebx
    calll readi32
    imull %ebx
    movl %eax, %edi
    calll readi32
    movl %eax, %ebx
    calll readi32
    imull %ebx
    negl %eax
    addl %edi, %eax
    calll writei32
    calll nl
    calll finish
    
