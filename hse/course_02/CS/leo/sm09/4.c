#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <stdio.h>

struct node {
    int key_;
    int l_;
    int r_;
};

int fullread(int fd, struct node *p) {
    char buf[12];
    size_t amount = 0;
    while (amount < sizeof(struct node))
        amount += read(fd, buf + amount, sizeof(struct node) - amount);

    memcpy(p, buf, amount);
    return 0;
}

void print_tree(int fd, size_t n) {
    lseek(fd, n * sizeof(struct node), SEEK_SET);
    struct node vert;
    fullread(fd, &vert);

    if (vert.r_)
        print_tree(fd, vert.r_);
    printf("%d\n", vert.key_);
    if (vert.l_)
        print_tree(fd, vert.l_);
}

int main(int argc, char ** argv) {
    int fd = open(argv[1], O_RDONLY);
    print_tree(fd, 0);
    close(fd);

    return 0;
}
