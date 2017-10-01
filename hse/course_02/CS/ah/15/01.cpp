#include <stdio.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/wait.h>
#include <stdlib.h>

int main(int argc, char *argv[]) {

    int fd = open(argv[2], O_RDONLY);

    if (fd < 0)
        printf("could not open file\n");
    if (dup2(fd, 0) != 0)
        printf("could not dup2\n");

    close(fd);


    int fd2 = open(argv[3], O_CREAT | O_TRUNC | O_RDWR, 0666);
    
    if (fd2 < 0)
        printf("could not open file");
    if (dup2(fd2, 1) != 0)
        printf("could not dup2");
    
    close(fd2);

    execlp(argv[1], argv[1], (char*)0);

    return 0;
}
