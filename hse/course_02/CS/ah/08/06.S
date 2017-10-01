#define EOF -1

        .data
main.msg:
        .asciz  "%d\n"

        .text
        .global main
main:
        push    $0
main.rec:
        call    getchar

        cmp     $EOF, %eax  // == EOF
        jz      main.exit
        cmp     $32, %eax   // <= 32
        jle     main.rec
        cmp     $48, %eax   // == 48
        jz      main.call_func
        cmp     $49, %eax   // == 49
        jz      main.call_func
        cmp     $50, %eax   // == 50
        jz      main.call_func
        cmp     $51, %eax   // == 51
        jz      main.call_func

        push    %eax
        push    $main.msg
        call    printf
        add     $8, %esp
        jmp     main.rec
main.call_func:
        call    call_func
        jmp     main.rec
main.exit:
        pop     %eax
        xor     %eax, %eax
        ret

        .global call_func
call_func:
        sub     $48, %eax
        mov     call_func.jmptab(,%eax,4), %eax
        jmp     *%eax

call_func.jmptab:
        .int    call_func.D1, call_func.D2, call_func.D3, call_func.D4

call_func.D1:
        call    func0
        ret
call_func.D2:
        call    func1
        ret
call_func.D3:
        call    func2
        ret
call_func.D4:
        call    func3
        ret
