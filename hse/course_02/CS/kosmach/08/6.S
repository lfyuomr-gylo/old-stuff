    .data
f_s: .asciz "%d\n"
    .text
    .global main

main:
    pushl %ebp
    movl %esp, %ebp

input:
    calll getchar
    cmpl $-1, %eax
    jz end // EOF
    cmpl $32, %eax
    jle input // <= 32
    cmpl $47, %eax
    jle norm// 33 <= eax <= 47
    cmpl $51, %eax // 48 <= eax <= 51
    jle tabb
    jmp norm

norm:
    calll l2
l2: popl %edx
    addl $f_s-l2, %edx
    pushl %eax
    pushl %edx
    calll printf
    addl $8, %esp
    jmp input

tabb:
    subl $48, %eax
    calll l1
l1: popl %edx
    addl $jmptab-l1, %edx
    movl (%edx, %eax, 4), %ebx
    calll *%ebx
    jmp input

end:
    movl %ebp, %esp
    popl %ebp
    xorl %eax, %eax
    ret


jmptab: .int func0, func1, func2, func3

