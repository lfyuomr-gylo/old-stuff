#include <iostream>
#include <unistd.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/wait.h>
#include <stdlib.h>

void sum(int p[2]) {
    close(p[1]);
    long long int sum = 0;
    for (int add = 0; read(p[0], &add, sizeof(int)) > 0; sum += add);
    std::cout << sum << std::endl;
    close(p[0]);
}

void forward_process(void (*f)(int[2]), int p[2]) {
    pid_t pid = fork();
    if (pid == 0) {
        f(p);
        exit(0);
    } else {
        close(p[0]);
        close(p[1]);
        waitpid(pid, NULL, 0);
        exit(0);
    }
}

int main() {
    int p[2];
    pipe(p);

    pid_t pid = fork();
    if (pid == 0) {
        forward_process(&sum, p);
        exit(1);
    } else {
        close(p[0]);
        int in;
        while (std::cin >> in) {
            write(p[1], &in, sizeof(int));
        }
        close(p[1]);
        waitpid(pid, NULL, 0);
    }

    return 0;
}
