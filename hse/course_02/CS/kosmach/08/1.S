#include <asm/unistd_32.h>
    .data                   
    c: .byte   0            
    .text                   
    .global main

main:
    movl    $__NR_read, %eax
    movl    $0, %ebx        
    movl    $c, %ecx        
    movl    $1, %edx        
    int     $0x80
    cmpl    $0, %eax
    jz end
    
    movl    $__NR_write, %eax
    movl    $1, %ebx
    movl    $c, %ecx
    movl    $1, %edx
    int     $0x80
    jmp main

end:

    movl    $__NR_exit, %eax
    xorl    %ebx, %ebx
    int     $0x80
