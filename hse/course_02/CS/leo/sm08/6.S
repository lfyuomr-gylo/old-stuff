#include <asm/unistd_32.h>
        .data
outstr: .string "%d\n"

        .text
        .global main
main:
        pushl %ebp
        movl %esp, %ebp

while:
        call getchar
        cmpl $-1, %eax
        jz finish

        cmpl $33, %eax
        jl while

        cmpl $48, %eax
        jl prnt

        cmpl $52, %eax
        jl funcall

        jmp prnt

prnt:
        call prntnum
prntnum:
        popl %ebx
        addl $outstr-prntnum, %ebx

        pushl %eax
        pushl %ebx
        call printf
        addl $8, %esp

        jmp while
    //------


funcall:
        call cfunc
cfunc:
        popl %ebx
        addl $ftable-cfunc, %ebx
        subl $48, %eax
        //movl (%ebx, %eax, 4), %ebx
        mull $4
        addl %eax, %ebx
        call *%ebx
        jmp while
    //------


finish:
        movl %ebp, %esi
        popl %ebp
        xorl %eax, %eax
        ret
//-------------------------------------------



ftable: .int func0, func1, func2, func3

func0:
        pushl $1
        pushl $outstr
        call printf
        addl $8, %esp

        ret

func1:
        pushl $7
        pushl $outstr
        call printf
        addl $8, %esp

        ret

func2:
        pushl $14
        pushl $outstr
        call printf
        addl $8, %esp

        ret

func3:
        pushl $49
        pushl $outstr
        call printf
        addl $8, %esp

        ret
