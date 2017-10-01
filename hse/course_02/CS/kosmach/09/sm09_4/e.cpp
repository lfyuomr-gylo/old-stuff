#include <fstream>
#include <iostream>
#include <string>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <ctype.h>

using namespace std;

struct node
{
    int key;
    int left_idx;
    int right_idx;
};

int main() {
    string a = "mmm";
    int fd = open(a.c_str(), O_WRONLY);
    node nodes[6];
    nodes[0].key = 10;
    nodes[0].left_idx = 1;
    nodes[0].right_idx = 2;

    nodes[1].key = 5;
    nodes[1].left_idx = 4;
    nodes[1].right_idx = 0;

    nodes[2].key = 15;
    nodes[2].left_idx = 3;
    nodes[2].right_idx = 5;

    nodes[3].key = 12;
    nodes[3].left_idx = 0;
    nodes[3].right_idx = 0;

    nodes[4].key = 2;
    nodes[4].left_idx = 0;
    nodes[4].right_idx = 0;

    nodes[5].key = 23;
    nodes[5].left_idx = 0;
    nodes[5].right_idx = 0;

    write(fd, nodes, sizeof(node) * 6);
    close(fd);
    return 0;
}
