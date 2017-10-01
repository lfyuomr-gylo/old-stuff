#include <iostream>
#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <stdlib.h>

using namespace std;

int mysys(const char *str) {
    pid_t pid = fork();
    if(pid == 0) {
        execlp(str, str, NULL);
        exit(-1);
    }
    if(pid < 0) return -1;
    int status;
    wait(&status);
    if(WIFEXITED(status)) {
        return WEXITSTATUS(status);
    }
    if(WIFSIGNALED(status)) {
        return 128+WTERMSIG(status);
    }
    return 0;
}

int main(int argc, char ** argv)
{
    return !(((!mysys(argv[1]) || !mysys(argv[2])) && (!mysys(argv[3]))));
}

