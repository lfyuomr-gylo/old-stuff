#include <dlfcn.h>
#include <stdio.h>
#include <iostream>
#include <string>
using namespace std;

int main() {
    long long sum = 0;
    string s;
    while (cin) {
        long long a, b;
        scanf("%llx%llx", &a, &b);
        cout << a << ' ' << b << endl;
        sum += -b - a;
        cout << -b -a << endl;
        getline(cin, s);
    }
    //cout << sum << endl;

}
