//
// Created by leo on 30.11.15.
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

struct arg_t {
    pthread_t precur;
    size_t id;
};

void *func(void *d) {
    arg_t *arg = NULL;
    if (d) {
        arg = reinterpret_cast<arg_t*>(d);
        pthread_join(arg->precur, NULL);
        std::cout << arg->id << std::endl;
    }
    else
        std::cout << 0 << std::endl;
    return NULL;
}

int main() {
    pthread_t cur, prev;
    arg_t *args = new arg_t[10];

    pthread_create(&cur, NULL, func, NULL);
    for (size_t i = 1; i < 10; i++) {
        prev = cur;
        args[i].precur = prev;
        args[i].id = i;
        pthread_create(&cur, NULL, func, reinterpret_cast<void*>(args + i));
    }
    pthread_join(cur, NULL);

    delete[] args;
    return 0;
}