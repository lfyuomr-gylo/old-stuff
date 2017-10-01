#include <bits/stdc++.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <sys/mman.h>

int main(int argv, char **argc) {
    uint64_t N = strtoull(argc[1], NULL, 10);
    uint64_t pnum = 1;
    pid_t ppid;

    uint64_t *cur = reinterpret_cast<uint64_t*>(mmap(NULL, sizeof(uint64_t), PROT_READ | PROT_WRITE, MAP_SHARED | MAP_ANON, -1, 0));
    *cur = 1;
    
    for (; pnum <= 2; pnum++)
        if (!(ppid = fork()))
            break;

    // wait children
    if (ppid != 0)
        for (uint64_t i = 0; i < 2; i++)
            wait(NULL);

    
    while(*cur <= N) {
        if (*cur % 2 != pnum % 2) {
            sched_yield();
            continue;
        }
        std::cout << pnum << ' ' << *cur << std::endl;
        (*cur)++;
    }

    munmap(cur, sizeof(uint64_t));
    return 0;
}
