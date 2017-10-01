#include <iostream>
#include <pthread.h>

long *max, *current;
int *current_id;

struct thread_desc {
	int id;
};

thread_desc *descs;

void *run(void *n) {
	while (*current < *max) {
		while (*current_id != ((thread_desc*)n)->id) {
			sched_yield();
		}
		std::cout << (((thread_desc*)n)->id + 1) << " " << *current << std::endl;
		*current += 1;
		*current_id = 1 - ((thread_desc*)n)->id;
	}
	*current_id = 1 - ((thread_desc*)n)->id;
	return NULL;
}

int main(int argc, char *argv[]) {
    if (argc <= 1) {
        printf("0\n");
        return 0;
    }

    max = new long;
    sscanf(argv[1], "%ld", max);
    current = new long;
    *current = 1;
    current_id = new int;
    *current_id = 0;
    descs = new thread_desc[2];

    if (*max == 1) {
    	// No need to create threads
    	std::cout << "1 1" << std::endl;
    } else {
	    descs[0].id = 0;
	    descs[1].id = 1;

	    pthread_t t1, t2;
	    pthread_create(&t1, NULL, run, (void*)descs);
	    pthread_create(&t2, NULL, run, (void*)(descs + 1));
	    pthread_join(t2, NULL);
	    pthread_join(t1, NULL);
	}

    return 0;
}

