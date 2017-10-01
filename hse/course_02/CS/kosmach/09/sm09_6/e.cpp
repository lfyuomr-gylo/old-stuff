#include <iostream>
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

int main(int argc, char ** argv) {
    int fd = open(argv[1], O_RDWR);
    
    Data p[4];
    
    p[0].x = 2;
    p[0].y = 4;

    p[1].x = 5;
    p[1].y = 7;

    p[2].x = 6;
    p[2].y = 3;

    p[3].x = 50;
    p[3].y = 100;

    for(int i =0; i < 3; i++) {
        lseek(fd, i * 12, SEEK_SET);
        fullwrite(fd, p[i]);
    }
 
    close(fd);
    return 0;
}
