import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;



/**
 * Program that computes a topological order for a directed graph
 * @author Kimmo
 */
public class TODG {
    
    
    //Edge's present edges in graph
    public static class Edge {
        
        Node start;
        Node end;
        boolean visited;
        
        public Edge(Node a, Node b) {
            start = a;
            end = b;
        }
        
        @Override
        public boolean equals(Object a) {
            Edge e = (Edge)a;
            return e.start == start && e.end == end;
        }
    }
    
    //Node's present nodes in graph. Each node might have edges coming in and leaving it
    public static class Node implements Comparable<Node> {
        int id;
        public HashSet<Edge> in;
        public HashSet<Edge> out;
        
        public Node(int a) {
            id = a;
            in = new HashSet<>();
            out = new HashSet<>();
        }
        
        //adds edge to both nodes
        public Node addEdge(Node a) {
            Edge e = new Edge(this, a);
            out.add(e);
            a.in.add(e);
            return this;
        }
        
        @Override
        public int compareTo(Node other) {
            return id - other.id;
        }
        
    }
    
    public static class NodeComparator implements Comparator<Node> {
        @Override
        public int compare(Node a, Node b) {
            return a.id - b.id;
        }
    }
    
    //Function that searches Arraylist to find node with id value x
    //If it is found, it returns that node, if not, it returns null
    public static Node cycleNodes(ArrayList<Node> g, int x) {
        for(Node node : g ) {
            if (node.id == x) {
                return node;
            }
        }
        return null;
    }
    

    //Graph is given to program in txt file and each line of file is one edge in format "x y"
    //where x is start node and y is end node.
    public static ArrayList<Node> readEdges() {
        
        ArrayList<Node> graph = new ArrayList<>();
        
        try {
           BufferedReader in = new BufferedReader(new FileReader("input.txt"));
           String line;
           String[] tmp;
           while((line = in.readLine()) != null) {
               
               tmp = line.split(" ");
               
               int a = Integer.parseInt(tmp[0]);
               int b = Integer.parseInt(tmp[1]);
        
               //Each node is searched from graph and added if its not found
               Node node1 = cycleNodes(graph, a);
               Node node2 = cycleNodes(graph, b);
               
               if(node1 == null) {
                  node1 = new Node(a);
                  graph.add(node1);
               }
               
               if(node2 == null) {
                  node2 = new Node(b);
                  graph.add(node2);
               }
               
               //Edge is added nodes
               node1.addEdge(node2);
           }
           
        }
        catch(IOException e) {
	    System.out.println("input.txt not found");
	}
        
        //Nodes are arranged to ascending order
        Collections.sort(graph, new NodeComparator());
        
        return graph;
    }

    /**
     *   Program reads txt file and uses Kahn's algorithm to give topological order of graph
     *   or prints out if its cyclic since cyclic graph cant have topological order.
     */
    public static void main(String[] args) {
        // TODO code application logic here
    
        
        //Final result list
        ArrayList<Node> ready = new ArrayList<>();
        
        ArrayList<Node> graph = readEdges();
    
        //Working queue, treeset is used since we want to take out always item with smallest id
        TreeSet<Node> E = new TreeSet();
    
        //First all nodes without incoming edges are added to working queue.
        for(Node n : graph) {
             if(n.in.isEmpty()) {
                 E.add(n);
             }
        }
        
        
        while (!E.isEmpty()) {
            
            Node n = E.pollFirst();
            
            E.remove(n);
            
            ready.add(n);
            
            //Iterate each out going edge from node n
            for(Iterator<Edge> it = n.out.iterator(); it.hasNext();) {
                
                //take next edge
                Edge e = it.next();
                //take node from end of edge
                Node m = e.end;
                
                it.remove();
                //remove edge that was used to find this node
                m.in.remove(e);
                
                //if node has no incoming edges, add it to working queue
                if(m.in.isEmpty()) {
                    E.add(m);
                }
            }
        }
    
       
        boolean cycleFound = false;
        
        //Checks if there are cycles in graph
        for(Node n : graph) {
            if(!n.in.isEmpty()) {
                cycleFound = true;
                break;
            }
        }
        
        //if cycle is found
        if(cycleFound) {
            System.out.println("The graph is not acyclic");
        }
        //if no cycles are found, print results
        else {
            for(int i = 0; i < ready.size(); i++) {
                System.out.print(ready.get(i).id);
                if (i+1 < ready.size()) {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }    
}

