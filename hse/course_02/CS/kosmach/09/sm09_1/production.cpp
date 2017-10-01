#include <iostream>
#include <vector>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

bool read_file(const char *path, std::vector<unsigned char> &vec) {
    int fd = open(path, O_RDONLY);
    if(fd < 0) return false;
    unsigned char buf[4096];
    int bytes;
    while( (bytes=read(fd, buf, sizeof(buf))) > 0) {
        if(bytes < 0) {
            close(fd);
            return false;
        }
        vec.insert(vec.end(), buf, buf + bytes);
    }
    close(fd);
    return true;
}
