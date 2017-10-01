#include <asm/unistd_32.h>
    .data                   
format_string: .asciz   "Process: %d, %d, %d\n"            
    .text                   
    .global main

main:
    pushl $0
    calll umask
    addl $4, %esp
    pushl %eax
    calll getgid
    pushl %eax
    calll getuid
    pushl %eax
    pushl $format_string
    calll printf
    addl $16, %esp
    pushl $0
    calll exit
