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

int readline(char *buf) {
    /*
     * read next line from stdin
     * the pointer in input stream will be 
     * shifted to next line(next symbol after '\n')
     * return value: 0 if line was read successfully
     *               -1 otherwise
    */
    int outcome = scanf("%[^\n]", buf);
    if (outcome != 1)
        return -1;
    getchar();
    return 0;

}

void read_gids(char *buf, gid_t *gids, size_t *size) {
    *size = 0;
    int cur = 0, len = strlen(buf);
    while (cur < len) {
        int foo;
        sscanf(buf + cur, "%u%n", gids + (*size)++, &foo);
        cur += foo;
    }
}

void parse_fdescr(char *buf, struct file_descr *result) {
    int cur = 0;
    sscanf(buf, "%u%u%o%n", &result->uid_, &result->gid_, &result->perm_, &cur);
    
    int i = strlen(buf) - 1;
    while (buf[i] == ' ')
        i--;
    while (buf[cur] == ' ')
        cur++;

    int j = 0;
    for (;j + cur <= i; j++)
        result->name_[j] = buf[cur + j];
    result->name_[j] = '\0';
}

int in(gid_t *gids, size_t size, uid_t gid) {
    for (size_t i = 0; i < size; i++)
        if (gids[i] == gid)
            return 0;

    return -1;
}

int check_permission(const struct file_descr *file, uid_t puid, gid_t *gids, size_t size, unsigned op) {
    /*
     * return 0 if permission allowed,
     *        -1 itherwise
    */
    if (file->uid_ == puid)
        return (((file->perm_ >> 6) & op) == op) ? 0 : -1;

    if (!in(gids, size, file->gid_))
        return (((file->perm_ >> 3) & op) == op) ? 0 : -1;

    return ((file->perm_ & op) == op) ? 0 : -1;
}

int main() {
    char buf[1021];
    // read process uid
    readline(buf);
    uid_t puid;
    sscanf(buf, "%d", &puid);
   
    // read process gids
    gid_t thith[32];
    size_t size = 0;
    readline(buf);
    read_gids(buf, thith, &size);
    
    // read operation
    readline(buf);
    unsigned operation;
    sscanf(buf, "%u", &operation);

    struct file_descr curfile;
    while (!readline(buf)) {
        parse_fdescr(buf, &curfile);
        if (!check_permission(&curfile, puid, thith, size, operation))
            printf("%s\n", curfile.name_);
    }

    return 0;
}
