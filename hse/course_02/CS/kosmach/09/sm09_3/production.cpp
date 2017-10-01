#include <iostream>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

using namespace std;

void copy_file(int in_fd, int out_fd) {
    char buf[4096];
    int in_bytes;
    while( (in_bytes = read(in_fd, buf, sizeof(buf))) > 0 ) {
        int out_bytes = 0;
        int r;
        do {
            r = write(out_fd, buf + out_bytes, in_bytes - out_bytes);
            out_bytes += r;
        } while( r >  0);
    }
}
