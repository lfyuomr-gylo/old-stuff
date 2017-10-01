#include <bits/stdc++.h>

int Pos32Bits(unsigned long num) {
    int result = 0;
    for (int i = 1; num; i++) {
        if (i > 32)
            throw std::overflow_error("num is greater than 2^32");
        result += num & 1;
        num = num >> 1;
    }
    return result;
}

int NumProc(std::string snum) {
    try {
        size_t numlen = 0;
        unsigned long num = std::stoul(snum, &numlen, 10);
        if (numlen != snum.size())
            throw std::invalid_argument("snum is not a number");
        return Pos32Bits(num);
    }
    catch (...) {
        return -1;
    }
}

int main(int argc, char *argv[]) {
    for (int i = 1; i < argc; i++) {
        int result = NumProc(argv[i]);
        std::cout << result << std::endl;
    }
    return 0;
}
