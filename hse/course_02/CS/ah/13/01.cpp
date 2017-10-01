#include <iostream>
#include <stdlib.h>
#include <sys/mman.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>

int main(int argc, char *argv[]) {
    for(int i = 1; i < argc; ++i) {
        int fd = open(argv[i], O_RDONLY, 0);
        if (fd < 0) {
            std::cout << "-1\n";
            continue;
        }

        if (lseek(fd, 0, SEEK_END) == lseek(fd, 0, SEEK_SET)) {
            std::cout << "0\n";
            continue;
        }

        char *data = (char *)mmap(NULL, getpagesize(), PROT_READ | PROT_WRITE, MAP_SHARED, fd, 0);
        if(data == MAP_FAILED) {
            std::cout << "-1\n";
            continue;
        }

        long long int lines = 0;
        size_t lsz = 0;
        for (size_t i = 0; data[i] != '\0'; ++i) {
            if(data[i] == '\n') {
                ++lines;
                lsz = 0;
            } else {
                ++lsz;
            }
        }

        std::cout << lines << "\n";
    }
    return 0;
}
