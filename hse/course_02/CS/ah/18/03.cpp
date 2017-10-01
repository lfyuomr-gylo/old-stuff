#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>

void *run(void *i) {
    long out = 0, j = 0;
    while (scanf("%ld", &j) == 1) {
        out += j;
        sched_yield();
    }
    return (void*)(out);
}

int main(int argc, char *argv[]) {
    if (argc <= 1) {
        printf("0\n");
        return 0;
    }

    long n;
    sscanf(argv[1], "%ld", &n);

    pthread_t *threads = (pthread_t*)calloc(n, sizeof(pthread_t));

    for (long i = 0; i < n; ++i) {
        pthread_create(threads + i, NULL, run, NULL);
    }

    long out = 0;

    for (long i = 0; i < n; ++i) {
        long j;
        pthread_join(threads[i], (void**)(&j));
        out += j;
    }

    printf("%ld\n", out);

    free(threads);

    return 0;
}
