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

int n = 0;

void handle_SIGINT(int s) {
    if (n < 4)
        std::cout << n++ << std::endl;
    else
        exit(0);
}

int main() {
    std::cout << getpid() << std::endl;

    signal(SIGINT, handle_SIGINT);

    for (;;)
        pause();

    return 0;
}
