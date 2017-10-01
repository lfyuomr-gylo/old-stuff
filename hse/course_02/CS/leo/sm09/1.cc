#include <vector>
#include <iostream>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>

bool read_file(const char *path, std::vector<unsigned char> &v) {
    int fd = open(path, O_CLOEXEC , O_RDONLY);
    if (fd == -1)
        return false;

    char *buff = new char[4096];
    ssize_t count = 0;
    do {
        count = read(fd, buff, 4096);
        if (count == -1)
            return false;

        v.insert(v.end(), buff, buff + count);
    } while (count);

    free(buff);
    close(fd);
    return true;
}
/*
int main() {
    std::vector<unsigned char> v;
    bool thith = read_file("thith", v);
    for (auto s: v)
        std::cout << s << ' ';

    return 0;
}*/
