#include <fstream>
#include <string>

using namespace std;

int main() {
    ofstream lol("rrrr");
    string a = "Hello \n world \n";
    lol << a;
    return 0;
}
