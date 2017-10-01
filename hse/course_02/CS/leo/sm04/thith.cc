#include <bits/stdc++.h>
#include <cstdint>

uint64_t to_set (const std::string &strset) {
    uint64_t set = 0;
    if (strset == "#")
        return 0; 
    for (auto ch: strset) {
        if ((ch >= '0') && (ch <= '9'))
            set |= (uint64_t(1) << (ch - '0'));
        if ((ch >= 'A') && (ch <= 'Z'))
            set |= (uint64_t(1) << (ch + 10 - 'A'));
        if ((ch >= 'a') && (ch <= 'z'))
            set |= (uint64_t(1) << (ch + 36 - 'a'));
    }
    std::cout << std::endl << set << std::endl;
    return set;
}

void print_set (uint64_t set) {
    set <<= 2;
    set >>= 2;
    if (set == 0) {
        std::cout << "#";
        return;
    }
    int i = 0; // мб ошибка
    for (; i < 10; i++) {
        if (set & (uint64_t(1) << i))
            std::cout << char('0' + i);
    }
    for (; i < 36; i++) {
        if (set & (uint64_t(1) << i))
            std::cout << char('A' + i - 10);
    }
    for (; i < 62; i++) {
        if (set & (uint64_t(1) << i))
            std::cout << char('a' + i - 36);
    }
}
int main() {
    std::string str;
    std::stack<uint64_t> st;
    uint64_t tmp1, tmp2;
    while (std::cin >> str) {
        if (str == "~") {
            tmp1 = st.top();
            st.pop();
            tmp2 = ~tmp1;
            st.push(tmp2);
        } else if (str == "^") {
            tmp1 = st.top();
            st.pop();
            tmp2 = st.top();
            st.pop();
            tmp2 ^= tmp1;
            st.push(tmp2);
        } else if (str == "&") {
            tmp1 = st.top();
            st.pop();
            tmp2 = st.top();
            st.pop();
            tmp2 &= tmp1;
            st.push(tmp2);
        } else if (str == "|") {
            tmp1 = st.top();
            st.pop();
            tmp2 = st.top();
            st.pop();
            tmp2 |= tmp1;
            st.push(tmp2);
        } else
            st.push(to_set(str));
    }
    print_set(st.top());
    return 0;
}
