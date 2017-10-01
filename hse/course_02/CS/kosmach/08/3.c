#include <stdio.h>

extern void sum(unsigned v1, unsigned v2, unsigned *vr);

int main() {
    unsigned a, b, c;
    a = 5151;
    b = 0;
    b = ~0;
    b-= 10000;
    printf("%u\n", b);
    sum(a, b, &c);
    printf("%u\n", c);
    return 0;
}
