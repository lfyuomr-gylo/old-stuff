#include <iostream>
#include <inttypes.h>

int check(uint32_t num) {
    if (!num)
        return 1;

    int f = 0, l = 31;
    while (!((num >> f) & 1))
        ++f;
    while (!((num >> l) & 1))
        --l;
    return (l - f + 1) <= 24;
}

int main() {
    uint32_t num = 0;
    while (std::cin >> num) {
        std::cout << check(num) << std::endl;
    }
}
