#include <bits/stdc++.h>
#include <unistd.h>
#include <wait.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <wait.h>

int main(int argc, char **argv) {
    pid_t pid = fork();
    if (!pid) {
        // child
        int ifd = open(argv[2], O_RDONLY);
        dup2(ifd, 0);
        close(ifd);
        
        int ofd = open(argv[3], O_CREAT | O_WRONLY | O_TRUNC, 0666);
        dup2(ofd, 1);
        close(ofd);

        execlp(argv[1], argv[1], NULL);
    }

    if (pid)
        wait(NULL);
    return 0;
}
