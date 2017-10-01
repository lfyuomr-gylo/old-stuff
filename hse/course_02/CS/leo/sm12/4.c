#include <bits/stdc++.h>
#include <unistd.h>

int main(int argc, char** argv) {
    for (int i = 1; i < argc; i++) {
        std::string res;
        if (strlen(argv[i]) == 2)
            res += "---";
        if (strlen(argv[i]) == 1)
            res += "------";

        for (size_t j = 0; j < strlen(argv[i]); j++) {
            unsigned char symb = argv[i][j] - '0';
            if ((symb >> 2) & 1)
                res += "r";
            else
                res += "-";

            if ((symb >> 1) & 1)
                res += "w";
            else
                res += "-";

            if (symb & 1)
                res += "x";
            else
                res += "-";
        }
        std::cout << res << std::endl;
    }
    return 0;
}

