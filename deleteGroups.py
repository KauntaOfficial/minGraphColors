# Benjamin Chappell
# Only deletes the results from groups.txt bc I'm too lazy to do it manually

def main():
    groups = open("groups.txt", "w")
    groupsLines = open("groupsInOrder.txt", "w")
    groups.seek(0)
    groups.truncate()
    groupsLines.seek(0)
    groupsLines.truncate()

if __name__ == "__main__":
    main()