#include <bits/stdc++.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h>

int main() {
    pid_t parent = getpid();
    std::vector<pid_t> children;
    for (size_t i = 0; i < 3; i++) {
        pid_t pid;
        if (!(pid = fork()))
            break;
        children.push_back(pid);
    }
    if (getpid() == parent) {
        for (auto child: children)
            waitpid(child, NULL, 0);
    }
    else {
        char line[7];
        read(0, line, 7);
        int64_t x = strtoll(line, NULL, 10);
        x *= x;
        std::cout << x << std::endl;
    }
    return 0;
}
