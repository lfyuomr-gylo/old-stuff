    .text
format_string: .asciz "0x%08x\n"
    .global printstack

printstack:
    movl %ebp, %esi
    movl %esp, %edi
    movl (%esp), %ebx
    pushl %ebx
    pushl $format_string
    calll printf
    addl $8, %esp

go_deeper:
    cmpl $0, %ebp
    jz root
    jmp pr

continue:
    movl (%ebp), %ebp
    jmp go_deeper

pr:
    pushl 4(%ebp)
    pushl $format_string
    calll printf
    addl $8, %esp
    jmp continue

root:
    movl %esi, %ebp
    movl %edi, %esp
    ret
