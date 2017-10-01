#include <iostream>
#include <sstream>
#include <set>
#include <string>

#define READ    4
#define WRITE   2
#define EXECUTE 1

void trim(std::string &str) {
    size_t pos = 0;
    while(pos < str.size() && str[pos] == ' ')
        ++pos;
    str = str.substr(pos, str.size());
    pos = str.size();
    while(pos >= 1 && str[pos - 1] == ' ')
        --pos;
    str = str.substr(0, pos);
}

struct Process {
    int uid;
    std::set<int> guids;

    Process()
    : uid(0)
    , guids()
    {}

    friend std::istream& operator >> (std::istream& in, Process& p) {
        {
            std::string input;
            std::getline(in, input);
            std::istringstream iss(input, std::istringstream::in);
            iss >> p.uid;
        }
        
        {
            std::string input;
            std::getline(in, input);
            std::istringstream iss(input, std::istringstream::in);

            int temp;
            while(iss >> temp) {
                p.guids.insert(temp);
            }
        }

        return in;
    }
};

struct File {
    int uid, guid;
    char perms[3];
    std::string name;

    File()
    : uid(0)
    , guid(0)
    , name()
    { perms[0] = perms[1] = perms[2] = 0; }

    bool check_perms(char wanted, int req_uid, const std::set<int> &req_guids) const {
        int i = 0;
        if (uid == req_uid) {  // is owner
            i = 0;
        } else if (req_guids.count(guid)) {  // is group member
            i = 1;
        } else {
            i = 2;
        }

        return (perms[i] & wanted) == wanted;
    }

    friend std::istream& operator >> (std::istream& in, File& f) {
        in >> f.uid;
        in >> f.guid;

        std::string perms;
        in >> perms;

        if (perms.size() == 2) {
            perms.insert(0, "0");
        } else if (perms.size() == 1) {
            perms.insert(0, "00");
        }

        f.perms[0] = perms[0] - '0';
        f.perms[1] = perms[1] - '0';
        f.perms[2] = perms[2] - '0';

        std::getline(in, f.name);
        trim(f.name);

        return in;
    }
};

int main() {
    Process p;
    std::cin >> p;

    int wanted;
    std::cin >> wanted;

    File f;

    while (std::cin >> f)
        if (f.check_perms(wanted, p.uid, p.guids))
            std::cout << f.name << "\n";

    return 0;
}
