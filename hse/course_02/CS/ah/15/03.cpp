#include <stdio.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/wait.h>
#include <stdlib.h>

int mysys(const char *str) {
    pid_t pid = fork();
    if (pid > 0) {
        int status;
        wait(&status);
        if (WIFEXITED(status))
            return WEXITSTATUS(status);
        return -1;
    } else if (pid == 0) {
        execlp("/bin/sh", "/bin/sh", "-c", str, NULL);
        return -1;
    } else {
        return -1;
    }
}

int main(int argc, char *argv[]) {
    return !((!mysys(argv[1]) || !mysys(argv[2])) && !mysys(argv[3]));
}
