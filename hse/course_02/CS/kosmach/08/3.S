    .global sum

sum:
    pushl %ebp
    movl %esp, %ebp
    xorl %ecx, %ecx
    addl 8(%ebp), %ecx
    addl 12(%ebp), %ecx
    movl 16(%ebp), %edx
    jc over
    movl %ecx, (%edx)
    jmp end_of_f
    
end_of_f:
    movl %ebp, %esp
    popl %ebp
    ret

over:
    movl $4294967295, (%edx)
    jmp end_of_f
