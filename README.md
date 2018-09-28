# Probabilistic-Search

Consider a landscape represented by a map of cells. The cells can be of various terrain types (‘flat’, ‘hilly’, ‘forested’, ‘a complex maze of caves and tunnels’) representing how difficult they are to search. Hidden somewhere in this landscape is a Target.
Initially, the target is equally likely to be anywhere in the landscape
We have the ability to search a given cell, to determine whether or not the target is there. However, the more difficult the terrain, the harder is can be to search - the more likely it is that even if the target is there, you may not find it (a false negative).
The false positive rate for any cell is taken to be 0

The goal is to locate the target in as few searches as possible by utilizing the results of the observations collected.
