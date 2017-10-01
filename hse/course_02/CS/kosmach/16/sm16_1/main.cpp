#include <iostream>
#include <unistd.h>
#include <stdlib.h>
#include <sys/wait.h>

using namespace std;

int main(int argc, char ** argv)
{
    int pipefd[2];
    pipe(pipefd);

    if(!fork()) {
        dup2(pipefd[1], 1);
        close(pipefd[1]);
        close(pipefd[0]);
        execlp(argv[1], argv[1], NULL);
    }
    if(!fork()) {
        dup2(pipefd[0], 0);
        close(pipefd[0]);
        close(pipefd[1]);
        execlp(argv[2], argv[2], NULL);
    }

    close(pipefd[0]);
    close(pipefd[1]);

    wait(NULL);
    wait(NULL);

    return 0;
}

