#include <iostream>
#include <time.h>
#include <stdlib.h>



int main() {
    time_t t = time(NULL);
    time_t d = 0;
    while (std::cin >> d) {
        time_t *r = (time_t*) malloc(sizeof(time_t));
        int64_t test = (int64_t) t + (int64_t)d * 60 * 60 * 24;
        *r = test;
        if (*r != test) {
            std::cout << "OVERFLOW" << std::endl;
            continue;
        }


        tm *res = localtime(r);
        char *form = (char*) malloc(sizeof(char) * 20);
        strftime(form, 19, "%Y-%m-%d", res);
        std::cout << form << std::endl; 

    }
    
    return 0;
}
