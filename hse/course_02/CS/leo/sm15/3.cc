#include <bits/stdc++.h>
#include <unistd.h>
#include <wait.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <wait.h>

bool mysys(const char *str) {
    pid_t pid = fork();
    if (pid < 0)
        return false;
    if (!pid) {
        // child
        execlp("/bin/sh", "/bin/sh", "-c", str, NULL);
        exit(-1);
    }

    int status;
    wait(&status);

    if (WIFEXITED(status))
        return  !WEXITSTATUS(status);

    return false;
}

int main(int argc, char **argv) {
    return !((mysys(argv[1]) || mysys(argv[2])) && mysys(argv[3]));
}