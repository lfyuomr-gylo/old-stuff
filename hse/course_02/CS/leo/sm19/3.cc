//
// Created by leo on 09.12.15.
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

std::vector<long long> vec;

pthread_mutex_t lock = PTHREAD_MUTEX_INITIALIZER;
void *append(void *args) {
    int_fast16_t id = reinterpret_cast<int_fast16_t>(args);
    long long count = id * 1000ll;
    for (size_t i = 0; i < 1000; i++, count++) {
        pthread_mutex_lock(&lock);
        vec.push_back(count);
        pthread_mutex_unlock(&lock);
    }

    return NULL;
}

int main() {
    pthread_t *threads = new pthread_t[100];
    for (int_fast16_t i = 0; i < 100; i++)
        pthread_create(threads + i, NULL, append, reinterpret_cast<void*>(i));

    for (size_t i = 0; i < 100; i++)
        pthread_join(threads[i], NULL);

    for (auto item :vec)
        std::cout << item << std::endl;

    delete[] threads;
    return 0;
}