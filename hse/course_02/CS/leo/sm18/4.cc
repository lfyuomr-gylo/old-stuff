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
volatile unsigned int cur = 1;
volatile int cur_tid = 1;

struct msg_t {
    int tid_;
    unsigned int max_;
};

void *func(void *d) {
    msg_t msg = *reinterpret_cast<msg_t*>(d);
    while (cur <= msg.max_) {
        if (cur_tid == msg.tid_ && cur <= msg.max_) {
            std::cout << msg.tid_ << ' ' << cur++ << std::endl;
            cur_tid = (msg.tid_ == 1) ? 2 : 1;
        }
//        else
//            sched_yield();
    }

    return NULL;
}

int main(int argc, char **argv) {
    unsigned int max = strtoul(argv[1], NULL, 10);
    std::vector<pthread_t> threads;
    msg_t *messages = new msg_t[2];
    for (int i = 1; i <= 2; i++) {
        messages[i] = {
                i,
                max
        };
        pthread_t cur;
        pthread_create(&cur, NULL, func, reinterpret_cast<void*>(messages + i));
        threads.push_back(cur);
    }

    for (auto item : threads)
        pthread_join(item, NULL);
    return 0;
}
