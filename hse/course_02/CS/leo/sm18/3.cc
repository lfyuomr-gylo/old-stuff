//
// Created by leo on 02.12.15.
//
#include <bits/stdc++.h>
#include <unistd.h>
#include <wait.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <wait.h>
#include <signal.h>
#include <pthread.h>

void *func(void *d) {
    int sum = 0;
    int cur;
    while (scanf("%d", &cur) == 1) {
        sum += cur;
        sched_yield();
    }
    return reinterpret_cast<void*>(sum);
}

int main(int argc, char **argv) {
    std::vector<pthread_t> niddles;
    int n;
    sscanf(argv[1], "%d", &n);
    for (int i = 0; i < n; i++) {
        pthread_t cur;
        pthread_create(&cur, NULL, func, NULL);
        niddles.push_back(cur);
    }

    int sum = 0;
    for (auto item : niddles) {
        int cur;
        pthread_join(item, reinterpret_cast<void**>(&cur));
        sum += cur;
    }

    std::cout << sum << std::endl;
    return 0;
}