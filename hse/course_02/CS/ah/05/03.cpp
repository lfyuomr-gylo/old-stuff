/*
#include <iostream>

enum class FPClass {
    ZERO, DENORMALIZED, NORMALIZED, INF, NAN
};
*/

FPClass fpclassf(float value, bool &psign) {
    union {
        float f;
        struct {
            unsigned mantissa : 32 - 8 - 1;
            unsigned exp : 8;
            unsigned sign : 1;
        } bf;
    };

    f = value;

    psign = bf.sign;

    if (bf.exp == 0b00000000 && bf.mantissa == 0b00000000) {
        return FPClass::ZERO;
    } else if (bf.exp == 0b00000000 && bf.mantissa >= 0b00000001) {
        return FPClass::DENORMALIZED;
    } else if (bf.exp >= 0b00000001 && bf.exp <= 0b11111110) {
        return FPClass::NORMALIZED;
    } else if (bf.exp == 0b11111111 && bf.mantissa == 0b00000000) {
        return FPClass::INF;
    }

    psign = 0;
    
    return FPClass::NAN;
}

/*
int main() {
    float f, g;
    bool sign = 0;
    while (std::cin >> f >> g) {
        std::cout << (f / g) << std::endl;
        std::cout << int(fpclassf(f / g, sign)) << ' ' << sign << std::endl;
    }
    return 0;
}
*/
