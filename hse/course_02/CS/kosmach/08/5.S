    .data
format_string: .asciz   "%d\n"
    .global immadd

immadd:
    pushl %ebp
    movl %esp, %ebp
    
    xorl %ebx, %ebx
    movl 4(%ebp), %esi
    addl (%esi), %ebx
    addl 4(%esi), %ebx

    addl $8, %esi
    movl %esi, 4(%ebp)

    pushl %ebx
    pushl $format_string
    calll printf
    addl $8, %esp

    movl %ebp, %esp
    popl %ebp
    ret

