#include <stdio.h>

extern void printstack();


void world() {
    printstack();
}

void hello() {
    world();
}

int main() {
    hello();
    return 0;
}
