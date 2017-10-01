#include <bits/stdc++.h>
#include <unistd.h>
#include <wait.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <wait.h>
#include <signal.h>
#include <pthread.h>

class Semaphore {
 public:
    Semaphore(int ival):
            val_(ival),
            cond_(PTHREAD_COND_INITIALIZER),
            mutex_(PTHREAD_MUTEX_INITIALIZER) {
    }

    int up(int s=1) {
        pthread_mutex_lock(&mutex_);
        val_ += s;
        pthread_cond_broadcast(&cond_);
        int result = val_;
        pthread_mutex_unlock(&mutex_);
        return result;
    }

    int down(int s=1) {
        pthread_mutex_lock(&mutex_);
        if (s <= val_)
            val_ -= s;
        else
            pthread_cond_wait(&cond_, &mutex_);
        int result = val_;
        pthread_mutex_unlock(&mutex_);
        return result;
    }
 private:
    std::atomic_int val_;
    pthread_cond_t cond_;
    pthread_mutex_t mutex_;

};

void foo(Semaphore *sem) {
    for (size_t i = 0; i < 10000; i++) {
        printf("%d\n", sem->down());
        sched_yield();
        printf("%d\n", sem->up());
    }
}

int main() {
    Semaphore sem(10);

    std::vector<std::thread> threads;
    for (size_t i = 0; i < 20; i++)
        threads.push_back(std::thread(foo, &sem));

    for (auto &thread : threads)
        thread.join();

    return 0;
}