#include <iostream>
#include <pthread.h>

void *run(void *n) {
	long i;
	if (std::cin >> i) {
		pthread_t t;
		pthread_create(&t, NULL, run, NULL);
		pthread_join(t, NULL);

		std::cout << i << std::endl;
	}
	return NULL;
}

int main() {
	run(NULL);
    return 0;
}
