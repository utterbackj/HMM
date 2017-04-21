# HMM

Problem: A robot must figure out its position in a maze but doesn't know what movements it has made. However, the tiles of the maze have different colors that the robot can detect, so must use a Hidden Markov Model to approximate the chances it's on any given square.

The visual representation shows a circle for each position; the size of the circle corresponds to the probability the robot is on that square. A forward and backward (smoothed) algorithm is used to determine the probability.
