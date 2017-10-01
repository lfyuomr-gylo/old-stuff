#include <bits/stdc++.h>

uint32_t BE32Num(char* bytes) {
    union {
        uint32_t result;
        uint8_t bts[4];
    };
    bts[0] = bytes[3];
    bts[1] = bytes[2];
    bts[2] = bytes[1];
    bts[3] = bytes[0];
    return result;
}

uint64_t FileSum(std::istream& is) {
    char bytes[4];
    uint64_t sum = 0;
    for (;;) {
        is.read(bytes, 4);
        if (is.eof())
            break;

        sum += BE32Num(bytes);
    }

    return sum;
}

int main(int argc, char* argv[]) {
    std::ifstream is;
    for (int i = 1; i < argc; i++) {
        if (!strcmp(argv[i], "-")) {
            std::cout << FileSum(std::cin) << std::endl;
        }
        else {
            is.open(argv[i], std::ios::binary);
            std::cout << FileSum(is) << std::endl;
            is.close();
        }
    }

    return 0;
}
