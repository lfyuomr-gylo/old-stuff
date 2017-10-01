#include <asm/unistd_32.h>
        .data
c:      .byte   0

        .text
        .global main
main:
        movl    $__NR_read, %eax
        movl    $0, %ebx        // 0 = stdin
        movl    $c, %ecx        // &c
        movl    $1, %edx        // 1 = len(c)
        int     $0x80

        cmpl    $0, %eax  // if no input
        jz      end       // exit

        movl    $__NR_write, %eax
        movl    $1, %ebx        // 0 = stdout
        movl    $c, %ecx        // &c
        movl    $1, %edx        // 1 = len(c)
        int     $0x80

        jmp     main

end:
        movl    $__NR_exit, %eax
        xorl    %ebx, %ebx
        int     $0x80
