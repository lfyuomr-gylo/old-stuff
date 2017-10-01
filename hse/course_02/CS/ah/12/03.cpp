#include <iostream>
#include <sys/stat.h>
#include <sys/types.h>
#include <dirent.h>
#include <unistd.h>

int main(int argc, char **argv) {
    int64_t size = 0;

    DIR *dirdsc;

    if (argc >= 2 && (dirdsc = opendir(argv[1])) != NULL) {
        struct dirent *dirinfo;
        while ((dirinfo = readdir(dirdsc))) {
            struct stat s;

            std::string abspath = argv[1];
            abspath += '/';
            abspath += dirinfo->d_name;
            
            if (lstat(abspath.c_str(), &s) != -1 && S_ISREG(s.st_mode))
                size += s.st_size;
        }
        closedir(dirdsc);
    }

    std::cout << size << "\n";
}
