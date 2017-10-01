#include "simpleio.S"
        .text
        .global main
main:
        call    readi32
        movl    %eax, %esi  /* A -> esi */
        call    readi32
        imull   %esi        /* A * B -> eax */
        movl    %eax, %edi  /* A * B -> edi */

        call    readi32
        movl    %eax, %esi  /* C -> esi */
        call    readi32
        imull   %esi        /* C * D -> eax */

        subl    %eax, %edi

        movl    %edi, %eax
        call    writei32
        call    nl
        call    finish
