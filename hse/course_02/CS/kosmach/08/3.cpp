#include <stdio.h>

extern void sum(unsigned v1, unsigned v2, unsigned *vr);

int main() {
    unsigned a = 23, b = 42, c = 0;
    sum(a, b, &c);
    printf("%u", c);
    return 0;
}
