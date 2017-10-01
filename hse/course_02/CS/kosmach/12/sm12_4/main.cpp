#include <iostream>
#include <string>

using namespace std;

int main(int argc, char ** argv)
{
    for(int i = 1; i < argc; i++) {
        std::string mode = "rwxrwxrwx";
        for(int j = 0; j < 3; j++) {
            string mm (argv[i]);
            int n = (int)(mm[mm.size()-3+j]-'0');

            for(int e = 0; e < 3; e++) {
                if((n & 0b100) == 0) mode[j*3 + e] = '-';
                n <<= 1;
            }

        }
        cout << mode << endl;
    }
    return 0;
}

