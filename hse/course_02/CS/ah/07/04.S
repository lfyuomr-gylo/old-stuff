#include "simpleio.S"
        .text
        .global main
main:
        xorl    %eax, %eax
        xorl    %edx, %edx
        xorl    %esi, %esi
        xorl    %edi, %edi
loop:
        addl    %eax, %esi
        adcl    %edx, %edi
        call    readi64
        jnc     loop
        movl    %esi, %eax
        movl    %edi, %edx
        call    writei64
        call    nl
        call    finish
