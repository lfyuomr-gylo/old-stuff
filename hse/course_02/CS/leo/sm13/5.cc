#include <bits/stdc++.h>
#include <unistd.h>
#include <cstdint>
#include <elf.h>
#include <sys/mman.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <errno.h>
#include <fcntl.h>


struct MMF {
    char *mem;
    size_t size;
};

void process_header(const Elf32_Ehdr &header) {
    // check if file is not ELF (header.e_ident[:4] != "\x7fELF")
    if (header.e_ident[0] != '\x7f' || header.e_ident[1] != 'E' ||
        header.e_ident[2] != 'L' || header.e_ident[3] != 'F')
        throw std::invalid_argument("Not an ELF file");

    // ----------------

    // check if this file is unsupported
    if (header.e_ident[4] != 1) {
        // word size != 32
        throw std::invalid_argument("Not supported ELF file");
    }
    if (header.e_ident[5] != 1) {
        // unsupported byte order(is not little endian
        throw std::invalid_argument("Not supported ELF file");
    }
    if (header.e_ident[6] != 1) {
        // unknown ELF format version
        throw std::invalid_argument("Not supported ELF file");
    }
    if (header.e_ident[7] != 0 || header.e_ident[8] != 0) {
        // some shit
        throw std::invalid_argument("Not supported ELF file");
    }
    // -------------------

    // display file type
    std::cout << "TYPE: ";
    switch(header.e_type) {
        case 0:
            std::cout << "NONE" << std::endl;
            break;
        case 1:
            std::cout << "REL" << std::endl;
            break;
        case 2:
            std::cout << "EXEC" << std::endl;
            break;
        case 3:
            std::cout << "DYN" << std::endl;
            break;
        case 4:
            std::cout << "CORE" << std::endl;
            break;
        default:
            throw std::runtime_error("Not supported ELF file");
    }
}

struct MMF mmfopen(const char *path) {
    int fd = open(path, O_RDONLY);
    if (fd == -1)
        throw std::runtime_error("Couldn't open input file");

    struct stat file;
    if (lstat(path, &file) == -1)
        throw std::runtime_error("Error while reading ELF Header");

    void* mem = mmap(NULL, file.st_size, PROT_READ, MAP_SHARED, fd, 0);
    if (mem == (void*) -1)
        throw std::runtime_error("Error while reading ELF Header");

    struct MMF result;
    result.mem = reinterpret_cast<char*>(mem);
    result.size = file.st_size;
    return result;
}

void print_sheader(const Elf32_Ehdr &header, const Elf32_Shdr &sheader, MMF file, const char *names_table) {
    printf("%20s 0x%08x %10d %10d 0x%04x\n", names_table + sheader.sh_name, sheader.sh_addr,
           sheader.sh_offset, sheader.sh_size, sheader.sh_addralign);
}

int main(int argc, char **argv) {
    struct MMF file;
    try {
        file = mmfopen(argv[1]);
    }
    catch(std::exception &error) {
        std::cerr << error.what() << std::endl;
        return 1;
    }

    // read ELF header
    Elf32_Ehdr header;
    if (file.size < sizeof(header)) {
        std::cerr << "Error while reading ELF Header" << std::endl;
        return 1;
    }
    else
        memcpy(reinterpret_cast<char*>(&header), file.mem, sizeof(header));
    // ---------------------------

    // process header
    try {
        process_header(header);
    }
    catch(std::exception &err) {
        std::cerr << err.what() << std::endl;
        return 1;
    }
    // ---------------------

    // print section headers table
    printf ("%20s %10s %10s %10s %6s\n", "NAME", "ADDR", "OFFSET", "SIZE", "ALGN");
    Elf32_Shdr sheader;
    memcpy(reinterpret_cast<char*>(&sheader), file.mem + header.e_shoff + header.e_shstrndx * header.e_shentsize, sizeof(sheader));
    char *names = file.mem + sheader.sh_offset;
    auto off = header.e_shoff;
    for (uint32_t cur = 1; cur < header.e_shnum; cur++) {
        off += header.e_shentsize;
        memcpy(reinterpret_cast<char*>(&sheader), file.mem + off, sizeof(sheader));
        print_sheader(header, sheader, file, names);
    }

    munmap(file.mem, file.size);
    return 0;
}
