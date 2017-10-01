#include <sys/mman.h>
#include <stdio.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <unistd.h>
#include <stdint.h>
#include <fcntl.h>

template<typename T>
class mapped_mem_t {
 public:
    mapped_mem_t(size_t size, int prot, int flag, int fd, off_t offset):
            mem_(reinterpret_cast<T*>(mmap(NULL, size * sizeof(T), prot, flag, fd, offset))),
            size_(size)
    {}

    ~mapped_mem_t() {
        munmap(mem_, size_ * sizeof(T));
    }

    T& operator[](size_t i) {
        return mem_[i];
    }
 private:
     T *mem_;
     size_t size_;
};

int main(int argc, char **argv) {
    struct stat filestat;
    for (int i = 1; i < argc; i++) {
        uint64_t lines = 0;
        int fd = open(argv[i], O_RDONLY);
        fstat(fd, &filestat);
        if(filestat.st_size == 0) {
            printf("0\n");
            continue;
        }
        
        //char *data = (char*) mmap(NULL, filestat.st_size, PROT_READ, MAP_SHARED, fd, 0);
        mapped_mem_t<char> data(filestat.st_size, PROT_READ, MAP_SHARED, fd, 0);
        for (ssize_t cur = 0; cur < filestat.st_size; cur++) {
            if (data[cur] == '\n')
                lines++;
        }
        if (data[filestat.st_size - 1] != '\n')
            lines++;

        printf("%llu\n", lines);
        //munmap(data, filestat.st_size);
    }
    
    return 0;
}
