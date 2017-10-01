#include <stdio.h>
#include <stdlib.h>
/*
struct Info {
    char name[16];
    int value;
};
*/
struct Item {
    Info info;
    Item* next;
};

Item* list;

void solve() {
    if (!arr_size)
        return;
    
    list = (Item*) malloc(sizeof(Item));
    list->info = arr[arr_size - 1];
    //list->next = (Item*) malloc(sizeof(Item*));
    struct Item* cur = list;
    for (int i = arr_size - 2; i > -1; i--) {
        cur->next = (Item*) malloc(sizeof(Item));
        cur->next->info = arr[i];
        cur = cur->next;
    }
}
/*
int thith = 5;

int main() {
    struct Info *a = (struct Info*) malloc(sizeof(struct Info*));
    a->name[0] = 't';
    a->value = 5;
    struct Info *b = (struct Info*) malloc(sizeof(struct Info*));
    *b = *a;
    a->name[1] = 'h';
    printf("b:%s %d\n", b->name, b->value);
    printf("a:%s %d\n", a->name, a->value);

    return 0;
}*/
