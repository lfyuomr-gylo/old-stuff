#include <iostream>
#include <sys/stat.h>
#include <sys/types.h>
#include <unistd.h>

int main(int argc, char **argv) {
    int64_t size = 0;
    for (int i = 1; i < argc; ++i) {
        struct stat s;
        if (lstat(argv[i], &s) != -1 && S_ISREG(s.st_mode) && !access(argv[i], X_OK))
            size += s.st_size;
    }
    std::cout << size << "\n";
}
