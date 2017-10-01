#include <stdio.h>
#include <set>

using namespace std;

int main() {
    set<int> used;
    unsigned s, f;
    while(scanf("%x-%x %*[^\n]", &f, &s) == 2)
        for(unsigned i = f >> 22; i <= s >> 22; i++)
            used.insert(i);
    printf("%zu\n", (used.size() + 1) * 4096);
    return 0;
}
