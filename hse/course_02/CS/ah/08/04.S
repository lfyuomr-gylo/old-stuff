/*
stack: |     |     | ebp |  *  |     |     |
                      ^    ret
                     ebp
                     eax
*/

        .text

msg:    .asciz  "0x%08x\n"

        .global printstack

printstack:
        push    %ebp
        mov     %esp, %ebp

        // caching registers
        push    %edi

        mov     %ebp, %edi

rec:
        pushl   %edi
        pushl   $msg
        call    printf
        addl    $8, %esp

        mov     (%edi), %edi
        test    %edi, %edi
        jnz     rec

        // restoring registers
        pop     %edi

        mov     %ebp, %esp
        pop     %ebp
        ret
