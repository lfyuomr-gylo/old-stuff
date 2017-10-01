#include <bits/stdc++.h>

bool is_cmd(const std::string &str) {
    if (str.size() != 1)
        return false;

    if (str[0] == '&' || str[0] == '|' || str[0] == '^' || str[0] == '~')
        return true;
    return false;
}

uint64_t do_oper(uint64_t s1, uint64_t s2, char cmd) {
    switch (cmd) {
        case '&':
            return s1 & s2;
        case '|':
            return s1 | s2;
        case '^':
            return s1 ^ s2;
        case '~':
            return ~s1 & ((uint64_t(1) << 62) - 1);
        default:
            return ~uint64_t(0);
    }
}

uint64_t make_set(const std::string &str) {
    if (str[0] == '#')
        return 0u;

    uint64_t result = 0u;
    for (char s : str) {
        if (s >= '0' && s <= '9')
            result |= uint64_t(1) << (s - '0');
        if (s >= 'A' && s <= 'Z')
            result |= uint64_t(1) << (s - 'A' + 10);
        if (s >= 'a' && s <= 'z')
            result |= uint64_t(1) << (s - 'a' + 36);
    }

    return result;
}

void print_set(uint64_t set) {
    if (!set) {
        std::cout << "#";
        return;
    }
    
    for (size_t i = 0; i < 10; i++)
        if ((set >> i) & 1)
            std::cout << char(i + '0');

    for (size_t i = 0; i < 26; i++)
        if (set >> (i + 10) & 1)
            std::cout << char(i + 'A');

    for (size_t i = 0; i < 26; i++)
        if (set >> (i + 36) & 1)
            std::cout << char(i + 'a');
}

int main() {
    std::stack<uint64_t> setstack;
    std::string str;
    uint64_t s1 = 0, s2 = 0;
    while (std::cin >> str) {
        if (is_cmd(str)) {
            s1 = setstack.top();
            setstack.pop();
            if (str[0] != '~') {
                s2 = setstack.top();
                setstack.pop();
            }
            setstack.push(do_oper(s1, s2, str[0]));
        }
        else
            setstack.push(make_set(str));
    }
    print_set(setstack.top());

    return 0;
}
