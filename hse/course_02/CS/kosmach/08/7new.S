    .data
pformat_string: .asciz "%d\n"
sformat_string: .asciz "%d"
    .global func
func:
    pushl %ebp
    movl %esp, %ebp
    
    pushl %ebx // -4(%ebp)
    pushl %edi // -8(%ebp)
    pushl %esi // -12(%ebp)
    
    subl $4, %esp // -16(%ebp) = N
    movl %esp, %esi
    pushl %esi
    pushl $sformat_string
    calll scanf
    addl $8, %esp

    cmpl $1, -16(%ebp)
    jle end

    xorl %edi, %edi // counter

input:
    cmpl %edi, -16(%ebp)
    jz compute
    subl $4, %esp
    movl %esp, %esi
    pushl %esi
    pushl $sformat_string
    calll scanf
    add $8, %esp
    incl %edi // edi ++
    jmp input

compute:
    movl %ebp, %esp
    subl $20, %esp
    xorl %edi, %edi // counter
    subl $1, -16(%ebp) // N--
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
    popl %esi
    popl %edi
    popl %edx
    popl %ebp
    ret
