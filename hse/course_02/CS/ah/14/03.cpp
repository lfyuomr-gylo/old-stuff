#include <iostream>
#include <stdio.h>
#include <stdlib.h>
#include <sys/mman.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <unistd.h>

bool await_number(int *sem, int *number, int max_number, int id, int other_id) {
    while (*sem != id)
        sched_yield();
    return *number <= max_number;
}

void child(int *sem, int *number, int max_number, int id, int other_id) {
    while (await_number(sem, number, max_number, id, other_id)) {
        std::cout << (int)id << " " << *number << std::endl;
        fflush(stdout);
        *number += 1;
        *sem = other_id;
    }
    *sem = other_id;
    exit(0);
}

int main(int argc, char *argv[]) {
    int *sem = (int*)mmap(NULL, sizeof(int), PROT_READ | PROT_WRITE, MAP_SHARED | MAP_ANON, -1, 0);
    *sem = 0;

    int *number = (int*)mmap(NULL, sizeof(int), PROT_READ | PROT_WRITE, MAP_SHARED | MAP_ANON, -1, 0);
    *number = 1;
    
    int max_number = strtol(argv[1], NULL, 10);

    pid_t p1 = fork();
    if (p1 == 0) {
        child(sem, number, max_number, 1, 2);
    } else if (p1 < 0) {
        return -1;
    }

    pid_t p2 = fork();
    if (p2 == 0) {
        child(sem, number, max_number, 2, 1);
    } else if (p2 < 0) {
        return -1;
    }

    *sem = 1;

    waitpid(p1, NULL, 0);
    waitpid(p2, NULL, 0);

    munmap(sem, sizeof(int));
    munmap(number, sizeof(int));

    return 0;
}
