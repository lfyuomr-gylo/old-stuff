#include <iostream>
#include <dlfcn.h>

int main(int argc, char ** argv) {
    double (*func)(double) = (double (*)(double))dlsym(dlopen("/lib/libm.so.6", RTLD_NOW), argv[1]);
    for(double n; std::cout.precision(10), std::cin >> n; ) std::cout << func(n) << std::endl;
    return 0;
}
