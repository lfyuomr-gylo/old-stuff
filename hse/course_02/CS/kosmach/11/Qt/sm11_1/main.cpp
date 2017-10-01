#include <iostream>
#include <inttypes.h>
#include <stdio.h>
#include <time.h>

using namespace std;

int main() {
    time_t now;
    int64_t days;
    while(scanf("%lld", &days) != -1) {
        time(&now);

        int64_t _now = now;
        int64_t _next = _now + days * 86400;

        time_t next = now + days * 86400;


        if(next == _next) {

            tm * next_time = localtime(&next);
            printf("%d-%.2d-%.2d\n", 1900 + next_time -> tm_year, 1 + next_time -> tm_mon, next_time -> tm_mday);

        } else {
            printf("%s", "OVERFLOW\n");
        }
    }
    return 0;
}
