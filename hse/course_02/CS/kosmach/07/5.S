#include "simpleio.S"
    .text
    .global main

main:
    xorl %ebx, %ebx /* четные */
    xorl %edi, %edi /* нечетные */

loop:
    calll readi32
    jc end
    testl $1, %eax
    jnz addn2
    jz add2
    
add2:
    addl %eax, %ebx
    jmp loop

addn2:
    addl %eax, %edi
    jmp loop

end:
    movl %ebx, %eax
    calll writei32
    calll nl
    movl %edi, %eax
    calll writei32
    calll nl
    calll finish

