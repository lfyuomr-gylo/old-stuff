    .data
f: .asciz "%d\n"
    .global main

main:
    calll immadd
    .int -6
    .int 7
    pushl $5
    pushl $f
    calll printf
    addl $8, %esp
    
    pushl $0
    calll exit
