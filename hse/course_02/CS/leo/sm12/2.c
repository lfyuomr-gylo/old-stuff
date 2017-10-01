#include <bits/stdc++.h>
#include <sys/stat.h>
#include <unistd.h>

int main(int argc, char **argv) {
    long long size = 0;
    for (int i = 1; i < argc; i++) {
        struct stat file;
        if (lstat(argv[i], &file) == -1)
            continue;
        if (!S_ISREG(file.st_mode))
            continue;
        if (access(argv[i], X_OK) != 0)
            continue;
        
        size += file.st_size;
    }
    std::cout << size << std::endl;
    return 0;
}

