#include "simpleio.S"
    .text
    .global main

main:
    calll readi32
    cmpl $0, %eax
    jz empty
    movl %eax, %edi /* edi = N */
    xorl %esi, %esi
    
loop:
    calll readi32
    pushl %eax
    incl %esi
    cmpl %esi, %edi
    jnz loop

    xorl %esi, %esi
    
end:
    popl %eax
    calll writei32
    calll nl
    incl %esi
    cmpl %esi, %edi
    jnz end
    calll finish

empty:
    calll finish
