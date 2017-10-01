#include "simpleio.S"
        .text
        .global main
main:
        xorl    %eax, %eax
        xorl    %ebx, %ebx
        xorl    %esi, %esi
        xorl    %edi, %edi
loop:
        movl    $1, %edi
        andl    %eax, %edi
        jnz     skip
        addl    %eax, %ebx
        jmp     skip2
skip:
        addl    %eax, %esi
skip2:
        call    readi32
        jnc     loop
        movl    %ebx, %eax
        call    writei32
        call    nl
        movl    %esi, %eax
        call    writei32
        call    nl
        call    finish
