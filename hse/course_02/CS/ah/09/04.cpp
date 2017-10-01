#include <stdio.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>

struct node {
    int key;
    int l_id;
    int r_id;
};

ssize_t read_buffer(int fd, char *buffer, size_t size) {
    ssize_t red = 0;
    while (red < (ssize_t)size) {
        ssize_t cred = read(fd, buffer + red, size - red);
        red += cred;
        if (cred == 0 || cred == -1)
            break;
    }
    return red;
}

node read_node(int fd, int pos) {
    union {
        char buf[sizeof(node)];
        node st;
    };
    lseek(fd, sizeof(node) * pos, SEEK_SET);
    read_buffer(fd, buf, sizeof(node));
    return st;
}

void dfs(int fd, int pos) {
    node root = read_node(fd, pos);
    
    if(root.r_id)
        dfs(fd, root.r_id);
    
    printf("%d\n", root.key);
    
    if(root.l_id)
        dfs(fd, root.l_id); 
}

int main(int argc, char **argv) {
    int fd = open(argv[1], O_RDONLY);
    dfs(fd, 0);
    close(fd);
    return 0;
}
