#include <bits/stdc++.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h>

void reverse(pid_t ancestor) {
    int n;
    if (std::cin >> n) {
        pid_t pid;
        if (!(pid = fork())) {
            reverse(ancestor);
        }
        else if(pid > 0) {
            int status;
            waitpid(pid, &status, 0);
            if (WIFEXITED(status) && WEXITSTATUS(status)) {
                if (getpid() != ancestor)
                    exit(1);
                else
                    exit(0);
            }
        }
        else if (pid < 0) {
            std::cout << -1 << std::endl;
            if (getpid() != ancestor)
                exit(1);
            else
                exit(0);
        }
        
        if (pid)
            std::cout << n << std::endl;
        exit(0);
    }
}

int main() {
    reverse(getpid());
    return 0;
}
