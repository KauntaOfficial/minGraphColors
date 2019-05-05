# Benjamin Chappell
# Only deletes the results from groups.txt bc I'm too lazy to do it manually

def main():
    groups = open("groups.txt", "w")
    groups.seek(0)
    groups.truncate()

if __name__ == "__main__":
    main()