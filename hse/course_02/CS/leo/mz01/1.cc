int findmax(const int data[], int size) {
	int max = 0;
	for (int i = max; i < size; i++)
		if (data[i] > data[max]) 
			max = i;
	
	return max;
}

