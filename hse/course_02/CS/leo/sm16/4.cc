#include <bits/stdc++.h>
#include <unistd.h>
#include <wait.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <wait.h>

void fullwrite(int fd, char *buf, size_t blen) {
    for (size_t count = 0; count < blen; count += write(fd, buf + count, blen - count));
}
bool fullread(int fd, char *buf, size_t blen) {
    size_t count = 0;
    while (count < blen) {
        size_t cur;
        if ((cur = read(fd, buf + count, blen - count)) > 0) {
//            std::cout << "cur = " << cur << std::endl;
            count += cur;
        }
        else {
//            std::cout << "I've returned false" << std::endl;
            return false;
        }
    }
    return true;
}

void ping_pong(int ifd, int ofd, uint64_t n, size_t mynum) {
    if (n < mynum)
        return;

    uint64_t cur = 1;
    if (mynum == 1) {
        std::cout << mynum << ' ' << cur << std::endl;
        cur++;
        fullwrite(ofd, reinterpret_cast<char*>(&cur), sizeof(cur));
    }

    while (fullread(ifd, reinterpret_cast<char*>(&cur), sizeof(cur))) {
        std::cout << mynum << ' ' << cur << std::endl;
        cur++;
        if (cur <= n)
            fullwrite(ofd, reinterpret_cast<char*>(&cur), sizeof(cur));
        else {
//            std::cout << mynum << " returns" << std::endl;
            return;
        }
    }
}

int main(int argc, char **argv) {
    uint64_t N = strtoll(argv[1], NULL, 10);

    int chanel[2][2];
    pipe(chanel[0]);
    pipe(chanel[1]);

    size_t pnum = 1;
    for (; pnum <= 2; pnum++)
        if (!fork())
            break;

    if (pnum != 3) {
        // children
        close(chanel[pnum - 1][1]);
        close(chanel[2 - pnum][0]);
        ping_pong(chanel[pnum - 1][0], chanel[2 - pnum][1], N, pnum);

        close(chanel[pnum - 1][0]);
        close(chanel[2 - pnum][1]);
        exit(0);
    }
    else {
        wait(NULL);
//        std::cout << "thith" << std::endl;
        close(chanel[0][0]);
        close(chanel[0][1]);
        close(chanel[1][0]);
        close(chanel[1][1]);
        wait(NULL);
//        std::cout << "thith" << std::endl;
    }

    return 0;
}
