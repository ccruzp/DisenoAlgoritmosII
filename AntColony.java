import java.util.Scanner;
import java.util.Arrays;

public class AntColony {
    // Algorithm parameters
    // Original amount of trail
    double c = 1.0;
    // Trail preference
    double alpha = 1;
    // Greedy preference
    double beta = 5;
    // Trail evaporation rate
    double evaporation = 0.5;
    // New trail deposit coefficient
    double Q = 500;
    // Number of ants used = numAntFactor * numNodes
    double numAntFactor = 0.8;
    // Probability of selecting the next town completely random
    double pr = 0.01;
    // Number of iterations
    int maxIter = 2000;

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
    // Ant's probability of going to some city
    double probs[] = null;
    // Best solution find so far
    int[] bestSolution = null;
    // Best solution's cost
    double bestCost;

    int currentIndex = 0;
    Random rand = new Random();

    public AntColony() {
	costs = Reader.readInput(); // Read the costs matrix.
	numNodes = costs.length; // Number of nodes (cities).
	numAnts = numAntFactor * numNodes; // Number of ants.
	trails = new double[numNodes][numNodes]; // Matrix for the ant's trails for every node pair
	probs = new double[numNodes]; // Array for the probability of moving to a certain node
	ants = new Ant[numAnts]; // Array of ants.
	for(int i = 0; i < numAnts; ++i) {
	    ants[i] = new Ant();
	}
    }

    public boolean probTo(Ant ant) {
	int i = ant.path[currentIndex];
	double denom = 0.0;
	double numerator;
	for(int j = 0; j < numNodes; ++j) {
	    if(!ant.visited(j)) {
		denom += pow(trails[i][j], alpha) * pow(1.0 / costs[i][j], beta);
	    }
	}
	for(int j = 0; j < numNodes; ++j) {
	    if(ant.visited[j]) {
		probs[j] = 0;
	    } else {
		numerator = pow(trails[i][j], alpha) * pow(1.0 / costs[i][j], beta);
		probs[j] = numerator / denom;
	    }
	}
	return true;
    }

    public int selectNextNode(Ant ant) {
	if(rand.nextDouble() < pr) { 
	    int t = rand.nextInt(numNodes - currentIndex); // random Node
	    int j = -1;
	    for(int i = 0; i < numNodes; ++j) {
		if(!ant.visited[i]) {
		    ++j;
		}
		if(j == t) {
		    return i;
		}
	    }
	}
	probTo(ant);
	double r = rand.nextDouble();
	double tot = 0;
	for(int i = 0; i < numNodes; ++i) {
	    tot += probs[i];
	    if(tot >= r) {
		return i;
	    }
	}
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
	    double contribution = Q / ants[i].getCost();
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
	while(currentIndex < numNodes-1) {
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
	if(bestSolution == null) {
	    bestSolution = ants[0].path;
	    bestCost = ants[0].getCost();
	}
	for(int i = 0; i < numAnts; ++i) {
	    newCost = ants[i].getCost();
	    if(newCost <  bestCost) {
		bestCost = newCost;
		bestSolution = ants[i].path.clone();
	    }
	}
	return true;
    }

    public int[] solve() {
	// Clear trails
	for(int i = 0; i < numNodes; ++i) {
	    for(int j = 0; j < numNodes; ++j) {
		trails[i][j] = c;
	    }
	}
	int iter = 0;
	while(iter < maxIter) {
	    setupAnts();
	    moveAnts();
	    updateTrails();
	    updateBestSolution();
	    ++iter;
	}
	return bestSolution.clone();
    }

    public static void main(String[] args) {
	AntColony antColony = new AntColony();
	AntColony.solve();
    }
}

class Ant {
    int[] path;
    boolean[] visited;

    public Ant(int numNodes) {
	path = new int[numNodes];
	visited = new boolean[numNodes];
    }

    public boolean visitNode(int node, int currentIndex) {
	path[currentIndex + 1] = node;
	visited[node] = true;
	return true;
    }

    public boolean isVisited(int node) {
	return visited[node];
    }

    public double getCost(double[][] costs) {
	double cost = 0;
	for(int i = 0; i < costs.length-1; ++i) {
	    cost += costs[path[i]][path[i+1]];
	}
	return cost;
    }

    public boolean clear() {
	for(int i = 0; i < visited.length; ++i) {
	    visited[i] = false;
	}
	return true;
    }
}

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
	scanner.nextInt();
	
        
	for(int i = 0; i < numNodos; ++i) {
	    
	    for(int j = 0; j < numNodos; ++j) {
		l[i][j] = scanner.nextDouble();
	    }
	    // scanner.nextInt();
	    // scanner.nextInt();
	}
	//Last isolated case
	for(int j = 0; j < numNodos; ++j) {
	    l[numNodos-1][j] = scanner.nextDouble();
	}
            
	for (int i = 0; i < numNodos; i++) {
	    l[i][i] = Integer.MAX_VALUE;
	}
        
	return l;
    }
}