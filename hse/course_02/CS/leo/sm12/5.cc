#include <bits/stdc++.h>
#include <sys/types.h>
#include <dirent.h>
#include <strings.h>
#include <unistd.h>

class my_comp {
 public:
    bool operator()(const std::string &s1, const std::string &s2) const {
        return strcasecmp(s1.c_str(), s2.c_str()) < 0;
    }
};

void traversal(std::string path) {
    DIR *stream = opendir(path.c_str());
    if (!stream)
        return;

    std::vector<std::string> subdirs;
    while (struct dirent *dir = readdir(stream)) {
        if (!strcmp(dir->d_name, ".") || !strcmp(dir->d_name, ".."))
            continue;
        if (dir->d_type != DT_DIR)
            continue;
        if (access((path + "/" + dir->d_name).c_str(), R_OK))
            continue;
        subdirs.push_back(dir->d_name);
    }
    closedir(stream);

    std::sort(subdirs.begin(), subdirs.end(), my_comp());
    for (auto &cur: subdirs) {
        std::cout << "cd " << cur << std::endl;
        traversal(path + "/" + cur);
        std::cout << "cd .." << std::endl;
    }

}

int main(int argc, char **argv) {
    traversal(argv[1]);
    return 0;
}

