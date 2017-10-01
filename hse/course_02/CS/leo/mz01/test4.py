import subprocess
import random

def make_test(size):
    input_file = open("input4", "w")
    input_file.write(str(size) + '\n')
    array = []
    for i in range(size):
        array.append(random.random() * 10 ** 5)
        input_file.write(str(array[i]) + '\n')
    
    input_file.close()
    return array


for i in range(1, 1024):
    for j in range(i ** 2):
        data = make_test(i)
        sorted_data = list(data)
        proc = subprocess.Popen("./4 < input4", shell=True, stdout=subprocess.PIPE)
        prog_return = proc.communicate()[0].decode("utf-8").split('\n')
        prog_return.pop()
        result = [float(x.split()[2]) for x in prog_return]
        sorted_result = sorted(result)
        sorted_result.reverse()
        if (result != sorted_result):
            print("Error was found")
            input()
        
