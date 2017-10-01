#include <bits/stdc++.h>
#include <unistd.h>

class BufStream {
 public:
    BufStream():
            fd_(0),
            buf_(new char[16]),
            cur_(0),
            size_(0),
            eof_(false),
            err_(false) {
        rdbuf();
    }

    BufStream(int fd):
            fd_(fd),
            buf_(new char[16]),
            cur_(0),
            size_(0),
            eof_(false),
            err_(false) {
        rdbuf();
    }

    ~BufStream() {
        delete[] buf_;
    }

    explicit operator bool() {
        return !(cur_ >= size_ && eof_) && !err_;
    }


    char getch() {
        if (cur_ >= size_)
            rdbuf();
        if (!(*this))
            return -1;

        return buf_[cur_++];
    }
 private:
    void rdbuf() {
        if (eof_ || err_)
            return;

        cur_ = 0, size_ = 0;
        while (size_ < 16) {
            ssize_t add = read(fd_, buf_ + size_, 16 - size_);
            if (add == -1) {
                err_ = true;
                return;
            }
            if (add == 0) {
                eof_ = true;
                return;
            }
            
            size_ += add;
        }
    }


    int fd_;
    char *buf_;
    ssize_t cur_;
    ssize_t size_;

    bool eof_;
    bool err_;
};

int main() {
    BufStream stream;
    int64_t cur = 0;
    int64_t result = 0;
    bool minus = false;
    while (stream) {
        char c = stream.getch();
        if (isspace(c)) {
            result += cur;
            cur = 0;
            minus = false;
            continue;
        }
        if (c == '+') {
            result += cur;
            cur = 0;
            minus = false;
            continue;
        }
        if (c == '-') {
            result += cur;
            cur = 0;
            minus = true;
            continue;
        }
        if (isdigit(c)) {
            cur *= 10;
            cur += (minus) ? '0' - c : c - '0';
            continue;
        }
    }
    std::cout << result << std::endl;
    return 0;
}
