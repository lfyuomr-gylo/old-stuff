#include "simpleio.S"
    .text
    .global main

main:
    xorl %ebx, %ebx

loop:
    calll readi32
    jc end
    addl %eax, %ebx
    jmp loop

end:
    movl %ebx, %eax
    calll writei32
    calll nl
    calll finish
