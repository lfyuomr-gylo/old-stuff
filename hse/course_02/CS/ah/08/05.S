        .text
immadd.msg:
        .asciz  "%d\n"
        .global immadd
immadd:
        push    %ebp
        mov     %esp, %ebp

        mov     4(%ebp), %edx
        mov     4(%edx), %eax
        add     0(%edx), %eax

        push    %eax
        push    $immadd.msg
        call    printf
        add     $8, %esp

        add     $8, 4(%ebp)

        mov     %ebp, %esp
        pop     %ebp
        ret


//#define RUN_TESTS
#ifdef RUN_TESTS

.globl main

main:
        call    immadd
        .int    0
        .int    0

        call    immadd
        .int    5
        .int    10

        call    immadd
        .int    100
        .int    200

        call    immadd
        .int    -10
        .int    20
        
        xor     %eax, %eax
        push    %eax
        call    exit
#endif
