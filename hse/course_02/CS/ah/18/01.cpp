#include <iostream>
#include <pthread.h>

struct thread_desc {
	pthread_t wait;
	bool should_wait;
	int i;
};

void *run(void *n) {
	thread_desc *a = (thread_desc*)n;
    if (a->should_wait)
        pthread_join(a->wait, NULL);
    std::cout << a->i << std::endl;
    return NULL;
}

int main() {
	thread_desc *threads = new thread_desc[10];
	pthread_t cthread = 0;

    for (int i = 0; i < 10; ++i) {
        threads[i].wait = cthread;
        threads[i].i = i;
        threads[i].should_wait = (i > 0);
        pthread_create(&cthread, NULL, run, (void*)(threads + i));
    }
    pthread_join(cthread, NULL);

    delete[] threads;

    return 0;
}
