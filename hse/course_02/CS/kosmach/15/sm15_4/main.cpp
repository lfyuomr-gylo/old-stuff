#include <iostream>
#include <string>
#include <string.h>
#include <iostream>
#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

using namespace std;

int main(int argc, char ** argv)
{
    string a = "import sys \n\
a = 1 \n\
for i in range(1, len(sys.argv)):\n\
    a = a * int(sys.argv[i])\n\
print(a)\n";
    int fd = creat("script.py", 0777);
    size_t bytes = 0;
    while(bytes < a.size()) {
        bytes += write(fd, a.c_str()+bytes, a.size()-bytes);
    }
    char ** arg = new char * [argc+3];
    arg[0] = new char [7];
    arg[1] = new char[10];
    strcpy(arg[0], "python");
    strcpy(arg[1], "script.py");
    for(int i = 2; i <= argc + 2 ; i++) {
        arg[i] = argv[i-1];
    }
    arg[argc+2] = NULL;

    close(fd);
    execvp("python", arg);
    return 0;
}

