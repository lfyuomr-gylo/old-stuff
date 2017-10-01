#include <bits/stdc++.h>
/*
struct Item {
    Item *next;
    long long value;
};*/

class Arena {
 public:
    explicit Arena(size_t size):
            head_(new Item[size / sizeof(Item)]),
            ffree_(NULL) {
        ssize_t count = size / sizeof(Item);
        if (count) {
            ffree_ = head_;

            count--;
            head_[count] = Item();
            head_[count].next = nullptr;
            count--;
            for (; count + 1; count--) {
                head_[count] = Item();
                head_[count].next = head_ + count + 1;
            }
        }
        else {
            ffree_ = nullptr;
        }
    }

    ~Arena() {
        delete[] head_;
    }

    Item *get() {
        if (!ffree_)
            return nullptr;
        
        Item *res = ffree_;
        ffree_ = ffree_->next;
        return res;
    }

    void put(Item *it) {
        it->next = ffree_;
        ffree_ = it;
    }

 private:
    Arena(const Arena&) = delete;
    void operator=(const Arena&) = delete;
    
    Item *head_;
    Item *ffree_;
};
/*
int main() {
    return 0;
}*/
