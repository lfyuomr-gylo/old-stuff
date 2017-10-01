#include <sys/stat.h>
#include <stdlib.h>
#include <stdio.h>

int main(int argc, char **argv) {
    struct stat file;
    unsigned long long size = 0;
    for (int i = 1; i < argc; i++) {
        int cur = lstat(argv[i], &file);
        if (cur == -1)
            continue;
        if (!S_ISREG(file.st_mode))
            continue;
        if (S_ISLNK(file.st_mode))
            continue;
        if (file.st_nlink != 1)
            continue;

        size += file.st_size;
    }

    printf("%llu\n", size);
    return 0;
}
