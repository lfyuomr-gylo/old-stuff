#include "simpleio.S"
    .text
    .global main
// %ebx, %esi, %edi -- stable
// %ebx -- N stored
// %edi -- counter
// %esi -- array pointer
main:
    call readi32
    testl %eax, %eax
    jz end

    movl %eax, %ebx
    movl $0, %edi
    movl $arr, %esi

readloop:
    cmpl %edi, %ebx
    jz finishread
    call readi32
    movl %eax, (%esi, %edi, 4)
    incl %edi
    jmp readloop

finishread:
    movl %ebx, %edi
    jmp writeloop

writeloop:
    testl %edi, %edi
    jz end

    decl %edi
    movl (%esi, %edi, 4), %eax
    incl %edi
    
    call writei32
    call nl
    decl %edi
    jmp writeloop

end:
    call finish
//---------------------
    .data
arr: .space 64 * 4, 0
