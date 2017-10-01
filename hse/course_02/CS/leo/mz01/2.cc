#include <bits/stdc++.h>

void counter(std::ifstream& istream) {
    char s;
    std::vector<size_t> counts(10);
    while (!istream.eof()) {
        istream.get(s);
        if (!istream.eof() && s >= '0' && s <= '9')
            counts[s - '0']++;
    }

    for (size_t i = 0; i < counts.size(); i++)
        std::cout << i << ' ' << counts[i] << std::endl;
}

int main() {
    std::string path;
    std::getline(std::cin, path);
    std::ifstream stream(path.c_str());
    
    counter(stream);

    return 0;
}
