#include <iostream>
#include <unistd.h>
#include <string>
#include <sys/types.h>
#include <sys/wait.h>
#include <stdlib.h>

using namespace std;

int main(int argc, char ** argv)
{
    int N;
    int curr;
    if(argc == 1) {
        cin >> N;
        curr = 1;
        cout << curr << flush;
        if(N == curr) cout << '\n' << flush;
    } else {
        N = strtol(argv[1], NULL, 10);
        curr = strtol(argv[2], NULL, 10);
        cout << ' ' << curr << flush;
    }

    if(N == curr) return 0;

    pid_t id = fork();
    if(id == 0) { //child
        execl(argv[0], argv[0], to_string(N).c_str(), to_string(curr + 1).c_str(), NULL);
    } else {
        int status;
        wait(&status);
    }
    if(argc == 1) cout << '\n';
    return 0;
}

