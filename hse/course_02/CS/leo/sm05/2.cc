#include <bits/stdc++.h>
#include <cstdint>

union FloatBitsAccess {
    float f;
    uint32_t i;
};

bool GetFloatSgn(float f) {
    FloatBitsAccess tr;
    tr.f = f;
    return tr.i >> 31;
}

uint32_t GetFloatExp(float f) {
    FloatBitsAccess tr;
    tr.f = f;
    return (tr.i >> 23) & ((1 << 8) - 1);
}

uint32_t GetFloatFrac(float f) {
    FloatBitsAccess tr;
    tr.f = f;
    return (tr.i & ((1 << 23) - 1));
}

int main() {
    float current = 0.0;
    //std::cout.setf(std::ios::showbase);
    while (std::cin >> current) {
        std::cout << GetFloatSgn(current) << ' ' << GetFloatExp(current) << ' ';
        std::cout.setf(std::ios::hex, std::ios::basefield);
        std::cout << GetFloatFrac(current) << std::endl;
        std::cout.setf(std::ios::dec, std::ios::basefield);
    }
    return 0;
}
