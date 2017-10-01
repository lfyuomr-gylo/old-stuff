#include <iostream>
#include <sstream>
#include <set>

using namespace std;

int main()
{
    set<int> used;
    string a;
    while(getline(cin, a)) {
        istringstream iss(a);
        unsigned long long f, s;
        iss >> hex >> f;
        iss.ignore()>> hex >> s;

        int begin = f >> 22, end = s >> 22;
        for(int i = begin; i <= end; i++) used.insert(i);

    }

    cout << dec << (used.size() + 1) * 4096 << endl;
    return 0;
}
