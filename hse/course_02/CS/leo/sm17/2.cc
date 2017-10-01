#include <bits/stdc++.h>
#include <unistd.h>
#include <wait.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <wait.h>
#include <signal.h>

enum class MODE : int {
    MSGN = 1,
    MSQR = 2
};

MODE mode = MODE::MSGN;

void handle_SIGUSR1(int s) {
     mode = MODE::MSGN;
}

void handle_SIGUSR2(int s) {
    mode = MODE::MSQR;
}

int main() {
    signal(SIGUSR1, handle_SIGUSR1);
    signal(SIGUSR2, handle_SIGUSR2);

    std::cout << getpid() << std::endl;

    int x;
    while (std::cin >> x)
        std::cout << ((mode == MODE::MSGN) ? -x : x * x) << std::endl;

    return 0;
}