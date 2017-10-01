#include <vector>
#include <iostream>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>

void copy_file(int in_fd, int out_fd) {
    char buf[4096];
    int icount, ocount;
    do {
        icount = read(in_fd, buf, 4096);
        ocount = write(out_fd, buf, icount);
        while (ocount != icount)
            ocount += write(out_fd, buf + ocount, icount - ocount);
    } while(icount);
}
