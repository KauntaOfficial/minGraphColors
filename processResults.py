# Benjamin Chappell

import os

kmeans = 'octave kMeans.m'

def main(kmeans):
    results = open("currentResults.txt", "r")
    toReturn = open("groups.txt", "a")

    # Run kmeans on the current data set as defined by the stuff in kmeans.m
    os.system(kmeans)

    #Get all of the results.
    resultLines = results.readlines()

    # Get a list of the unique centroids.
    resultSet = list(set(resultLines))
    for i in range(0, len(resultSet)):
        resultSet[i] = int((resultSet[i][:len(resultSet[i]) - 1]))
    resultSet.sort()

    groups = [[] for i in range(0, len(resultSet))]

    for i in range(0, len(resultLines)):
        idx = int(resultLines[i])
        groups[idx - 1].append(i)

    for i in range(0, len(groups)):
        toReturn.write(str(i) + " ")

        for j in groups[i]:
            toReturn.write(str(j) + " ")
        toReturn.write("\n")
    
    toReturn.write("\n")
    toReturn.close()

if __name__ == "__main__":
    main(kmeans)