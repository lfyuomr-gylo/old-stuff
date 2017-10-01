#include <iostream>
#include <unistd.h>
#include <string>
#include <sys/types.h>
#include <sys/wait.h>
#include <stdlib.h>

using namespace std;

int main()
{
    for(int i = 0; i < 3; i++) {
        pid_t id = fork();
        if(id == 0) {

            char buf[8];
            read(0, buf, 7);
            buf[7] = 0;
            int a = strtol(buf, NULL, 10);
            cout << a*a << endl << flush;
            exit(0);
        }
    }
    for(int i = 0; i < 3; i++) wait(NULL);
    return 0;
}

