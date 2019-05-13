# Benjamin Chappell

readf = open("groups.txt", "r")
writef = open("groupCount.txt", "w")

def main(readf, writef):
    groups = readf.readlines()

    for i in range(0, len(groups)):
        groups[i] = groups[i].split()
        for j in range(0, len(groups[i])):
            groups[i][j] = int(groups[i][j])

    groupLengths = []
    for i in range(0, len(groups) - 1):
        writef.write(str(i + 1) + " - " + str(len(groups[i]) - 1) + "\n")
        groupLengths.append(len(groups[i]) - 1)

    print groupLengths

    average = sum(groupLengths) / len(groupLengths)
    writef.write("Average: " + str(average)

    print groupLengths

if __name__ == "__main__":
    main(readf, writef)
