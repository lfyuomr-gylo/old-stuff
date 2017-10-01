        .data
rdform: .string "%d"
prform: .string "%d\n"

        .text
        .global func
func:
    // standart prologue
        pushl %ebp
        movl %esp, %ebp

    // save registers values before modifications
        pushl %ebx
        pushl %edi
        pushl %esi
        
        subl $4, %esp   //
        movl %esp, %ebx // allocate memory for buffer and
        subl $4, %esp   // assign buffer address to ebx
    // ------------
        
    // read N to edi
        pushl %ebx
        pushl $rdform
        call scanf
        addl $8, %esp 
        movl (%ebx), %edi

        testl %edi, %edi // check if N == 0
        jz finish

    // read first element
        pushl %ebx
        pushl $rdform
        call scanf
        addl $8, %esp

        decl %edi
        subl $4, %ebx
while:
        testl %edi, %edi
        jz finish
    
    // read element
        pushl %ebx
        pushl $rdform
        call scanf
        addl $8, %esp

    // calculate current sum in ecx
        movl (%ebx), %ecx
        addl $4, %ebx
        addl (%ebx), %ecx
        subl $4, %ebx

    // print sum
        pushl %ecx
        pushl $prform
        call printf
        addl $8, %esp

    // move a[i + 1] --> a[i]
        movl (%ebx), %ecx
        addl $4, %ebx
        movl %ecx, (%ebx)
        subl $4, %ebx
       
        decl %edi
        jmp while        
    // -----------

finish:
        addl $8, %esp // free allocated memory

    // restore modofied registers values
        popl %esi
        popl %edi
        popl %ebx

    // standard epilogue
        movl %ebp, %esp
        popl %ebp
        ret
        
