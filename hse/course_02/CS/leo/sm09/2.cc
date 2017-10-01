#include <vector>
#include <iostream>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>

int main(int argc, char **argv) {
    int fd = creat(argv[1], 0600);

    union {
        unsigned short n;
        char buf[2];
    };
    while (std::cin >> n) {
        char thith = buf[0];
        buf[0] = buf[1];
        buf[1] = thith;
        write(fd, buf, 2);
    }

    close(fd);
    return 0;
}
