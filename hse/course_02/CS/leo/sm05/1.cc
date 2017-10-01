#include <bits/stdc++.h>

int PrecLen(unsigned num) {
    if (num == 1)
        return 1;

    bool flag = true;
    unsigned first = 0, last = 0;
    for (unsigned i = 0; num; i++) {
        if (num & 1) {
            if (flag) {
                first = i;
                last = i;
                flag = false;
            }
            else {
                last = i;
            }
        }
        num >>= 1;
    }
    return (first == last && !first) ? 0 : last - first + 1;
}

int main() {
    unsigned num = 0;
    while (std::cin >> num) {
        std::cout << (PrecLen(num) <= 24) << std::endl;
    }
}
