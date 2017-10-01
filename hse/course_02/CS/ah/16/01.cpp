#include <stdio.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/wait.h>
#include <stdlib.h>

void process(int p[2], bool ptype, char *cmd) {
    dup2(p[ptype], ptype);
    close(p[!ptype]);
    close(p[ptype]);
    execlp("/bin/sh", "/bin/sh", "-c", cmd, NULL);
    exit(-1);
}

int main(int argc, char *argv[]) {
    int p[2];
    pipe(p);

    for (int i = 0; i < 2; ++i) {
        pid_t pid = fork();
        if (pid == 0) {
            process(p, !i, argv[i + 1]);
        } else if (pid > 0) {
            close(p[1 - i]);
        }
    }

    for (int i = 0; i < 2; ++i)
        wait(NULL);

    return 0;

}
