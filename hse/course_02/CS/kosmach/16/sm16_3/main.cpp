#include <iostream>
#include <unistd.h>
#include <stdlib.h>
#include <sys/wait.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

using namespace std;

#define cmd1 argv[1]
#define cmd2 argv[2]
#define cmd3 argv[3]
#define file_path argv[4]

int main(int argc, char ** argv)
{
    int pipefd[2];
    pipe(pipefd);

    int file = open(file_path, O_CREAT | O_RDWR | O_TRUNC, 0666);

    if(!fork()) {
        dup2(pipefd[0], 0);
        dup2(file, 1);
        close(pipefd[1]);
        close(pipefd[0]);
        execlp(cmd3, cmd3, NULL);
        exit(0);
    }

    if(!fork()) {

        if(!fork()) {
            dup2(pipefd[1], 1);
            close(pipefd[1]);
            close(pipefd[0]);
            execlp(cmd1, cmd1, NULL);
            exit(0);
        }
        wait(NULL);
        if(!fork()) {
            dup2(pipefd[1], 1);
            close(pipefd[1]);
            close(pipefd[0]);
            execlp(cmd2, cmd2, NULL);
            exit(0);
        }
        close(pipefd[0]);
        close(pipefd[1]);
        wait(NULL);
        exit(0);
    }
    close(pipefd[0]);
    close(pipefd[1]);
    wait(NULL);
    wait(NULL);

    close(file);
    return 0;
}

