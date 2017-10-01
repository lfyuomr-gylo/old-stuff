        .data
outstr: .string "0x%08x\n"

        .global printstack
printstack:
        pushl %ebp
        movl %esp, %ebp
        movl %ebp, %ebx
while:
    // print return address for current function
        pushl 4(%ebx)
        pushl $outstr
        call printf
        addl $8, %esi

    // move previous function's %ebp to %ebx
        movl (%ebx), %ebx

    // the deepest frame reach condition
        testl %ebx, %ebx
        jnz while

        movl %ebp, %esp
        popl %ebp
        ret
