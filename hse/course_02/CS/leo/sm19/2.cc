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

struct Item {
    Item(long long val=0):
            next_(NULL),
            val_(val) {
    }

    struct Item *next_;
    long long val_;
};

std::atomic<Item*> head(NULL);
void *append(void *args) {
    int_fast16_t id = reinterpret_cast<int_fast16_t>(args);
    long long count = id * 1000ll + 999ll;
    for (size_t i = 0; i < 1000; i++, count--) {
        Item *next = new Item(count);
        next->next_ = head.exchange(next);
        sched_yield();
    }

    return 0;
}

int main() {
    pthread_t *threads = new pthread_t[100];
    for (int_fast16_t i = 0; i < 100; i++)
        pthread_create(threads + i, NULL, append, reinterpret_cast<void*>(i));

    for (size_t i = 0; i < 100; i++)
        pthread_join(threads[i], NULL);

    Item *cur = head.load(), *prev = NULL;
    while (cur) {
        std::cout << cur->val_ << std::endl;
        prev = cur;
        cur = cur->next_;
        delete prev;
    }

    delete[] threads;
    return 0;
}
