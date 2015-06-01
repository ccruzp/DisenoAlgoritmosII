/*
LAS ESTRUCTURAS SE USARON PORQUE    
numNodos: Permite la iteracion a traves de los elementos sencillamente,
          sintener que recurrir a funciones de tamaño de arreglos
caminos:  Asigna sencillamente a cada elemento su siguiente. Permite conocer
          el sucesor en orden lineal, lo cual ayuda enormemente para optimizacion 2-opt.
          Tambien otorga la idea de un ciclo dirigido, que tiene mas sentido 
          que con una matriz de adyacencias.
costos:   Permite realizar busquedas de costos en orden constante. Como los costos
          son preprocesados, el costo(redundancia) se amortiza sobre el tiempo
recorrido:bleh

 */
// package tsp;

// import java.io.File;
// import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Arrays;

public class TabuSearch {
    static int numIterations = 2500;
    static int tabuLength = 20;
    static float[][] m = new float[][]{{0, 1, 3, 4, 5}, // Caso de la página para hacer pruebas
		{1, 0, 1, 4, 8},
		{3, 1, 0, 5, 1},
		{4, 4, 5, 0, 2},
		{5, 8, 1, 2, 0}};

    public static void main(String[] args) {
	TSP tsp = new TSP(Reader.readInput()); // Crea el TSP
	// TSP tsp = new TSP(m);

	TabuList tabu = new TabuList(tsp.numNodes, tabuLength); // Crea la lista Tabu
	int currentSol[] = new int[tsp.numNodes+1]; // Almacena la solución actual
	for(int i = 0; i < tsp.numNodes; ++i) {
	    currentSol[i] = i;
	}
	// currentSol = TSP.localSearch(tsp);
	System.arraycopy(currentSol, 0, tsp.solution, 0, tsp.numNodes+1);
	float bestCost = TSP.getCost(tsp, currentSol); // Guarda la mejor solución encontrada hasta ahora.
	for(int i = 0; i < numIterations; ++i) {
	    // System.out.println("Iteración #" + i);
	    currentSol = TabuSearch.getBestNeighbour(tabu, tsp, currentSol); // Busca la mejor solución en la vecindad.
	    float currentCost = TSP.getCost(tsp, currentSol); // Calcula el costo de la solución actual
	    if(currentCost < bestCost) { // Si el costo de la solución actual es menor que el de la mejor solución encontrada hasta ahora, entonces la solución actual es la nueva mejor solución.
		System.arraycopy(currentSol, 0, tsp.solution, 0, tsp.numNodes);
		bestCost = currentCost;
	    }
	}
	System.out.print("La mejor solución es:");
	for(int i = 0; i < tsp.solution.length; ++i) {
	    System.out.print(" " + tsp.solution[i]);
	}
	System.out.println("Costo: " + bestCost);
    }

    // Busca la mejor solución en la vecindad.
    public static int[] getBestNeighbour(TabuList tabu, TSP tsp, int[] currentSol) {
	int[] bestSol = new int[currentSol.length];
	System.arraycopy(currentSol, 0, bestSol, 0, bestSol.length);
	float bestCost = TSP.getCost(tsp, currentSol);
	int city1 = 0;
	int city2 = 0;
	boolean first = true;
	for(int i = 1; i < bestSol.length-1; ++i) {
	    for(int j = 2; j < bestSol.length-1; ++j) {
		if (i == j) continue;
		int[] newSol = new int[bestSol.length];
		System.arraycopy(currentSol, 0, newSol, 0, bestSol.length);
		newSol = swap(i, j, currentSol);

		float newCost = TSP.getCost(tsp, newSol);
		if((newCost < bestCost || first) && tabu.list[i][j] == 0) {
		    first = false;
		    city1 = i;
		    city2 = j;
		    System.arraycopy(newSol, 0, bestSol, 0, bestSol.length);
		    bestCost = newCost;
		}
	    }
	}

	if(city1 != 0) {
	    tabu.decrementTabu();
	    tabu.add(city1, city2);
	}
	return bestSol;
    }
    // Cambia dos ciudades
    public static int[] swap(int city1, int city2, int[] sol) {
	int temp = sol[city1];
	sol[city1] = sol[city2];
	sol[city2] = temp;
	return sol;
    }
}

/*
  Clase: TSP
  FunciÃ³n: implementa el TSP. Esta es la clase principal.
  ParÃ¡metros:
  - numNodos: representa el nÃºmero de nodos que tiene el grafo.
  - listaNodos: es un arreglo que almacena todos los nodos del grafo.
  - caminos: Arreglo que posee para cada elemento, a cual se mueve (la siguiente ciudad a recorrer)

  - costos: Es una matriz de int que guarda en cada posiciÃ³n (i, j) 
            el costo de viajar entre el nodo i y el nodo j, y viceversa.
  - vecinos: Lista ordenada de los nodos mas cercanos a cada nodo.
  - recorrido: define el costo total de hacer un recorrido.
*/
class TSP {
    int numNodes;
    int[] solution;
    float[][] costs;

    public TSP(float[][] costs) {

	this.costs = costs;
	this.numNodes = costs.length;
	solution = new int[numNodes+1];
    }

    // Calcula el costo de recorrer un camino.
    public static float getCost(TSP tsp, int[] path) {
	float cost = 0;
	float s;
	for(int i = 0; i < tsp.numNodes; ++i) {
	    cost += tsp.costs[path[i]][path[i+1]];
	}
	return cost;
    }

    // Local search. No lo logra mucho.
    public static int[] localSearch(TSP tsp) {
        
	int[] set = new int[tsp.numNodes];
	int[] camino = new int[tsp.numNodes];
	float recorrido = 0;
	Arrays.fill(set,0);
	int start = 0;          //Nodo donde comienza y cierra el ciclo
	int num = tsp.numNodes;
	int l = start;
	while (num-- > 1) {
	    float menor = Integer.MAX_VALUE;
	    int nodoDes = 0;
	    for(int j = 0; j < tsp.numNodes; ++j) {
		if (tsp.costs[l][j] <= menor && set[j] < 1 && (l != j)) {
		    menor = tsp.costs[l][j];
		    nodoDes = j;
		}
	    }
	    camino[l] = nodoDes;
	    recorrido += menor;
	    set[l]++;
	    set[nodoDes]++;
	    l = nodoDes;
    
	}
	camino[l] = start;
	recorrido += tsp.costs[l][start];
	System.out.println("METODO DEL VECINO MAS CERCANO");
	System.out.println("Costo total= " + recorrido);
	
	tsp.solution[0] = 0;
	tsp.solution[tsp.numNodes] = 0;
	for(int i = 1; i < camino.length; ++i) {
	    tsp.solution[i] = camino[tsp.solution[i-1]];
	}
	return tsp.solution;
    }
}


/*
  Clase: TabuList
  Función: mantiene la lista de los movimientos tabú para la búsqueda tabú.
*/
class TabuList {
    int list[][];
    int tabuSize;

    public TabuList(int numNodos, int tabuSize) {
	list = new int[numNodos][numNodos];
	this.tabuSize = tabuSize;
	for(int i = 0; i < numNodos; ++i) {
	    for(int j = 0; j < numNodos; ++j) {
		list[i][j] = 0;
	    }
	}
    }

    public void add(int node1, int node2) {
	list[node1][node2] = tabuSize;
	list[node2][node1] = tabuSize;
    }

    public void decrementTabu() {
	for(int i = 0; i < list.length; ++i) {
	    for(int j = 0; j < list.length; ++j) {
		if(list[i][j] != 0) {
		    list[i][j] -= 1;
		}
	    }
	}
    }
}

/*
  Clase: Reader
  FunciÃ³n: procesa el archivo de entrada y crea la lista de nodos.
*/

class Reader {
    public Reader() {

    }

    /*
      Método: readInput.
      FunciÃ³n: lee la información por entrada estándar 
      (mediante uno de los archivos output.txt creados con Preprocessor)
    */
    public static float[][] readInput() {
        // File file = new File(System.in);
        // try {
            
            // Scanner scanner = new Scanner(file);
	    Scanner scanner = new Scanner(System.in);
            int numNodos = scanner.nextInt();
	    // System.out.println("numNffodos: " + numNodos);
            float[][] l = new float[numNodos][numNodos];
	    float v;
	    for (int i = 0; i < numNodos; ++i) {
		for(int j = i; j < numNodos; ++j) {
		    l[i][j] = 0;
		}
	    }
            for(int i = 0; i < numNodos; ++i) {
                scanner.nextFloat();
                for(int j = 0; j < numNodos; ++j) {
		    if(i == j) {
			l[i][j] = Integer.MAX_VALUE;
		    } else {
			v = scanner.nextFloat();
			l[i][j] = v;
			l[j][i] = v;
		    }
                }
            }
            return l;
            
        // }
        // // catch (FileNotFoundException e) {
	// catch (Exception e) {
        //     System.out.println("File not Found");
        // } 
        // return null;
    }
}