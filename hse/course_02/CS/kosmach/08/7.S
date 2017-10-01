    .data
pformat_string: .asciz "%d\n"
sformat_string: .asciz "%d"
    .global func
func:
    pushl %ebp
    movl %esp, %ebp

    pushl %ebx
    pushl %esi
    pushl %edi
    subl $4, %esp // -16(%ebp) = N    
    // -4(ebp) = ebx
    // -8(ebp) = esi
    // -12(ebp) = edi
    movl %esp, %esi
    pushl %esi
    pushl $sformat_string
    calll scanf
    addl $8, %esp
    //movl -4(%ebp) = N

    xorl %edi, %edi
input:
    cmpl %edi, -16(%ebp)
    jz compute
    subl $4, %esp
    movl %esp, %esi
    pushl %esi
    pushl $sformat_string
    calll scanf
    addl $8, %esp
    incl %edi
    jmp input

compute:
    movl %ebp, %esp
    subl $20, %esp
    xorl %edi, %edi
    subl $1, -16(%ebp)
compute_loop:
    cmpl %edi, -16(%ebp)
    jz output
    movl -4(%esp), %esi
    addl %esi, (%esp)
    subl $4, %esp
    incl %edi
    jmp compute_loop
    
output:
    movl %ebp, %esi
    subl $20, %esi
    subl $4, %esp
    xorl %edi, %edi
output_loop:
    cmpl %edi, -16(%ebp)
    jz end
    pushl (%esi)
    pushl $pformat_string
    calll printf
    addl $8, %esp
    subl $4, %esi
    incl %edi
    jmp output_loop

end:
    movl %ebp, %esp
    subl $12, %esp
    popl %edi
    popl %esi
    popl %ebx
    popl %ebp
    ret
