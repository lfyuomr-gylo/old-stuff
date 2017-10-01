//
// Created by leo on 27.11.15.
//
#include <bits/stdc++.h>
#include <unistd.h>
#include <cstdint>
#include <elf.h>
#include <sys/mman.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <errno.h>
#include <fcntl.h>
#include <stab.h>

struct MMF {
    char *mem;
    size_t size;
};

struct Stab {
    uint32_t n_strx;   // позиция начала строки в секции .strstab
    uint8_t n_type;    // тип отладочного символа
    uint8_t n_other;   // прочая информация
    uint16_t n_desc;   // описание отладочного символа
    uintptr_t n_value; // значение отладочного символа
};

struct func_t {
    bool operator<(const func_t &other) const {
        int foo = strcmp(name.c_str(), other.name.c_str());
        return (foo) ? foo < 0 : start < other.start;
    }

    std::string name;
    size_t index;
    size_t start;
    size_t end;
    std::string source;
};

void mmfopen(const char *path, MMF *file) {
    int fd = open(path, O_RDONLY);
    struct stat info;
    fstat(fd, &info);
    file->mem = reinterpret_cast<char*>(mmap(NULL, info.st_size, PROT_READ, MAP_SHARED, fd, 0));
    file->size = info.st_size;
}

Elf32_Shdr find_header(char *file, const Elf32_Ehdr &header, const char *target) {
    // return 'target' section header
    Elf32_Shdr sheader;
    memcpy(reinterpret_cast<char*>(&sheader), file + header.e_shoff + header.e_shstrndx * header.e_shentsize, sizeof(sheader));
    char *snames = file + sheader.sh_offset;

    for (size_t i = 1; i < header.e_shnum; i++) {
        memcpy(reinterpret_cast<char*>(&sheader), file + header.e_shoff + header.e_shentsize * i, sizeof(sheader));
        if (!strcmp(snames + sheader.sh_name, target))
            return sheader;
    }
    throw std::runtime_error("Section is not found");
}

std::string get_fname(char *name) {
    return std::string(name, strchr(name, ':'));
}

int main(int argc, char **argv) {
    MMF file;
    mmfopen(argv[1], &file);

    Elf32_Ehdr header;
    memcpy(reinterpret_cast<char*>(&header), file.mem, sizeof(header));

    Elf32_Shdr hstab, hstrstab;
    try {
        hstab = find_header(file.mem, header, ".stab");
        hstrstab = find_header(file.mem, header, ".stabstr");
    }
    catch(...) {
        std::cout << "No debug info" << std::endl;
        return 0;
    }

    char *stab = file.mem + hstab.sh_offset, *strstab = file.mem + hstrstab.sh_offset;
    //--------------------


    std::vector<func_t> functions;
    struct Stab cur;
    func_t curfunc;
    std::string curfile;
    bool in_func_now = false;
    for (size_t count = 1; count * sizeof(Stab) < hstab.sh_size; count++) {
        memcpy(reinterpret_cast<char*>(&cur), stab + count * sizeof(cur), sizeof(cur));
        switch(cur.n_type) {
            case N_SO:
                if (in_func_now) {
                    curfunc.end = cur.n_value;
                    functions.push_back(curfunc);
                }
                else
                    curfile = strstab + cur.n_strx;

                in_func_now = false;
                break;
            case N_SOL: // implemented
                curfile = strstab + cur.n_strx;
                break;
            case N_FUN: // implemented
                if (in_func_now) {
                    curfunc.end = cur.n_value;
                    functions.push_back(curfunc);
                }

                curfunc.name = get_fname(strstab + cur.n_strx);
                curfunc.index = count;
                curfunc.start = cur.n_value;
                in_func_now = true;
                break;
            case N_SLINE: // implemented
                if (in_func_now)
                    curfunc.source = curfile;
                break;
            default:
                continue;
        }
    }

    std::sort(functions.begin(), functions.end());

    for (auto function : functions)
        printf("%s %d 0x%08x 0x%08x %s\n", function.name.c_str(),
                                         function.index,
                                         function.start,
                                         function.end,
                                         function.source.c_str());

    munmap(file.mem, file.size);
    return 0;
}
