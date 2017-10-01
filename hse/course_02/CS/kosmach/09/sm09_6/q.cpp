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

int main(int argc, char ** argv) {
    int fd = open(argv[1], O_RDWR);
    
    Data p[4];

    for(int i =0; i < 3; i++) {
        lseek(fd, i * 12, SEEK_SET);
        fullread(fd, p[i]);
        cout << p[i].x << ' ' << p[i].y << endl;
    }

    close(fd);
    return 0;
}
