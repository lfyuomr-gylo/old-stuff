#include <stdio.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/wait.h>
#include <stdlib.h>

void r1(int p[2], int o, char *cmd) {
    dup2(p[0], STDIN_FILENO);
    dup2(o, STDOUT_FILENO);
    close(p[1]);
    close(p[0]);
    execlp(cmd, cmd, NULL);
}

void r2(int p[2], char *cmd) {
    dup2(p[1], STDOUT_FILENO);
    close(p[1]);
    close(p[0]);
    execlp(cmd, cmd, NULL);
}

int main(int argc, char *argv[]) {
    int p[2];
    pipe(p);
    int o = open(argv[4], O_WRONLY | O_CREAT | O_TRUNC, 0666);

    if(!fork())
        r1(p, o, argv[3]);

    close(o);
    close(p[0]);

    if(!fork()) {
        if(!fork())
            r2(p, argv[1]);
        wait(NULL);
        if(!fork())
            r2(p, argv[2]);
        wait(NULL);
        close(p[1]);
        exit(0);
    }
    
    close(p[1]);
    wait(NULL);
    wait(NULL);

    return 0;
}
