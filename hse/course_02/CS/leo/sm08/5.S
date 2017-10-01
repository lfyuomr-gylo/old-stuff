        .data
outform:
        .string "%d\n"

        .text
        .global immadd
immadd:
        pushl %ebp
        movl %esp, %ebp

        movl 4(%ebp), %ebx
        movl (%ebx), %edx
        addl 4(%ebx), %edx

    // print result
        pushl %edx
        pushl $outform
        call printf
        addl $8, %esp
    
    // adjust return execution point
        addl $8, 4(%ebp)

        movl %ebp, %esp
        popl %ebp
        ret
/*
        .global main
main:
        pushl %ebp
        movl %esp, %ebp

        call immadd
        .int 1
        .int 2

        pushl $7
        pushl $outform
        call printf
        addl $8, %esp

        movl %ebp, %esp
        popl %ebp
        ret
*/
