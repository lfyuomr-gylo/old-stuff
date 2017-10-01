#include <bits/stdc++.h>
#include <sys/stat.h>
#include <unistd.h>
#include <dirent.h>

int main(int argc, char **argv) {
    long long size = 0;
    DIR *dirp = opendir(argv[1]);
    while(dirent *curd = readdir(dirp)) {
        std::string path = argv[1];
        path += "/";
        path += curd->d_name;
        
        struct stat file;
        if (lstat(path.c_str(), &file) == -1)
            continue;
        if (!S_ISREG(file.st_mode))
            continue;

        size += file.st_size;
    }
    
    closedir(dirp);
    std::cout << size << std::endl;
    return 0;
}
