//
// Created by leo on 28.11.15.
//
#include <bits/stdc++.h>
#include <unistd.h>
#include <wait.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <wait.h>
#include <signal.h>

void handle_SIGINT(int s) {
    return;
}

int main() {
    sigset_t igmask, pmask;
    sigemptyset(&igmask);
    sigemptyset(&pmask);
    sigaddset(&igmask, SIGINT);

    sigprocmask(SIG_BLOCK, &igmask, &pmask);
    sigdelset(&pmask, SIGINT);

    signal(SIGINT, handle_SIGINT);

    std::cout << getpid() << std::endl;
    for (size_t i = 0; i < 5; i++) {
        sigsuspend(&pmask);
        if (i < 4)
            std::cout << i << std::endl;
    }

    return 0;
}
