#include <bits/stdc++.h>
#include <unistd.h>
#include <wait.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <wait.h>

int mysys(const char *str) {
    pid_t pid = fork();
    if (pid < 0)
        return -1;
    if (!pid) {
        // child
        execlp("/bin/sh", "/bin/sh", "-c", str, NULL);
        exit(-1);
    }

    int status;
    wait(&status);

    if (WIFEXITED(status))
        return WEXITSTATUS(status);
    if (WIFSIGNALED(status))
        return 128 + WTERMSIG(status);

    return -1;
}

//int main() {
//    std::string command;
//    std::cin >> command;
//    mysys(command.c_str());
//    return 0;
//}
