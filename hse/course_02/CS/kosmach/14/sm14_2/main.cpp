#include <iostream>
#include <unistd.h>
#include <string>
#include <sys/types.h>
#include <sys/wait.h>
#include <stdlib.h>

using namespace std;

int main(int argc, char ** argv)
{
    int curr;
    cout << "hh" << endl << flush;
    if(!(cin >> curr)) return 0;

    pid_t id = fork();
    if(id == 0) { //child
        execl(argv[0], argv[0], "0",  0);
    } else if(id == -1) {
        cout << -1 << endl << flush;
    } else {
        int status;
        pid_t w = wait(&status);
        if(WEXITSTATUS(status) == 0) {
            cout << curr << endl << flush;
        } else {
            if(argc == 1) { // first proc
                return -1;
            } else {
                return 0;
            }
        }
    }
    return 0;
}

