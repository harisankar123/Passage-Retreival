# eliminate psg number from the output
# eliminate duplicate docs
fnew = open("2.out","w")
seen = []
with open("2.out.psg") as file:
    for line in file:
        line = line.strip()
        list = line.split(" ")
        key = list[0] + " " + list[2]
        if key not in seen:
            fnew.write(" ".join([list[0], list[1], list[2], list[4], list[5], list[6]]) + "\n")
            seen.append(key)
fnew.close()