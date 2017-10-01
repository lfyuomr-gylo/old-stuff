#include "simpleio.S"
    .text
    .global main

main:
    xorl %ebx, %ebx
    xorl %edi, %edi

loop:
    calll readi64
    jc end
    addl %eax, %ebx
    adcl %edx, %edi
    jmp loop

end:
    movl %ebx, %eax
    movl %edi, %edx
    calll writei64
    calll nl
    calll finish
