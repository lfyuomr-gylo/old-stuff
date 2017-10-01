        .text

read.input:
        .asciz  "%d"

read:
        push   %ebp
        mov    %esp, %ebp

        sub     $4, %esp
        mov     %ebp, %eax
        sub     $4, %eax

        push    %eax
        push    $read.input
        call    scanf
        add     $8, %esp

        mov     -4(%ebp), %eax
        
        mov     %ebp, %esp
        pop     %ebp
        ret

func.output:
        .asciz  "%d\n"

        .global func
func:
        push   %ebp
        mov    %esp, %ebp

        //      -4: N
        //      -8: current
        //      -12: a[current]
        //      -16: a[current - 1]
        call    read
        push    %eax
        push    $0
        push    $0
        push    $0

func.rec:
        mov     -8(%ebp), %edx
        inc     %edx
        mov     %edx, -8(%ebp)
        mov     -4(%ebp), %eax
        cmp     %eax, %edx      // current > N => end
        jg      func.rec_end

        mov     -12(%ebp), %eax
        mov     %eax, -16(%ebp)
        call    read
        mov     %eax, -12(%ebp)

        cmp     $1, -8(%ebp)    // current == 1
        jz      func.rec

        mov     -12(%ebp), %eax
        add     -16(%ebp), %eax

        push    %eax
        push    $func.output
        call    printf
        add     $8, %esp

        jmp     func.rec

func.rec_end:
        
        mov     %ebp, %esp
        pop     %ebp
        ret

//#define RUN_TESTS
#ifdef RUN_TESTS

.globl main

main:
        call    func
        xor     %eax, %eax
        ret
#endif
