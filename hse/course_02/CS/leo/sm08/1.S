#include <asm/unistd_32.h>
        .data
symb:   .byte 0

        .text
        .global main
main:

rrloop:
        call readc
        testl %eax, %eax
        jz finish
        call writec
        jmp rrloop

finish:
        movl $__NR_exit, %eax
        xorl %ebx, %ebx
        int $0x80

readc:
        movl $__NR_read, %eax
        movl $0, %ebx
        movl $symb, %ecx
        movl $1, %edx
        int $0x80
        ret
writec:
        movl $__NR_write, %eax
        movl $1, %ebx
        movl $symb, %ecx
        movl $1, %edx
        int $0x80
        ret

