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

bool fullread(int fd, char *buf, size_t blen) {
    return read(fd, buf, blen) > 0;
}

int main() {
    int canal[2];
    pipe(canal);
    pid_t pid;
    if (!(pid = fork())) {
        // son
        close(canal[1]);
        if (!fork()) {
            // grandson
            int64_t sum = 0;
            int cur = 0;
            while (fullread(canal[0], reinterpret_cast<char*>(&cur), sizeof(int)))
                sum += cur;

            std::cout << sum << std::endl;
            close(canal[0]);
            exit(0);
        }
        else {
            // son
            close(canal[0]);
            // wait for grandson
            wait(NULL);
            exit(0); // exit from son
        }
    }
    else {
        // parent
        close(canal[0]);
        for (int x = 0; std::cin >> x;)
            write(canal[1], reinterpret_cast<char*>(&x), sizeof(int));

        close(canal[1]);
        wait(NULL);
    }

    return 0;
}
