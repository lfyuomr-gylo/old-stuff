#include <stdio.h>


extern void printstack(void);


void f2(void)
{
    printstack();
}


void f1(void)
{
    f2();
    printf("---\n");
    printstack();
}


int main(void)
{
    int i = 0;
    f1();
    for (; i < 3; i++) {
        printf("---\n");
        printstack();
    }
    return 0;
}
