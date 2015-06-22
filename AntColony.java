import java.util.Scanner;
import java.util.Arrays;
import java.util.Random;

public class AntColony {
    // Algorithm parameters
    // Trail preference
    int alpha = 1;
    // Greedy preference
    int beta = 5;
    // Trail evaporation rate
    double evaporation = 0.5;
    // New trail deposit coefficient
    double Q = 1000;
    // Number of ants used = numAntFactor * numNodes
    double numAntFactor = 0.8;
    // Probability of selecting the next town completely random
    double probabilityNextRandom = 0.01;
    // Number of iterations
    int maxIter = 1000;

    // number of nodes
    int numNodes = 0;
    // number of ants
    int numAnts = 0;

    // Cost matrix
    double[][] costs = null;
    // Trail matrix
    double[][] trails = null;
    // Ants
    Ant ants[] = null;
    // Ant's probability of going to each city
    double probs[] = null;
    // Best solution find so far
    int[] bestSolution = null;
    // Best solution's cost
    double bestCost;

    int currentIndex = 0;
    Random rand = new Random();

    // Class constructor of the AntColony
    public AntColony() {
	costs = Reader.readInput(); // Read the costs matrix.
	numNodes = costs.length; // Number of nodes (cities).
	numAnts = (int) (numAntFactor * numNodes); // Number of ants.
	trails = new double[numNodes][numNodes]; // Matrix for the ant's trails for every node pair
	probs = new double[numNodes]; // Array for the probability of moving to a certain node
	ants = new Ant[numAnts]; // Array of ants.
	for(int i = 0; i < numAnts; ++i) {
	    ants[i] = new Ant(numNodes);
	}
    }

    // Method of power created because the Math.pow it's too slow.
    public double power(double a, int b) {
	double answer = a;
	for (int i = 0; i < b; ++i) {
	    answer *= answer;
	}
	return answer;
    }

    // Method to calculate an ant's probability to move to each node.
    public boolean probToMove(Ant ant) {
	int i = ant.path[currentIndex];
	double denom = 0.0;
	double numerator;
	double valor;
	double valor1;
	double valor2;
	for(int j = 0; j < numNodes; ++j) {
	    // if (i != j) {
		// if(!ant.isVisited(j) && costs[i][j] != 0.0) {
	    if(!ant.isVisited(j)) {
		// valor1 = power(trails[i][j], alpha);
		// valor2 = power(1.0 / costs[i][j], beta);
		// valor =  valor1*valor2 ;
		denom += power(trails[i][j], alpha) * power(1.0 / costs[i][j], beta);
		// if ((denom + valor) == 0.0) {
		//     System.out.println("Valor1: " + valor1 + "\nValor2" + valor2);
		// }
		// denom += valor;
		// if(Double.isNaN(denom)) {
		    //     System.out.println("Cost: " + costs[i][j]);
		    // }
		    // System.out.println("DENOM: " + denom);
		}
	    // }
	}
	for(int j = 0; j < numNodes; ++j) {
	    // if (i != j) {
		if(ant.isVisited(j)) {
		    probs[j] = 0.0;
		} else {
		    numerator = power(trails[i][j], alpha) * power(1.0 / costs[i][j], beta);
		    // double x = power(trails[i][j], alpha);
		    // double y = power(1.0 / costs[i][j], beta);
		    // numerator = x * y;
		    // if(Double.isNaN(numerator))	System.out.println("X: " + x + "\nY: " + y);
		    // if(Double.isNaN(numerator)) System.out.println("COSTO: " + costs[i][j]);
		    // if(Double.isNaN(numerator))	System.out.println("(" + i + ", " + j + ")");
		    // System.out.println("DENOM: " + denom);
		    // if(denom == 0.0)
		    // 	denom = 1;

		    if(denom == 0.0) {
			probs[j] = Double.MIN_VALUE;
		    } else {
			probs[j] = numerator / denom;
		    }
		    // if(Double.isNaN(probs[j])) System.out.println("NUM: " + numerator + "\nDENOM: " + denom);
		}
	    // }
	}
	return true;
    }

    public int selectNextNode(Ant ant) {
	if(rand.nextDouble() < probabilityNextRandom) { 
	    int t = rand.nextInt(numNodes - currentIndex); // random Node
	    int j = -1;
	    for(int i = 0; i < numNodes; ++i) {
		if(!ant.isVisited(i)) {
		    ++j;
		}
		if(j == t) {
		    return i;
		}
	    }
	}
	probToMove(ant);
	double r = rand.nextDouble();
	// System.out.println("R: " + r);
	double tot = 0;
	int max = 0;
	for(int i = 0; i < numNodes; ++i) {
	    // System.out.println("Antes TOT: " + tot);
	    // System.out.println("PROB: " + probs[i]);
	    tot += probs[i];
	    if (probs[i] < probs[max]) max = i;
	    if(tot >= r) {
		return i;
	    }
	}
	return max;
	// throw new RuntimeException("Not supposed to get here.");
    }

    public boolean updateTrails() {
	// evaporation
	for(int i = 0; i < numNodes; ++i) {
	    for(int j = 0; j < numNodes; ++j) {
		trails[i][j] *= evaporation;
	    }
	}

	// Ant's trail
	for(int i = 0; i < numAnts; ++i) {
	    // double contribution = Q / ants[i].getCost(costs);
	    double contribution = Q;
	    for(int j = 0; j < numNodes-1; ++j) {
		trails[ants[i].path[j]][ants[i].path[j+1]] += contribution;
	    }
	    // trails[ants[i].path[numNodes-1]][ants[i].path[0]] += contribution;
	}
	return true;
    }

    // Choose nextTown for all ants
    public boolean moveAnts() {
	// Each ant follows the trail
	// System.out.println("\t\tnumNodes: " + numNodes); 
	while(currentIndex < numNodes-1) {
	// System.out.println("\t\tcurrentIndex: " + currentIndex); 
	    for(int i = 0; i < numAnts; ++i) {
		ants[i].visitNode(selectNextNode(ants[i]), currentIndex);
	    }
	    ++currentIndex;
	}
	return true;
    }

    // numAnts ants with random start node
    public boolean setupAnts() {
	currentIndex = -1;
	for(int i = 0; i < numAnts; ++i) {
	    ants[i].clear();
	    ants[i].visitNode(rand.nextInt(numNodes), currentIndex);
	}
	++currentIndex;
	return true;
    }

    public boolean updateBestSolution() {
	double newCost;
	if(bestSolution == null) {
	    bestSolution = ants[0].path;
	    bestCost = ants[0].getCost(costs);
	}
	for(int i = 0; i < numAnts; ++i) {
	    newCost = ants[i].getCost(costs);
	    if(newCost <  bestCost) {
		bestCost = newCost;
		bestSolution = ants[i].path.clone();
	    }
	}
	return true;
    }

    // public int[] solve() {
    public double solve() {
	// Clear trails
	for(int i = 0; i < numNodes; ++i) {
	    for(int j = 0; j < numNodes; ++j) {
		trails[i][j] = 1.0;
	    }
	}
	int iter = 0;
	while(iter < maxIter) {
	    // System.out.println("Iteration: " + iter);
	    setupAnts();
	    // System.out.println("    Seteo");
	    moveAnts();
	    // System.out.println("    Move");
	    updateTrails();
	    // System.out.println("    Trails");
	    updateBestSolution();
	    // System.out.println("    Sol");
	    ++iter;
	}
	System.out.println("Solution: ");
	for(int i = 0; i < numNodes; ++i) {
	    System.out.print(bestSolution[i] + " ");
	}
	// return bestSolution.clone();
	Ant ant = new Ant(bestSolution);
	return ant.getCost(costs);
    }

    public static void main(String[] args) {
	AntColony antColony = new AntColony();
	double sol = antColony.solve();
	System.out.println("Costo: " + sol);
    }
}

// Class for the ants.
class Ant {
    int[] path; // Path the ant followed
    boolean[] visited; // Array that says if an ant has gone to every node

    // Constructor
    public Ant(int numNodes) {
	path = new int[numNodes];
	visited = new boolean[numNodes];
    }

    public Ant(int[] sol) {
	path = sol.clone();
	visited = new boolean[sol.length];
    }

    // Mark a node visited
    public boolean visitNode(int node, int currentIndex) {
	path[currentIndex + 1] = node;
	visited[node] = true;
	return true;
    }

    // Says if a node has been visited
    public boolean isVisited(int node) {
	return visited[node];
    }

    // Calculate the costs of an ant's path
    public double getCost(double[][] costs) {
	double cost = 0;
	for(int i = 0; i < costs.length-1; ++i) {
	    cost += costs[path[i]][path[i+1]];
	}
	return cost;
    }

    // Sets the visited array to false
    public boolean clear() {
	for(int i = 0; i < visited.length; ++i) {
	    visited[i] = false;
	}
	return true;
    }
}

// Class reader
class Reader {
    public Reader() {
	
    }
    
    /*
      MÃ©todo: readInput.
      Función: lee la información por entrada estándar 
      (mediante uno de los archivos .txt creados con Preprocessor)
    */
    public static double[][] readInput() {
	Scanner scanner = new Scanner(System.in);
	int numNodos = scanner.nextInt();
	double[][] l = new double[numNodos][numNodos];
	double read;
  
	for(int i = 0; i < numNodos; ++i) {
	    scanner.nextInt();	    
	    for(int j = 0; j < numNodos; ++j) {

		read = scanner.nextDouble();
		if(read == 0.0) {
		    // System.out.println("(" + i + ", " + j + ")");
		    l[i][j] = 8.0;
		}
		else l[i][j] = read;
		// l[i][j] = scanner.nextDouble();
	    }
	    // scanner.nextInt();
	    // scanner.nextInt();
	}
	//Last isolated case
	// for(int j = 0; j < numNodos; ++j) {
	//     l[numNodos-1][j] = scanner.nextDouble();
	// }
            
	// for (int i = 0; i < numNodos; i++) {
	//     l[i][i] = Integer.MAX_VALUE;
	// }
        
	return l;
    }
}