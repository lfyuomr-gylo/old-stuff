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

int main(int argc, char ** argv)
{
    int in = open(argv[1], O_RDONLY);
    int out = open(argv[2], O_WRONLY);
    copy_file(in, out);
    cout << argv[1] <<  ' ' << argv[2] << endl;
    close(in);
    close(out);
    return 0;
}

