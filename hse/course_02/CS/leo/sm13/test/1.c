#include <stdio.h>
#include "intc.h"

extern void func();

int func1(int n) {
    printf("%d", n);
}

int main() {
    return 0;
}
