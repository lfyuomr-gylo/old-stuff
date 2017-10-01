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

volatile size_t i = 0;

class Thread {
 public:
    enum class MODE {
        JOINABLE = 1,
        DETACHED
    };
    Thread():
            thread_(NULL),
            attr_(NULL),
            joined_(false),
            mode_(MODE::JOINABLE) {
    }

    Thread(MODE mode):
            thread_(NULL),
            attr_(NULL),
            joined_(false),
            mode_(mode) {

    }

    ~Thread() {
        switch (mode_) {
            case MODE::JOINABLE:
                if (!joined_)
                    throw std::runtime_error("unjoined joinable thread");
                if (attr_)
                    pthread_attr_destroy(attr_);
                break;
            case MODE::DETACHED:
                if (attr_)
                    pthread_attr_destroy(attr_);
                break;
        }
    }

    virtual void run() = 0;

    int start() {
        return pthread_create(&thread_, attr_, Thread::Thread_func, reinterpret_cast<void *>(this));
    }

    int wait() {
        return pthread_join(thread_, NULL);
    }

 private:
    Thread(Thread&) = delete;
    Thread(Thread&&) = delete;
    void operator=(Thread&) = delete;

    static void *Thread_func(void *ptr) {
        reinterpret_cast<Thread*>(ptr)->run();
        return NULL;
    }

    pthread_t thread_;
    pthread_attr_t *attr_;
    bool joined_;
    MODE mode_;
};


int main() {

    for (size_t i = 0; i < 10; i++) {

    }
    return 0;
}