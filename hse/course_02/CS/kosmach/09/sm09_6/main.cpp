#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <ctype.h>
#include <string.h>
#include <stdlib.h>

using namespace std;

struct Data
{
    int x;       // 4 байта,
    long long y; // 8 байт
};

void fullread(int fd, struct Data &p) {
    char buff[12];
    unsigned int bytes = 0;
    int r;
    do {
        r = read(fd, buff + bytes, 12 - bytes);
        bytes += r;
    } while(bytes < 12);
    memcpy(&p.x, buff, sizeof(p.x));
    memcpy(&p.y, buff+sizeof(p.x), sizeof(p.y));
}

void fullwrite(int fd, struct Data &p) {
    char buff[12];
    unsigned int bytes = 0;
    int r;
    memcpy(buff, &p.x, sizeof(p.x));
    memcpy(buff + sizeof(p.x), &p.y, sizeof(p.y));
    do {
        r = write(fd, buff + bytes, 12 - bytes);
        bytes += r;
    } while(bytes < 12);
}

void run(int fd, int A) {
    int filesize = (lseek(fd, 0, SEEK_END) - lseek(fd, 0, SEEK_SET))/12;
    int i = 0;
    for(; i < filesize/2; i++) {
        struct Data p1, p2;
        lseek(fd, i * 12, SEEK_SET);
        fullread(fd, p1);
        lseek(fd, - (i+1) * 12, SEEK_END);
        fullread(fd, p2);
        p1.y += p1.x * A;
        p2.y += p2.x * A;
        lseek(fd, i * 12, SEEK_SET);
        fullwrite(fd, p2);
        lseek(fd, - (i+1) * 12, SEEK_END);
        fullwrite(fd, p1);
    }
    if(filesize % 2 == 1) {
        struct Data p1, p2;
        lseek(fd, i * 12, SEEK_SET);
        fullread(fd, p1);
        lseek(fd, - (i+1) * 12, SEEK_END);
        fullread(fd, p2);
        p1.y += p1.x * A;
        p2.y += p2.x * A;
        lseek(fd, i * 12, SEEK_SET);
        fullwrite(fd, p2);
        lseek(fd, - (i+1) * 12, SEEK_END);
        fullwrite(fd, p1);
    }
}

int main(int argc, char ** argv)
{
    int fd = open(argv[1], O_RDWR);
    int A = strtol(argv[2], NULL, 10);

    run(fd, A);

    close(fd);
    return 0;
}

