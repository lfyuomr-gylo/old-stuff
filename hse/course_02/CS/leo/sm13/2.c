#include <sys/mman.h>
#include <stdio.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <unistd.h>
#include <stdint.h>
#include <fcntl.h>
#include <dlfcn.h>
#include <iostream>

int main(int argc, char **argv) {
    void* hdl = dlopen("/lib/libm.so.6", RTLD_NOW);
    auto func = (double (*)(double)) dlsym(hdl, argv[1]); 
    double x;
    while (std::cin >> x)
        printf("%.10g\n", func(x));
    dlclose(hdl);
    return 0;
}
