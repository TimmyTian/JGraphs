# JGraphs
This package holds two Graph implementations, AdjacencyMatrix and AdjacencyList. There is also the option to upload a text file that will build and test each graph type.


The file format for building and testing the graph is as follows

---------------------------------------------------------------- Start of file

* These are comments
* These lines are skipped
*
*
* 
<unweighted OR weighted>
<directed OR undirected>

begin

A B C D * Each must have a space between each vertex

A B * A goes to B
B C * B goes to C
C D * C goes to D

* If the graph is weighted you need to specify the weight 'A B 10' 

end

---------------------------------------------------------------- end of file

Here is an example from file RichardCG.txt


* Author: Richard

weighted
undirected

begin

* Common Cities notice they don't have any spaces in their names
Fort_Collins
Denver Colorado_Springs Pueblo Boulder

* Their connections and appox. distances
Fort_Collins Denver 50
Fort_Collins Boulder 35
Denver Boulder 15
Denver Pueblo 80
Denver Colorado_Springs 55
Colorado_Springs Pueblo 25

end

isConnected     ?
addVertex       Vail                true
addEdge         Vail                Denver              75      true
hasEdge         Denver              Colorado_Springs    ?
hasEdge         Pueblo              Fort_Collins        ?


The above tests have an expexted or ? unexpected outcome. If you ask 'true' 
it will test to see if it returns true, otherwise if you ask with a ? then it will print the answer.
