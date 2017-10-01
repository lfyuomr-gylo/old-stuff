#include <bits/stdc++.h>
#include <unistd.h>
#include <wait.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <wait.h>

int main() {
    for (uint64_t i = 0; i < 1000; i++)
        system("cp /dev/null script.py && chmod +rwx script.py");
    return 0;
}