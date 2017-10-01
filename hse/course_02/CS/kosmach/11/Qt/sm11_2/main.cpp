#include <iostream>
#include <fstream>
#include <string>
#include <time.h>

using namespace std;

void fix_time(tm * time_s) {
    time_s -> tm_year -= 1900;
    time_s -> tm_mon -= 1;
    time_s -> tm_isdst = -1;
}

int main(int argv, char ** argc)
{
    ifstream file(argc[1]);
    string str;
    time_t curr_t;
    getline(file, str);
    tm * time_struct = new tm;
    strptime(str.c_str(), "%Y/%m/%d %H:%M:%S", time_struct);
    fix_time(time_struct);
    curr_t = mktime(time_struct);
    while(getline(file, str)) {
        strptime(str.c_str(), "%Y/%m/%d %H:%M:%S", time_struct);
        fix_time(time_struct);
        time_t next = mktime(time_struct);
        cout << (next - curr_t) << endl;
        curr_t = next;
    }

    return 0;
}

