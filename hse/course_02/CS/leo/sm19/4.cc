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

void *run(void*);

uint64_t NPROC, MAXVAL;
uint64_t VAL = 0;


pthread_mutex_t LOCK = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t CLOCK = PTHREAD_MUTEX_INITIALIZER;
class Worker {
 public:
    Worker(uint64_t id):
            id_(id),
            thread_(pthread_t()),
            cond_(pthread_cond_t()) {
        pthread_cond_init(&cond_, NULL);
    }

    void start() {
        pthread_create(&thread_, NULL, run, reinterpret_cast<void*>(this));
    }

    void join() {
        pthread_join(thread_, NULL);
    }

    void wait() {
        pthread_mutex_lock(&LOCK);
        if (VAL || id_)
            pthread_cond_wait(&cond_, &LOCK);
        std::cout << id_ << ' ' << VAL++ << std::endl;
        if (VAL > MAXVAL)
            exit(0);
        threads[(VAL * VAL) % NPROC].notify();
        pthread_mutex_unlock(&LOCK);
    }

    void notify() {
        pthread_cond_broadcast(&cond_);
    }

    uint64_t id() const {
        return id_;
    }

    static std::vector<Worker> threads;
 private:
    uint64_t id_;
    pthread_t thread_;
    pthread_cond_t cond_;
};

std::vector<Worker> Worker::threads;

int main(int argc, char **argv) {
    sscanf(argv[1], "%" SCNu64, &NPROC);
    sscanf(argv[2], "%" SCNu64, &MAXVAL);

    for (uint64_t i = 0; i <= NPROC; i++) {
        Worker::threads.push_back(Worker(i));
        Worker::threads[i].start();
    }

    Worker::threads[0].notify();

    for (auto item : Worker::threads)
        item.join();

    return 0;
}


void *run(void *args) {
    Worker *th = reinterpret_cast<Worker*>(args);
    for (;;) {
        th->wait();
    }
}