#include <bits/stdc++.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h>

int main() {
    size_t n = 3;
    //std::cin >> n;
    for (size_t i = 1; i <= n; i++) {
        (std::cout << i << ' ').flush();
        if (i == n)
            exit(0);
        pid_t pid;
        if (!(pid = fork()))
            continue;
        waitpid(pid, NULL, 0);
        if (i != 1)
            exit(0);
        else
            break;
    }
    std::cout << std::endl;
    return 0;
}
