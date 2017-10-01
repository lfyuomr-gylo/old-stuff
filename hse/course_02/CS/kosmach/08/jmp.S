    .data
f: .asciz "%d\n"
    .text
    .global main
    
main:
    movl $3, %ebx
    calll l1
l1: popl %edx
    addl $jmptab-l1, %edx
    movl (%edx, %ebx, 4), %eax
    jmp *%eax

jmptab: .int D0, D1, D2
    
D0:
    pushl $0
    pushl $f
    calll printf
    addl $8, %esp
    jmp ov

D1:
    pushl $2
    pushl $f
    calll printf
    addl $8, %esp
    jmp ov

D2:
    pushl $5
    pushl $f
    calll printf
    addl $8, %esp
    jmp ov

ov:
    pushl $0
    calll exit
