#include <iostream>
#include <unistd.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <iostream>
#include <unistd.h>
#include <string>
#include <sys/types.h>
#include <sys/wait.h>
#include <stdlib.h>
#include <sched.h>

using namespace std;

void run(int input, int output, pid_t my_id, int max_n) {
    int r = 1;
    if(my_id == 2) write(output, &r, sizeof(r));
    for(;;) {
        int n;
        read(input, &n, sizeof(n));
        if(n > max_n) exit(0);
        cout << my_id << ' ' << n << endl << flush;
        n++;

        sched_yield();

        write(output, &n, sizeof(n));

        if(n > max_n) exit(0);
    }
}

int main(int argc, char ** argv)
{
    int pipe1[2];
    int pipe2[2];

    pipe(pipe1);
    pipe(pipe2);

//    int number = 1;

    int M = strtol(argv[1], NULL, 10);

    if(M == 0) exit(0);

    if(!fork()) {
        close(pipe1[0]);
        close(pipe2[1]);

        run(pipe2[0], pipe1[1], 1, M);
    }

//    write(pipe2[0], &number, sizeof(number));

    if(!fork()) {
        close(pipe1[1]);
        close(pipe2[0]);

        run(pipe1[0], pipe2[1], 2, M);
    }

    close(pipe1[0]);
    close(pipe1[1]);
    close(pipe2[0]);
    close(pipe2[1]);

    wait(NULL);
    wait(NULL);

    return 0;
}

