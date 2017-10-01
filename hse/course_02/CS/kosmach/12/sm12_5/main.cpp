#include <iostream>
#include <vector>
#include <algorithm>
#include <sys/types.h>
#include <dirent.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
 #include <strings.h>

using namespace std;

bool my_comp(string a, string b ) {
    return (strcasecmp(b.c_str(), a.c_str()) > 0);
}

void go_deeper(const char * folder_name, int bububu) {
    DIR * folder = opendir(folder_name);
    struct dirent * file;

    vector<string> list_dir;
    struct stat file_info;// = new struct stat;
    while( (file = readdir(folder)) != NULL ) {
        std::string path;
        path.append(folder_name);
        path.append("/");
        path.append(file->d_name);

        if((access(path.c_str(), R_OK) == 0)) {

            int res = lstat(path.c_str(), &file_info);
            if(res == 0 && S_ISDIR(file_info.st_mode) && (access(path.c_str(), R_OK) == 0)) {
                list_dir.push_back(string(file->d_name));
            }
        }
    }

    sort(list_dir.begin(), list_dir.end(), my_comp);

    for(size_t i = 2; i < list_dir.size(); i++) {
        cout << "cd " << list_dir[i] << endl;
        std::string path;
        path.append(folder_name);
        path.append("/");
        path.append(list_dir[i].c_str());
        go_deeper(path.c_str(), 0);
    }

    if(bububu == 0) cout << "cd .." << endl;
    closedir(folder);

}

int main(int argc, char ** argv) {
    if(access(argv[1], R_OK) == 0) {
        go_deeper(argv[1], 1);
    }
    return 0;
}

