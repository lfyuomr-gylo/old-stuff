import subprocess
import random

def test():
    input_file = open("input2", "w")
    amounts = [0 for x in range(10)]
    for i in range(10**2):
        cur_s = random.choice(range(0, 128))
        if (cur_s >= ord('0') and cur_s <= ord('9')):
            amounts[cur_s - ord('0')] += 1

        input_file.write(chr(cur_s))

    input_file.close()
    result_file = open("result2", "w")
    for i, n in enumerate(amounts):
        result_file.write("{0} {1}\n".format(i, n))

    result_file.close()


for i in range(10**9):
    test()
    proc = subprocess.Popen("./2 < command2", shell=True, stdout=subprocess.PIPE)
    out = proc.communicate()[0].rstrip()
    result_file = open("result2", "r")
    res = result_file.read().rstrip()
    result_file.close()
    if (res != out.decode("utf-8")):
        print("error was found!")
        print("python program output:")
        print(res)
        print("\n\nC++ program output:")
        print(out.decode("utf-8"))
        break


