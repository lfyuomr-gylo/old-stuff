#include <stdio.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/wait.h>
#include <stdlib.h>
#include <string>

int main(int argc, char *argv[]) {
    std::string script = "\
#!/usr/bin/python\n\
print(1";
    for (int i = 1; i < argc; i++)
        script = script + " * " + argv[i];
    script += ")\n";

    int fd = creat("script.py", 0777);
    size_t done = 0;
    while (done < script.size()) {
        done += write(fd, script.c_str() + done, script.size() - done);
    }
    close(fd);
    execlp("/bin/sh", "/bin/sh", "-c", "./script.py", NULL);
}
