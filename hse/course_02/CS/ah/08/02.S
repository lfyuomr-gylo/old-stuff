        .text

msg:    .asciz  "Process: %d, %d, %d\n"

        .global main
main:
        call    getuid          // getuid()
        movl    %eax, %esi

        call    getgid          // getgid()
        movl    %eax, %edi

        xor     %eax, %eax
        push    %eax
        call    umask           // umask(0)
        addl    $4, %esp

        pushl   %eax
        pushl   %edi
        pushl   %esi
        pushl   $msg
        call    printf          // printf(msg, a, b, c)
        addl    $16, %esp

        xor     %eax, %eax
        push    %eax
        call    exit
