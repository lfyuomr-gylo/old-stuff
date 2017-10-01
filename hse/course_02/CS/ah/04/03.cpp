#include <iostream>
#include <fstream>
#include <inttypes.h>
#include <string.h>

using namespace std;

uint32_t read_int32(istream* input) {
    uint32_t out = 0;
    unsigned char* buffer = new unsigned char;
    for (short i = 0; i < 4; ++i) {
        input->read((char*)buffer, 1);
        out = !input->eof() ? (out << 8) + *buffer : 0;
    }
    return out;
}

uint64_t read_number(istream* input) {
    uint64_t out = 0;
    while (!input->eof())
        out += read_int32(input);
    return out;
}

int main(int argc, char ** argv) {
    for (int i = 1; i < argc; ++i) {
        if (strcmp(argv[i], "-") == 0) {
            cout << read_number(&cin) << endl;
        } else {
            ifstream input(argv[i], ios::binary | ios::in);
            cout << read_number(&input) << endl;
            input.close();
        }
    }

    return 0;
}
