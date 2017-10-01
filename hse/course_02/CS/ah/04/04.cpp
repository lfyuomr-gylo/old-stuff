#include <iostream>
#include <string.h>
#include <stack>

class Set {
    bool chars[62];

public:

    Set() { for (int i = 0; i < 62; chars[i++] = 0); };
    Set (std::initializer_list<char> I) {
        for (int i = 0; i < 62; chars[i++] = 0);
        for (auto i: I) {
            add(i);
        }
    };

    /* Adds a symbol to the set. Returns 1 on success and 0 on failure */
    bool add(char s) {
        int pos = code(s);
        if (pos >= 0) {
            chars[pos] = 1;
            return 1;
        } else {
            return 0;
        }
    }

    friend std::ostream& operator << (std::ostream& out, const Set& s) {
        bool not_empty = 0;
        for (int i = 0; i < 62; ++i) {
            if (s.chars[i]) {
                out << s.symbol(i);
                not_empty = 1;
            }
        }
        if (!not_empty)
            out << '#';
        return out;
    }

public:

    Set& operator |= (const Set& r) {
        for (int i = 0; i < 62; ++i) {
            chars[i] = chars[i] || r.chars[i];
        }
        return *this;
    }

    Set& operator &= (const Set& r) {
        for (int i = 0; i < 62; ++i) {
            chars[i] = chars[i] && r.chars[i];
        }
        return *this;
    }

    Set& operator ^= (const Set& r) {
        for (int i = 0; i < 62; ++i) {
            chars[i] = chars[i] != r.chars[i];
        }
        return *this;
    }

    Set operator ~ () {
        Set o;
        for (int i = 0; i < 62; ++i) {
            o.chars[i] = !chars[i];
        }
        return o;
    }

    friend const Set operator | (Set a, const Set& b) {
        return a |= b;
    }

    friend const Set operator & (Set a, const Set& b) {
        return a &= b;
    }

    friend const Set operator ^ (Set a, const Set& b) {
        return a ^= b;
    }

public:

    static int code(char s) {
        if (s >= '0' && s <= '9')
            return s - '0';
        if (s >= 'A' && s <= 'Z')
            return s - 'A' + 10;
        if (s >= 'a' && s <= 'z')
            return s - 'a' + 36;
        return -1; // e.g. an error
    }

    static char symbol(int c) {
        if (c < 10)
            return c + '0';
        if (c < 36)
            return c + 'A' - 10;
        if (c < 62)
            return c + 'a' - 36;
        return '\0'; // e.g. an error
    }

};

int main() {
    std::stack<Set> sets;

    char c;
    Set s;
    bool set_is_empty = 1;
    while (std::cin.read(&c, 1), !std::cin.eof()) {
        if (Set::code(c) != -1) {
            s.add(c);
            set_is_empty = 0;
        } else if (c == '#') {
            set_is_empty = 0;
        } else {
            if (!set_is_empty) {
                sets.push(s);
                s = Set();
            }
            set_is_empty = 1;
        }

        if (c == '|' || c == '&' || c == '^') {
            Set a = sets.top();
            sets.pop();
            if (c == '|')
                sets.top() |= a;
            if (c == '&')
                sets.top() &= a;
            if (c == '^')
                sets.top() ^= a;
        } else if (c == '~') {
            sets.top() = ~sets.top();
        }
    }

    std::cout << sets.top() << std::endl;

    return 0;
}
