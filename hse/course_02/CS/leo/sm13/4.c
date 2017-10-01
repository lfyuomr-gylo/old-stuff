#include <bits/stdc++.h>
/*
void print_bin(uint32_t n) {
    printf("n = ");
    for (int i = 31; i > -1; i--) {
        printf("%d", (n >> i) & 1);
        if (i == 12 || i == 22)
            printf(" ");
    }
    printf("\n");
    n &= (1 << 22) - 1;
    printf("n & (1 << 22 - 1) = ");
    for (int i = 31; i > -1; i--) {
        printf("%d", (n >> i) & 1);
        if (i == 12 || i == 22)
            printf(" ");
    }
    printf("\n");
}
*/
void account(std::set<uint16_t> &tables, uint32_t begin, uint32_t end) {
    uint16_t ftable = begin >> 22, ltable = end >> 22;
    if ((end & ((1 << 22) - 1)) == 0)
        ltable--;

    for (; ftable <= ltable; ftable++)
        tables.insert(ftable);
}

int main() {
    uint32_t p1 = 0, p2 = 0;
    std::set<uint16_t> tables;
    while (scanf("%x%*c%x%*[^\n]\n", &p1, &p2) == 2)
        account(tables, p1, p2);
    printf("%llu\n", (tables.size() + 1) * 4096llu);

    return 0;
}
