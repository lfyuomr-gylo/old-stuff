#include <iostream>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <ctype.h>

using namespace std;

class Reader {
private:
    char buf[16];
    int pointer;
    int bytes_size;
public:
    Reader();
    ~Reader();
    char get();
    bool empty();
    void to_next();
};

Reader::Reader() {
    pointer = 0;
    bytes_size = read(0, buf, sizeof(buf));
}

Reader::~Reader() {

}

char Reader::get() {
    if(pointer == bytes_size) {
        pointer = 0;
        bytes_size = read(0, buf, sizeof(buf));
    }
    if(bytes_size <= 0) {
        return '\0';
    }
    return buf[pointer++];
}

bool Reader::empty() {
    return (bytes_size <= 0);
}

void Reader::to_next() {
    while( isspace(this->get()) ) {
        // nothing
    }
    pointer--;
}

int read_number(Reader &s) {
    int sign = 1;
    int result = 0;
    char first = s.get();
    if(isspace(first)) return 0;
    if(first == '+' || first == '-') {
        if(first == '+') sign = 1;
        else sign = -1;
    } else {
        if(first == '\0') {
            return 0;
        } else {
            result += (int)(first-'0');
        }
    }
    char symb;
    while( !isspace((symb = s.get())) && !s.empty() && symb != '\0' ) {
        result *= 10;
        result += (int)(symb - '0');
    }
    return result*sign;
}

int main()
{
    long long int result = 0;
    Reader s;
    while(!s.empty()) {
        result += read_number(s);
    }
    cout << result << endl;
    return 0;
}
