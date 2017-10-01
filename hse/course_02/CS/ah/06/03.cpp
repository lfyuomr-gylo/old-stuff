#include <iostream>

/*
struct Item
{
    Item *next;
    long long value;
};
*/

class Arena {
	Item *items;
	Item *first;

public:
	explicit Arena(size_t size) {
		size /= sizeof(Item);

		items = new Item[size];

		if (size) {
			first = items;
			for (size_t i = 0; i < size - 1; (items + i)->next = (items + i + 1), ++i);
			(items + size - 1)->next = nullptr;
		} else {
			first = nullptr;
		}
	}

	~Arena() {
		delete[] items;
	}

	Item *get() {
		if (first != nullptr) {
			Item *out = first;
			first = first->next;
			return out;
		}
		return first;
	}

	void put(Item *p) {
		p->next = first;
		first = p;
	}
};

/*
int main() {
	Arena A(sizeof(Item) - 1);

	std::cout << sizeof(Item) << '\n';

	Item* a = A.get();
	std::cout << a << '\n';
	Item* b = A.get();
	std::cout << b << '\n';
	Item* c = A.get();
	std::cout << c << '\n';
	Item* d = A.get();
	std::cout << d << '\n';
	Item* e = A.get();
	std::cout << e << '\n';
	Item* f = A.get();
	std::cout << f << '\n';

	return 0;
}
*/
