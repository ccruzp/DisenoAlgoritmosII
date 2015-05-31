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
    static int numIterations = 1;
    static int tabuLength = 10;

    public static void main(String[] args) {
	TSP tsp = new TSP(Reader.readInput());
	TabuList tabu = new TabuList(tsp.numNodes, tabuLength);
	int currentSol[] = new int[tsp.numNodes+1];
	for(int i = 1; i <= tsp.numNodes; ++i) {
	    currentSol[i] = i;
	}
	System.arraycopy(currentSol, 0, tsp.solution, 0, tsp.numNodes+1);
	double bestCost = TSP.getCost(tsp, tsp.solution);
	for(int i = 0; i < numIterations; ++i) {
	    System.out.println("Iteración #" + i);
	    // for(int j = 0; j <= tsp.numNodes; ++j) {
	    // 	System.out.print(" " + currentSol[j]);
	    // }
	    // System.out.println();
	    // System.out.println();
	    // System.out.println();
	    currentSol = TabuSearch.getBestNeighbour(tabu, tsp, currentSol);
	    // for(int j = 0; j <= tsp.numNodes; ++j) {
	    // 	System.out.print(" " + currentSol[j]);
	    // }
	    System.out.println();
	    double currentCost = TSP.getCost(tsp, currentSol);
	    // System.out.printf("Current: " + currentCost + "\nBest: " + bestCost + "\n");
	    // System.out.println(currentCost - bestCost);
	    if(Double.compare(currentCost, bestCost) < 0) {
		System.out.println("Entré");
		System.arraycopy(currentSol, 0, tsp.solution, 0, tsp.numNodes);
		bestCost = currentCost;
	    }
	}
	System.out.print("La mejor solución es:");
	for(int i = 0; i < tsp.solution.length; ++i) {
	    System.out.print(" " + tsp.solution[i]);
	}
    }

    public static int[] getBestNeighbour(TabuList tabu, TSP tsp, int[] currentSol) {
	int[] bestSol = new int[currentSol.length];
	System.arraycopy(currentSol, 0, bestSol, 0, bestSol.length);
	double bestCost = TSP.getCost(tsp, currentSol);
	int city1 = 0;
	int city2 = 0;
	boolean first = true;
	for(int i = 1; i <= tsp.numNodes; ++i) {
	    for(int j = 2; j <= tsp.numNodes; ++j) {
		if (i == j) continue;
		int[] newSol = new int[bestSol.length];
		System.arraycopy(currentSol, 0, newSol, 0, bestSol.length);
		newSol = swap(i, j, currentSol);
		double newCost = TSP.getCost(tsp, newSol);

		if((newCost > bestCost || first) && tabu.list[i][j] == 0) {
		    first = false;
		    city1 = i;
		    city2 = j;
		    System.arraycopy(newSol, 0, bestSol, 0, bestSol.length);
		    bestCost = newCost;
		}
	    }
	}
	System.out.println("HOLA");
	for(int k = 0; k <= tsp.numNodes; ++k) {
	    System.out.print(" " + currentSol[k]);
	}

	if(city1 != 0) {
	    tabu.decrementTabu();
	    tabu.add(city1, city2);
	}
	return bestSol;
    }

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

  - costos: Es una matriz de double que guarda en cada posiciÃ³n (i, j) 
            el costo de viajar entre el nodo i y el nodo j, y viceversa.
  - vecinos: Lista ordenada de los nodos mas cercanos a cada nodo.
  - recorrido: define el costo total de hacer un recorrido.
*/
class TSP {
    int numNodes;
    int[] solution;
    double[][] costs;
    // double bestCost;

    public TSP(double[][] costs) {
	this.costs = costs;
	this.numNodes = costs.length -1;
        // bestCost = 0;
	solution = new int[numNodes+1];
    }

    public static double getCost(TSP tsp, int[] path) {
	double cost = 0;
	for(int i = 1; i < tsp.numNodes-1; ++i) {
	    cost += tsp.costs[path[i]][path[i+1]];
	}
	return cost;
    }
    // public static void main(String args[]) {
    // 	TSP tsp = new TSP(Reader.readInput());
        
    //Crea un camino inicial mediante busqueda de vecinos (Nearest Neighbor)
    // int[] set = new int[numNodos];
    // Arrays.fill(set,0);
    // int start = 30;          //Nodo donde comienza y cierra el ciclo
    // int num = numNodos;
    // int l = start;
    // while (num-- > 1) {
    //     double menor = Double.MAX_VALUE;
    //     int nodoDes = 0;
    //     for(int j = 0; j < numNodos; ++j) {
    //         if (costos[l][j] <= menor && set[j] < 1 && (l != j)) {
    //             menor = costos[l][j];
    //             nodoDes = j;
    //         }
    //     }
    //     caminos[l] = nodoDes;
    //     recorrido += menor;
    //     set[l]++;
    //     set[nodoDes]++;
    //     l = nodoDes;
    
    // }
    // caminos[l] = start;
    // recorrido += costos[l][start];
    // System.out.println("METODO DEL VECINO MAS CERCANO");
    // System.out.println("Costo total= " + recorrido);
    
    // //Optimiza el camino ya creado mediante 2-opt
    // boolean improvement = true;
    // while(improvement) {
    //     improvement = false;
    //     for (int i = 0; i < numNodos-1; i++) {
    //         for (int j = i+1; j < numNodos; j++) {
    //             //busca los candidatos
    //             int i2 = caminos[i];
    //             int j2 = caminos[j];
    //                 double costoActual = costos[i][i2] + costos[j][j2];
    //                 double nuevoCosto = costos[i][j] + costos[i2][j2];
    //                 if (costoActual > nuevoCosto) {
    //                     improvement = true;
    //                     recorrido = recorrido - costoActual + nuevoCosto;
    //                     caminos[i] = j;
    //                     caminos[i2] = j2;
    //                 }
    //         }
    //     }
    // }    
    // System.out.println("OPTIMIZACION 2-OPT");
    // System.out.println("Costo total= " + recorrido);
    // }
}


/*
  Clase: TabuList
  Función: mantiene la lista de los movimientos tabú para la búsqueda tabú.
*/
class TabuList {
    int list[][];
    int tabuSize;

    public TabuList(int numNodos, int tabuSize) {
	list = new int[numNodos+1][numNodos+1];
	this.tabuSize = tabuSize;
	for(int i = 0; i <= numNodos; ++i) {
	    for(int j = 0; j <= numNodos; ++j) {
		list[i][j] = 0;
	    }
	}
    }

    public void add(int node1, int node2) {
	list[node1][node2] = tabuSize;
	list[node2][node1] = tabuSize;
    }

    public void decrementTabu() {
	for(int i = 0; i < tabuSize; ++i) {
	    for(int j = 0; j < tabuSize; ++j) {
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
    public static double[][] readInput() {
        // File file = new File(System.in);
        // try {
            
            // Scanner scanner = new Scanner(file);
	    Scanner scanner = new Scanner(System.in);
            int numNodos = scanner.nextInt();
	    // System.out.println("numNffodos: " + numNodos);
            double[][] l = new double[numNodos+1][numNodos+1];

            for(int i = 1; i <= numNodos; ++i) {
                scanner.nextDouble();
                for(int j = 1; j <= numNodos; ++j) {
                    l[i][j] = scanner.nextDouble();
                }
            }
            for (int i = 1; i <= numNodos; i++) {
                l[i][i] = 1000000;
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