#include <stdio.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <ctype.h>
#include <string.h>

using namespace std;

struct node
{
    int key;
    int left_idx;
    int right_idx;
};

void fullread(int fd, struct node &p) {
    char buff[12];
    unsigned int bytes = 0;
    int r;
    do {
        r = read(fd, buff + bytes, 12 - bytes);
        bytes += r;
    } while(bytes < 12);
    memcpy(&p.key, buff, sizeof(p.key));
    memcpy(&p.left_idx, buff + sizeof(p.key), sizeof(p.left_idx));
    memcpy(&p.right_idx, buff + sizeof(p.key) + sizeof(p.left_idx), sizeof(p.right_idx));
}

void tree(int fd, int id) {
    struct node element;
    lseek(fd, id * sizeof(node), SEEK_SET);
    fullread(fd, element);
    if(element.right_idx != 0) tree(fd, element.right_idx);
    printf("%d\n", element.key);
    if(element.left_idx != 0) tree(fd, element.left_idx);
}

int main(int argc, char ** argv)
{
    int fd = open(argv[1], O_RDONLY);
    tree(fd, 0);
    close(fd);
    return 0;
}

