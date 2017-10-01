#include <stdio.h>
#include <ctype.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>

template<size_t buffer_size>
class Reader {
    int fd;
    char buffer[buffer_size];
    ssize_t in_buffer;
    ssize_t current;

public:
    Reader(int _fd)
    : fd (_fd)
    , in_buffer (0)
    , current (0)
    {}

    bool operator >> (char &c) {
        if (current < in_buffer) {
            c = buffer[current++];
        } else {
            current = 0;
            ssize_t read_result = read(fd, buffer, buffer_size);
            if (read_result <= 0)
                return 0;
            in_buffer = read_result;
            return *this >> c;
        }
        return 1;
    }
};

int main(int argc, char **argv) {
    Reader<16> istream(STDIN_FILENO);

    long long sum = 0;
    char ch;
    int current = 0;
    int sign = 1;

    while (istream >> ch) {
        if (ch == '-') {
            sign = -1;
        } else if (isspace(ch)) {
            sum += current * sign;
            current = 0;
            sign = 1;
        } else if (isdigit(ch)) {
            current = current * 10 + (ch - '0');
        }
    }
    
    sum += current * sign;

    printf("%lld\n", sum);

    return 0;
}
