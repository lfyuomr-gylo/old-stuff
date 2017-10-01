        .global main
main:
        pushl %ebp
        movl %esp, %ebp

        call immadd
        .int 1
        .int 2

        movl %ebp, %esp
        popl %ebp
        ret

