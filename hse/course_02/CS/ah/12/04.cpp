#include <iostream>
#include <string.h>

rwx getnums(int t) {
    rwx a;
    a.t1 = a.t2 = a.t3 = 0;

    int l = strlen(argv[i]);

    for (int i = 0; i < l; ++i) {
        
    }

    if (l == 3) {
        a.t1 = argv[i][0] - '0';
        a.t2 = argv[i][1] - '0';
        a.t3 = argv[i][2] - '0';
    } else if (l == 2) {
        a.t1 = 0;
        a.t2 = argv[i][0] - '0';
        a.t3 = argv[i][1] - '0';
    } else if (l == 1) {
        a.t1 = 0;
        a.t2 = 0;
        a.t3 = argv[i][0] - '0';
    } else {
        a.t1 = 0;
        a.t2 = 0;
        a.t3 = 0;
    }
    return a;
}

int main(int argc, char **argv) {
    struct rwx {
        int t1, t2, t3;
    };

    for (int i = 1; i < argc; ++i) {
        // =(

        rwx a;

        if (a.t1 >= 4) {
            a.t1 -= 4;
            std::cout << 'r';
        } else {
            std::cout << '-';
        }
        if (a.t1 >= 2) {
            a.t1 -= 2;
            std::cout << 'w';
        } else {
            std::cout << '-';
        }
        if (a.t1 >= 1) {
            a.t1 -= 1;
            std::cout << 'x';
        } else {
            std::cout << '-';
        }


        if (a.t2 >= 4) {
            a.t2 -= 4;
            std::cout << 'r';
        } else {
            std::cout << '-';
        }
        if (a.t2 >= 2) {
            a.t2 -= 2;
            std::cout << 'w';
        } else {
            std::cout << '-';
        }
        if (a.t2 >= 1) {
            a.t2 -= 1;
            std::cout << 'x';
        } else {
            std::cout << '-';
        }


        if (a.t3 >= 4) {
            a.t3 -= 4;
            std::cout << 'r';
        } else {
            std::cout << '-';
        }
        if (a.t3 >= 2) {
            a.t3 -= 2;
            std::cout << 'w';
        } else {
            std::cout << '-';
        }
        if (a.t3 >= 1) {
            a.t3 -= 1;
            std::cout << 'x';
        } else {
            std::cout << '-';
        }

        std::cout << "\n";
    }
}
