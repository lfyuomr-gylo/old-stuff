#include <iostream>
#include <stdio.h>
#include <stdlib.h>
#include <sys/mman.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#include <sys/time.h>

using namespace std;

class LFile {
    friend class File_Factory;
private:
    int _fd;
    char * data;
    explicit LFile();
protected:

    bool init_open(const char * filename);
    bool init_mmap();
public:
    char at(size_t index);
    ~LFile();
};

LFile::LFile() {
    //nothing
}

class File_Factory {
public:
    inline static LFile * get_my_amazing_file(const char *filename) {
        LFile * new_file = new LFile;
        if(!new_file->init_open(filename)) {
            delete new_file;
            return NULL;
        }

        if(!new_file->init_mmap()) {
            delete new_file;
            return NULL;
        }

        return new_file;
    }
};


bool LFile::init_open(const char * filename) {
    _fd = open(filename, O_RDWR, 0);
    size_t ends = lseek(_fd, 0, SEEK_END);
    size_t beigns = lseek(_fd, 0, SEEK_SET);
    if(ends == beigns) { // empty file
        return false;
    }
    if( _fd >= 0 ) {
        return true;
    } else {
        return false;
    }
}

bool LFile::init_mmap() {
    int pagesize = getpagesize();
    void * mp = mmap(NULL, pagesize, PROT_READ | PROT_WRITE, MAP_SHARED, _fd, 0);
    if( mp != MAP_FAILED ) {
        data = (char *)mp;
        return true;
    } else {
        return false;
    }
}

char LFile::at(size_t index) {
    return data[index];
}

LFile::~LFile() {
    close(_fd);
}

int main(int argc, char ** argv) {
    for(int i = 1; i < argc; i++) {
        LFile * file = File_Factory::get_my_amazing_file(argv[i]);
        if(file != NULL) {
            long long int lines = 0;
            size_t in = 0;
            while(file->at(in) != '\0') {
                if(file->at(in) == '\n') {
                    lines++;
                }
                in++;
            }
            if(file->at(in-1) != '\n') {
                lines++;
            }
            cout << lines << endl;
        } else {
            cout << 0 << endl;
        }

    }
    return 0;
}
