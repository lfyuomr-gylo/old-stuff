//
// Created by leo on 28.11.15.
//
#include <bits/stdc++.h>
#include <unistd.h>
#include <wait.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <wait.h>
#include <signal.h>

volatile uint64_t max_num = 0;
int p[2];

struct msg_t {
    pid_t pid;
    uint64_t num;
};

void handle_SIGUSR1(int s) {
    msg_t imsg;
    msg_t omsg;
    read(p[0], reinterpret_cast<char*>(&imsg), sizeof(imsg));

    if (imsg.num == max_num) {
        // завершаем, если слишком большое число
        if (imsg.pid) {
            omsg = {
                    0,
                    max_num
            };
            write(p[1], reinterpret_cast<char*>(&omsg), sizeof(omsg));
//            std::cout << kill(imsg.pid, SIGUSR1) << " 1 " << std::endl;
            std::cout.flush();
            kill(imsg.pid, SIGUSR1);
        }
        close(p[0]);
        close(p[1]);
        exit(0);
    }

    std::cout << ((getpid() < imsg.pid) ? 1 : 2) << ' ' << imsg.num << std::endl;
    std::cout.flush();
    omsg = {
            getpid(),
            imsg.num + 1
    };
    write(p[1], reinterpret_cast<char*>(&omsg), sizeof(omsg));
//    std::cout << kill(imsg.pid, SIGUSR1) << " 2 " << imsg.pid << std::endl;
    std::cout.flush();
    kill(imsg.pid, SIGUSR1);
}

int main(int argc, char **argv) {
    max_num = strtoull(argv[1], NULL, 10);
//    max_num = 10;
    pipe(p);

    signal(SIGUSR1, handle_SIGUSR1);
    uint64_t i;
    for (i = 0; i < 2; i++) {
        if (!fork()) {
            break;
        }
    }

    if (i == 1) {
        msg_t msg = {
                (max_num > 1) ? getpid() : 0,
                1
        };
        write(p[1], reinterpret_cast<char*>(&msg), sizeof(msg));
        if (max_num == 1) {
            close(p[0]);
            close(p[1]);
            exit(0);
        }
    } else if (i == 0) {
        raise(SIGUSR1);
    } else if (i == 2) {
        wait(NULL);
        wait(NULL);
        close(p[0]);
        close(p[1]);
        std::cout << "Done" << std::endl;
        std::cout.flush();
    }

    if (i != 2) {
        for (;;)
            pause();
    }
    return 0;
}