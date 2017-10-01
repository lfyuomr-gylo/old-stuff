#include <iostream>
#include <unistd.h>
#include <string>
#include <sys/types.h>
#include <sys/wait.h>
#include <stdlib.h>

using namespace std;

int main()
{

    pid_t id2 = fork();
    if(id2 == 0) {
        pid_t id3 = fork();
        if(id3 == 0) {
            cout << "3\n" << flush;
        } else {
            cout << "2 " << flush;
            wait(NULL);
        }
    } else {
        cout << "1 " << flush;
        wait(NULL);
    }
    return 0;
}

