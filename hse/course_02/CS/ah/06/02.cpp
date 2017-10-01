#include <iostream>

bool isspace_nnl(char c) {
	return isspace(c) && c != '\n';
}

void normalize_file(char *buf) {
	char *current_read = buf, *current_write = buf, *last_nonspace = buf;
	char *tmp = buf;
	while (*tmp != '\0') {
		++tmp;
		if (*tmp == '\0') {
			--tmp;
			if (*tmp != '\n') {
				++tmp;
				*tmp = '\n';
			}
			++tmp;
			*tmp = '\0';
		}
	}
	while (*current_read != '\0') {
		if (isspace_nnl(*current_read)) {
			last_nonspace = current_read;

			while (isspace_nnl(*current_read)) {
				++current_read;
			}

			if (*current_read == '\n' || *current_read == '\0') {
				last_nonspace = current_read;
			}

			while (last_nonspace != current_read) {
				*current_write = *last_nonspace;
				++current_write;
				++last_nonspace;
			}

			if (*current_read == '\0') {
				break;
			}
		}

		*current_write = *current_read;
		++current_write;
		++current_read;
	}
	*current_write = '\0';
}

/*
int main(int argc, char ** argv) {
    char a[] = "   .  \n  \0  ";

    normalize_file(a);

	std::cout << "'" << a << "'\n";

    return 0;
}
*/
