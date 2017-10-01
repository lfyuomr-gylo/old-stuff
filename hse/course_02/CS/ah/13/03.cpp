#include <stdio.h>

int main() {
    unsigned long long result = 0;
    unsigned s, f;
    while(scanf("%x-%x %*[^\n]", &f, &s) == 2)
        result += s - f;
    printf("%lld\n", result);
    return 0;
}

