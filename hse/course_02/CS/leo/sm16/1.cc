//
// Created by leo on 21.11.15.
//
#include <bits/stdc++.h>
#include <unistd.h>
#include <wait.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <wait.h>


int main(int argv, char **argc) {
    int canal[2];
    pipe(canal);

    int mypid = 0;
    for (; mypid < 2; mypid++) {
        if (!fork())
            break;
        else
            close(canal[1 - mypid]); // to spare 'close()' calls
    }

    if (mypid == 0) {
        // cmd1
        dup2(canal[1], 1);
        close(canal[0]);
        close(canal[1]);
        execlp("/bin/sh", "/bin/sh", "-c", argc[1], NULL);
        exit(-1);
    }
    if (mypid == 1) {
        // cmd2
        dup2(canal[0], 0);
        close(canal[0]); // canal[1] has been already closed during the creation of children
        execlp("/bin/sh", "/bin/sh", "-c", argc[2], NULL);
        exit(-1);
    }

    if (mypid == 2) {
        // parent
        for (size_t i = 0; i < 2; i++)
            wait(NULL);
    }
    return 0;
}