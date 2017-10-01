#include <bits/stdc++.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h>


int main() {
    int n;
    std::cin >> n;
    if (n == 1) {
        std::cout << 1 << std::endl;
        return 0;
    }
    for (int i = 1; i <= n; i++) {
        if (i != n)
            (std::cout << i << ' ').flush();
        else
            (std::cout << i).flush();
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
