#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>

struct file_descr {
    uid_t uid_;
    gid_t gid_;
    unsigned perm_;
    char name_[1020];
};


void parse_fdescr(char *buf, struct file_descr *result) {
    int cur = 0;
    sscanf(buf, "%u%u%o%n", &result->uid_, &result->gid_, &result->perm_, &cur);
    
    int i = strlen(buf) - 1;
    while (buf[i] == ' ')
        i--;
    while (buf[cur] == ' ')
        cur++;

    printf("cur = %d, i = %d\n\n", cur, i);
    int j = 0;
    for (;j + cur <= i; j++)
        result->name_[j] = buf[cur + j];
    result->name_[j] = '\0';
}

int main() {
    char buf[50];
    strcpy(buf, "1    3 10   thith  ");
    struct file_descr thith;
    parse_fdescr(buf, &thith);
    printf("uid = %u\ngid = %u\nperm = %d\nname = %s|\n", thith.uid_, thith.gid_, thith.perm_, thith.name_);
    
    return 0;
}
