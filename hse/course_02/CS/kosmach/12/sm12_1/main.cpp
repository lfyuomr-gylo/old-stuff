#include <iostream>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>

using namespace std;

int main(int argc, char ** argv)
{
    long long int result = 0;
    struct stat * buf = new struct stat;
    for(int i = 1; i < argc; i++) {
        int res = lstat(argv[i], buf);
        if(res == 0 && buf->st_nlink == 1 && S_ISREG(buf->st_mode)) {
            result += buf->st_size;
        }
    }
    delete buf;
    std::cout << result << std::endl;
    return 0;
}
