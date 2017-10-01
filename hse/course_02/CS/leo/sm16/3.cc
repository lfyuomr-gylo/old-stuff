#include <bits/stdc++.h>
#include <unistd.h>
#include <wait.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <wait.h>

// ( cmd1 ; cmd2 ) | cmd3 > file
int main(int argc, char **argv) {
    int chanel[2];
    pipe(chanel);
    if (!fork()) {
        // firs child.
        // performs ( cmd1 ; cmd2 ) > chanel[1]
        close(chanel[0]);
        dup2(chanel[1], 1);
        close(chanel[1]);

//        system(argv[1]);
//        system(argv[2]);
        if (!fork()) {
            execlp(argv[1], argv[1], NULL);
            exit(-1);
        }
        wait(NULL);
        if (!fork()) {
            execlp(argv[2], argv[2], NULL);
            exit(-1);
        }
        wait(NULL);
        exit(0);
    }
    if (!fork()) {
        // second child
        dup2(chanel[0], 0);
        int ofd = open(argv[4], O_WRONLY | O_CREAT | O_TRUNC, 0666);
        dup2(ofd, 1);
        close(ofd);
        close(chanel[0]);
        close(chanel[1]);

        execlp(argv[3], argv[3], NULL);
        exit(-1);
    }

    wait(NULL);
//    std::
// << "That's all" << std::endl;
    close(chanel[0]);
    close(chanel[1]);
    wait(NULL);

    return 0;
}