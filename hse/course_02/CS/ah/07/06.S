#include "simpleio.S"
        .text
        .global main
main:
        call    readi32
        xorl    %eax, %eax
        xorl    %esi, %esi

        call    readi32
        jnc     loop
        call    finish
loop:
        movl    %eax, arr(,%esi,4)
        incl    %esi
        call    readi32
        jnc     loop

loop2:
        decl    %esi
        movl    arr(,%esi,4), %eax
        call    writei32
        call    nl

        cmpl    $0, %esi
        jnz     loop2

        call    finish

        .data
arr:    .space  64 * 4, 0
