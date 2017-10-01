#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <stdlib.h>
#include <string.h>
#include <stdio.h>

#define DATA_SIZE 12llu
struct Data {
    int x;
    long long y;
};

int readstruct(int fd, struct Data *result) {
    size_t count = 0;
    
    char buf[DATA_SIZE];

    while (count < DATA_SIZE) {
        ssize_t cur = read(fd, buf + count, DATA_SIZE - count);
        if (cur == -1 || cur == 0)
            return -1;
        count += cur;
    }

    memcpy(&(result->x), buf, sizeof(int));
    memcpy(&(result->y), buf + sizeof(int), sizeof(long long));
    
    return 0;
}

int writestruct(int fd, const struct Data *data) {
    size_t count = 0;
    
    char buf[DATA_SIZE];
    memcpy(buf, &(data->x), sizeof(int));
    memcpy(buf + sizeof(int), &(data->y), sizeof(long long));
    
    while (count < DATA_SIZE) {
        ssize_t cur = write(fd, buf + count, DATA_SIZE - count);
        if (cur == -1 || cur == 0)
            return -1;
        count += cur;
    }
    return 0;
}

void fswap(int fd, size_t i, size_t j, int64_t a) {
    struct Data data1, data2;
    lseek(fd, i * DATA_SIZE, SEEK_SET);
    readstruct(fd, &data1);
    lseek(fd, j * DATA_SIZE, SEEK_SET);
    readstruct(fd, &data2);

    data1.y += a * data1.x;
    data2.y += a * data2.x;

    lseek(fd, i * DATA_SIZE, SEEK_SET);
    writestruct(fd, &data2);
    lseek(fd, j * DATA_SIZE, SEEK_SET);
    writestruct(fd, &data1);
}
/*
void printfile(int fd) {
    lseek(fd, 0, SEEK_SET);
    ssize_t cur = 0;
    struct Data data;
    for (;;) {
        cur = readstruct(fd, &data);
        if (cur == -1)
            break;
        printf("x = %d, y = %lld\n", data.x, data.y);
    }
    lseek(fd, 0, SEEK_SET);
}*/
/*
void test() {
    struct Data data1, data2;
    data1.x = 1, data1.y = -7;
    data2.x = 0, data2.y = 12;
    

    int fd = open("thith.test", O_RDWR, 0600);
    if (writestruct(fd, &data1) == -1)
        printf("shit happens\n");
    if (writestruct(fd, &data2) == -1)
        printf("shit happens\n");
    close(fd);
}
*/
int main(int argc, char **argv) {
//    test();
///*
    int fd = open(argv[1], O_RDWR);
    int32_t a = strtol(argv[2], NULL, 10);
    struct Data data;
    ssize_t cur = 0;
    size_t size = 0;


    // calculate number of strictires in file
    for (; cur != -1; size++)
        cur = readstruct(fd, &data);
    size--;
    if (size == 0)
        return 0;

    for (size_t i = 0; i * 2 < size; i++)
        fswap(fd, i, size - i - 1, a);

    close(fd);
    return 0;
//*/
}
