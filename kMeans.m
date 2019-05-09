## Copyright (C) 2019 ben
## 
## This program is free software; you can redistribute it and/or modify it
## under the terms of the GNU General Public License as published by
## the Free Software Foundation; either version 3 of the License, or
## (at your option) any later version.
## 
## This program is distributed in the hope that it will be useful,
## but WITHOUT ANY WARRANTY; without even the implied warranty of
## MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
## GNU General Public License for more details.
## 
## You should have received a copy of the GNU General Public License
## along with this program.  If not, see <http://www.gnu.org/licenses/>.

## -*- texinfo -*- 
## @deftypefn {} {@var{retval} =} kMeans (@var{input1}, @var{input2})
##
## @seealso{}
## @end deftypefn

## Author: ben <ben@ben-Inspiron-5575>
## Created: 2019-05-02

clear; close all; clc

% Load dataset.
X = load('1000.1.txt');
vertexCount = size(X, 1);

% Select an initial set of centroids
K = 200; % Arbitrary starting centroid count
maxIters = 15; % Arbitrary amount of iterations.
initialCentroids = kMeansInitCentroids(X, K);

% Find the closest centroids ot the examples using the init centroids.
idx = findClosestCentroids(X, initialCentroids);

% Run K-Means Algorithm. The false at the end tells the function not to plot the progress.
[centroids, idx] = runkMeans(X, initialCentroids, maxIters, false);

% Open the output file for writing.
output = 'currentResults.txt';
outputFile = fopen('currentResults.txt', 'w');

fprintf(outputFile, "%i\n", idx);
fclose(outputFile);