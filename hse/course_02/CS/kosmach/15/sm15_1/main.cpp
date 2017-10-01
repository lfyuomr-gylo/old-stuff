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
    int fdin = open(argv[2], O_RDONLY);
    int fdout = creat(argv[3], 0666);
    pid_t pid = fork();
    if(pid == 0) {
        dup2(fdin, 0);
        dup2(fdout, 1);
        execlp(argv[1], argv[1], NULL);
    }
    wait(NULL);
    close(fdin);
    close(fdout);
    return 0;
}

