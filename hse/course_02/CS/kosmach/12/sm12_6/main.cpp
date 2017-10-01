#include <iostream>
#include <set>
#include <sstream>
#include <string>
#include <vector>
#include <stdlib.h>
#include <algorithm>
#include <ctype.h>

using namespace std;

struct process {
    unsigned int uid;
    set<int> gid;
    unsigned int mode;
};

struct file {
    unsigned int uid;
    unsigned int gid;
    unsigned int mode;
    string name;
};

void read_process(process &p) {
    cin >> p.uid;
    string gids, g;
    getline(cin, gids); // not mistake
    getline(cin, gids);
    istringstream gid_stream(gids);
    while(gid_stream >> g) p.gid.insert(strtol(g.c_str(), NULL, 10));
    cin >> oct >> p.mode;
}

void read_files(vector<file> & v) {
    file cur;
    while(cin >> dec >> cur.uid >> cur.gid >> oct >> cur.mode) {
        getline(cin, cur.name);
        cur.name.erase(cur.name.begin(), find_if_not(cur.name.begin(), cur.name.end(), [](char c){return isspace(c);}));
        v.push_back(cur);
    }
}

bool check_file(const process &p, const file &f) {
    if(f.uid == p.uid) { // user process
        if( ((f.mode >> 6) & p.mode) == p.mode ) return true;
    } else if(p.gid.count(f.gid) > 0) { // group process
        if( ((f.mode >> 3) & p.mode) == p.mode ) return true;
    } else { // other
        if ( ((f.mode) & p.mode) == p.mode ) return true;
    }
    return false;
}

int main() {
    process proc;
    vector<file> files;
    read_process(proc);
    read_files(files);
    for(auto file : files) {
        if(check_file(proc, file)) {
            cout << file.name << endl;
        }
    }
    return 0;
}
