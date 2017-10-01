#include <asm/unistd_32.h>
        .data
outstr: .string "Process: %d, %d, %d\n"

        .text
        .global main
main:

finish:
        pushl $0
        call umask
        addl $4, %esp

        pushl %eax
        call getgid
        pushl %eax
        call getuid
        pushl %eax
        pushl $outstr

        call printf
        addl $16, %esp
        
        pushl $0
        call exit
