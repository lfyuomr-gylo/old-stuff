#include <iostream>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

using namespace std;

int main(int argc, char ** argv)
{
    int fd = open(argv[1], O_WRONLY | O_CREAT, 0600);
    unsigned short n;
    while(cin >> n) {
        char buf[2];
        buf[1] = (char)n;
        n >>=8;
        buf[0] = (char)n;
        write(fd, buf, sizeof(buf));
    }
    close(fd);
    return 0;
}

