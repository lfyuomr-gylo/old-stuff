        .global sum

sum:
        pushl %ebp
        movl %esp, %ebp

        movl 8(%ebp), %eax
        addl 12(%ebp), %eax
        jnc nooverflow
        call overflow
nooverflow:
        movl 16(%ebp), %edx
        movl %eax, (%edx)

        movl %ebp, %esp
        popl %ebp
        ret

overflow:
        movl $0xffffffff, %eax
        ret
