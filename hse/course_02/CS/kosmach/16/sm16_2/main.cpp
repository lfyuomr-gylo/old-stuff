#include <iostream>
#include <unistd.h>
#include <stdlib.h>
#include <sys/wait.h>

using namespace std;

int main()
{
    int pipefd[2];
    pipe(pipefd);

    if(!fork()) {
        if(!fork()) {
            close(pipefd[1]);
            long long int result = 0;
            int number;
            while(read(pipefd[0], &number, sizeof(number)) > 0) result += number;
            close(pipefd[0]);
            cout << result << endl;
            exit(0);
        }
        close(pipefd[0]);
        close(pipefd[1]);
        wait(NULL);
        exit(0);
    }
    close(pipefd[0]);

    int n;
    while(cin >> n) {
        write(pipefd[1], &n, sizeof(n));
    }
    close(pipefd[1]);
    wait(NULL);
    return 0;
}

