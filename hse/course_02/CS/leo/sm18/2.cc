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
    int x = 0;
    if (std::cin >> x) {
        pthread_t child;
        pthread_create(&child, NULL, func, NULL);
        pthread_join(child, NULL);
        std::cout << x << std::endl;
    }
    return NULL;
}

int main() {
    pthread_t thread;
    pthread_create(&thread, NULL, func, NULL);
    pthread_join(thread, NULL);

    return 0;
}
