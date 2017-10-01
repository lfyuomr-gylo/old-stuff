#include <bits/stdc++.h>
#include <unistd.h>
#include <wait.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <wait.h>


int main(int argc, char **argv) {
    //---------script declaration
    std::string script = "\
#!/usr/bin/env python3\n\
import sys\n\
from functools import reduce\n\
print(";
    for (int i = 1; i < argc - 1; i++) {
        script += argv[i];
        script += " * ";
    }
    script += argv[argc - 1];
    script += ")";
    //---------------

    int fd = creat("script.py", 0777);
    fchmod(fd, 0777);
    for (size_t i = 0; i < script.size(); i += write(fd, script.c_str() + i, script.size() - i));
    close(fd);
    execlp("/bin/sh", "/bin/sh", "-c", "./script.py", NULL);

    return 0;
}