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
        waitpid(pid, &status, 0);
        if (WIFEXITED(status))
            return WEXITSTATUS(status);
        else if (WIFSIGNALED(status))
            return 128 + WTERMSIG(status);
        else
            return 0;
    } else if (pid == 0) {
        execlp("/bin/sh", "/bin/sh", "-c", str, (char*)0);
        exit(-1);
    } else {
        return -1;
    }
}

// int main(int argc, char *argv[]) {
//     printf("\n\n%d\n\n", mysys("ls -a -l"));
//     return 0;
// }
