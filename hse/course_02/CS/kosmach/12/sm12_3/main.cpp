#include <iostream>
#include <sys/types.h>
#include <dirent.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include <string>

int main(int argc, char ** argv)
{
    DIR * folder = opendir(argv[1]);
    struct dirent * file;
    long long int result = 0;
    struct stat file_info;// = new struct stat;
    while( (file = readdir(folder)) != NULL ) {
        std::string path;
        path.append(argv[1]);
        path.append("/");
        path.append(file->d_name);
        int res = lstat(path.c_str(), &file_info);
        if(res == 0 && S_ISREG(file_info.st_mode) && !S_ISLNK(file_info.st_mode)) {
            result += file_info.st_size;
        }
    }
    closedir(folder);
    std::cout << result << std::endl;
    return 0;
}

