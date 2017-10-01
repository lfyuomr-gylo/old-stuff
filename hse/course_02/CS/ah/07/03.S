#include "simpleio.S"
        .text
        .global main
main:
        xorl    %eax, %eax
        xorl    %ebx, %ebx
loop:
        addl    %eax, %ebx
        call    readi32
        jnc     loop
        movl    %ebx, %eax
        call    writei32
        call    nl
        call    finish
