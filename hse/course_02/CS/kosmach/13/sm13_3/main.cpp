#include <iostream>
#include <sstream>

using namespace std;

int main()
{
    unsigned long long result = 0;
    string a;
    while(getline(cin, a)) {
        istringstream iss(a);
        unsigned long long f, s;
        iss >> hex >> f;
        iss.ignore()>> hex >> s;
        result += s-f;
    }
    cout << dec << result << endl;
    return 0;
}

