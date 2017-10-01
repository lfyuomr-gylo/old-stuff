#include <iostream>

int main() {
    union {
        float f;
        struct {
            unsigned mantissa : 32 - 8 - 1;
            unsigned exp : 8;
            unsigned sign : 1;
        } bf;
    };
    while (std::cin >> f) {
        std::cout << bf.sign << " " << bf.exp << " ";
        std::cout.setf(std::ios::hex, std::ios::basefield);
        std::cout << bf.mantissa << std::endl;
        std::cout.setf(std::ios::dec, std::ios::basefield);
    }
    return 0;
}
