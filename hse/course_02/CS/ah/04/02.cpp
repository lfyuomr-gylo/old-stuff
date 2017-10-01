#include <iostream>
#include <algorithm>
#include <string>

int count(unsigned long i) {
    int o = 0;
    while (i) {
        o += (1 & i);
        i >>= 1;
    }
    return o;
}

int main(int argc, const char * argv[]) {
    for (int i = 1; i < argc; ++i) {
        std::string arg = argv[i];
        try {
            size_t pos = 0;
            unsigned long j = std::stoul(arg, &pos);
            if (pos != arg.length())
                std::cout << -1 << std::endl;
            else
                std::cout << count(j) << std::endl;
        } catch(const std::exception& e) {
            std::cout << -1 << std::endl;
        }
    }
}
