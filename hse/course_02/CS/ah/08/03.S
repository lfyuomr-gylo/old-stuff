        .text
        .global sum
sum:
        pushl   %ebp
        movl    %esp, %ebp

        movl    8(%ebp), %eax
        addl    12(%ebp), %eax

        jnc     return          // no saturation

        xorl     %eax, %eax      // saturation
        notl    %eax

return:

        movl    16(%ebp), %edx
        movl    %eax, (%edx)    // returning result

        xorl    %eax, %eax
        xorl    %edx, %edx

        movl    %ebp, %esp
        popl    %ebp
        ret
