/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tabusearch;

/*
  LAS ESTRUCTURAS SE USARON PORQUE    
  numNodos: Permite la iteracion a traves de los elementos sencillamente,
  sintener que recurrir a funciones de tamaÃ±o de arreglos
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
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Arrays;

public class TabuSearch {
    static int numIterations = 5;
    static int tabuLength = 20;
    
    public static void main(String[] args) {
	TSP tsp = new TSP(Reader.readInput()); // Crea el TSP
	// TSP tsp = new TSP(m);
	
	TabuList tabu = new TabuList(tsp.numNodes, tabuLength); // Crea la lista Tabu
	int currentSol[] = new int[tsp.numNodes]; // Almacena la soluciÃ³n actual
	// for(int i = 0; i < tsp.numNodes; ++i) {
	//     currentSol[i] = i;
	// }
	// currentSol = TSP.localSearch(tsp);
	currentSol = TSP.nearestNeighbour(tsp);
	System.out.println("Nearest Neighbour");
	for(int i = 0; i < currentSol.length; ++i) {
	    System.out.print(" " + currentSol[i]);
	}
        System.out.println();
	System.arraycopy(currentSol, 0, tsp.solution, 0, tsp.numNodes);
	float bestCost = TSP.getCost(tsp, currentSol); // Guarda la mejor soluciÃ³n encontrada hasta ahora.
	System.out.println("Initial Cost: " + bestCost);
	for(int i = 0; i < numIterations; ++i) {
	    // System.out.println("IteraciÃ³n #" + i);
	    currentSol = TabuSearch.getBestNeighbour(tabu, tsp, currentSol); // Busca la mejor soluciÃ³n en la vecindad.
	    float currentCost = TSP.getCost(tsp, currentSol); // Calcula el costo de la soluciÃ³n actual

	    if(currentCost < bestCost) { // Si el costo de la soluciÃ³n actual es menor que el de la mejor soluciÃ³n encontrada hasta ahora, entonces la soluciÃ³n actual es la nueva mejor soluciÃ³n.
		System.arraycopy(currentSol, 0, tsp.solution, 0, tsp.numNodes);
		bestCost = currentCost;
	    }
	}
	System.out.print("La mejor soluciÃ³n es:");
	for(int i = 0; i < tsp.solution.length; ++i) {
	    System.out.print(" " + tsp.solution[i]);
	}
        System.out.println();
	System.out.println("Costo: " + bestCost);
    }
    
    // Busca la mejor soluciÃ³n en la vecindad.
    public static int[] getBestNeighbour(TabuList tabu, TSP tsp, int[] currentSol) {
	int[] bestSol = new int[currentSol.length];
	System.arraycopy(currentSol, 0, bestSol, 0, bestSol.length);
	float bestCost = TSP.getCost(tsp, currentSol);
	int city1 = 0;
	int city2 = 0;
	boolean first = true;
	int[] newSol;
	float newCost = bestCost;
	outerloop:
	for(int i = 1; i < bestSol.length-1; ++i) {
	    for(int j = 2; j < bestSol.length-1; ++j) {
		if (i == j) continue;
		newSol = new int[bestSol.length];
		System.arraycopy(currentSol, 0, newSol, 0, bestSol.length);
		newSol = swap(i, j, currentSol);
		
		// newCost = TSP.getCost(tsp, newSol, i, j);
		newCost = TSP.updateCost(tsp, bestSol, newSol, newCost, i, j);
		if((newCost < bestCost || first) && tabu.list[i][j] == 0) {
		    first = false;
		    city1 = i;
		    city2 = j;
		    System.arraycopy(newSol, 0, bestSol, 0, bestSol.length);
		    bestCost = newCost;
		    // break outerloop;
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
  FunciÃƒÂ³n: implementa el TSP. Esta es la clase principal.
  ParÃƒÂ¡metros:
  - numNodos: representa el nÃƒÂºmero de nodos que tiene el grafo.
  - listaNodos: es un arreglo que almacena todos los nodos del grafo.
  - caminos: Arreglo que posee para cada elemento, a cual se mueve (la siguiente ciudad a recorrer)
  
  - costos: Es una matriz de int que guarda en cada posiciÃƒÂ³n (i, j) 
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
    
    // Actualiza el costo del camino
    public static float updateCost(TSP tsp, int[] pathOld, int[] pathNew, float cost, int city1, int city2) {
	float newCost = cost;
	cost -= tsp.costs[pathOld[city1]][pathOld[city1 - 1]];
	cost -= tsp.costs[pathOld[city1]][pathOld[city1 + 1]];
	cost += tsp.costs[pathNew[city1]][pathNew[city1 - 1]];
	cost += tsp.costs[pathNew[city1]][pathNew[city1 + 1]];

	cost -= tsp.costs[pathOld[city2]][pathOld[city2 - 1]];
	cost -= tsp.costs[pathOld[city2]][pathOld[city2 + 1]];
	cost += tsp.costs[pathNew[city2]][pathNew[city2 - 1]];
	cost += tsp.costs[pathNew[city2]][pathNew[city2 + 1]];
	return cost;
    }
    
    // Calcula el costo de recorrer un camino.
    public static float getCost(TSP tsp, int[] path) {
	float cost = 0;
	float s;
	for(int i = 0; i < tsp.numNodes-1; ++i) {
	    cost += tsp.costs[path[i]][path[i+1]];
	}
	return cost;
    }
    
    // Local search. No lo logra mucho.
    // public static int[] localSearch(TSP tsp) {
    public static int[] nearestNeighbour(TSP tsp) {
	int numNodos = tsp.numNodes;
	int[] set = new int[numNodos];
        Arrays.fill(set, 0);
	int[] trace = new int[numNodos];
        int start = 0;
        trace[0] = start;
        
        for (int i = 0; i < numNodos-1; i++) {
            double bestCost = Double.MAX_VALUE;
            int newCity = 0;
        
            for (int j = 0; j < numNodos; j++) {
                if (tsp.costs[trace[i]][j] < bestCost && set[j] < 1) {
                    
                    bestCost = tsp.costs[trace[i]][j];
                    newCity = j;
                }
                        
            }
            trace[i+1] = newCity;
            set[trace[i]]++;
            set[newCity]++;
        }
        
	return trace;
    }
    // 	int numNodos = tsp.numNodes;
    // 	int[] set = new int[numNodos+1];
    // 	int[] camino = new int[numNodos+1];
    // 	Arrays.fill(set,0);
    // 	int start = 0;          //Nodo donde comienza y cierra el ciclo
    // 	int num = numNodos+1;
    // 	int i = 0;
    // 	set[start]++;
    // 	set[numNodos]++;
    // 	camino[0] = 0;
    // 	camino[numNodos] = 0;
        
    // 	while (i < numNodos) {
    // 	    double menor = Integer.MAX_VALUE;
    // 	    int nodoDes = 0;
    // 	    for(int j = 1; j < numNodos; j++) {
    // 		if (tsp.costs[camino[i]][j] <= menor && set[j] < 1 && camino[i]!=j) {
    // 		    menor = tsp.costs[camino[i]][j];
    // 		    nodoDes = j;
    // 		}
    // 	    }
    // 	    camino[i+1] = nodoDes;
	    
    // 	    set[nodoDes]++;
    // 	    i++;
    // 	}
	
	
    // 	return camino;
    // }
	// // // // // // // //
    // int[] set = new int[tsp.numNodes];
    // int[] camino = new int[tsp.numNodes];
    // float recorrido = 0;
    // Arrays.fill(set,0);
    // int start = 0;          //Nodo donde comienza y cierra el ciclo
    // int num = tsp.numNodes;
    // int l = start;
    // while (num-- > 1) {
    //     float menor = Integer.MAX_VALUE;
    //     int nodoDes = 0;
    //     for(int j = 0; j < tsp.numNodes; ++j) {
    // 	if (tsp.costs[l][j] <= menor && set[j] < 1 && (l != j)) {
    // 	    menor = tsp.costs[l][j];
    // 	    nodoDes = j;
    // 	}
    //     }
    //     camino[l] = nodoDes;
    //     recorrido += menor;
    //     set[l]++;
    //     set[nodoDes]++;
    //     l = nodoDes;
    
    // }
    // camino[l] = start;
    // recorrido += tsp.costs[l][start];
    // System.out.println("METODO DEL VECINO MAS CERCANO");
    // System.out.println("Costo total= " + recorrido);
    
    // tsp.solution[0] = 0;
    // tsp.solution[tsp.numNodes] = 0;
    // for(int i = 1; i < camino.length; ++i) {
    //     tsp.solution[i] = camino[tsp.solution[i-1]];
    // }
    // return tsp.solution;
}


/*
  Clase: TabuList
  FunciÃ³n: mantiene la lista de los movimientos tabÃº para la bÃºsqueda tabÃº.
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
  FunciÃƒÂ³n: procesa el archivo de entrada y crea la lista de nodos.
*/

class Reader {
    public Reader() {
	
    }
    
    /*
      MÃ©todo: readInput.
      FunciÃƒÂ³n: lee la informaciÃ³n por entrada estÃ¡ndar 
      (mediante uno de los archivos output.txt creados con Preprocessor)
    */
    public static float[][] readInput() {
        File file = new File("C:\\Users\\GabrielAugusto\\Downloads\\rl1889.txt");
        try {
            
            Scanner scanner = new Scanner(file);
            int numNodos = scanner.nextInt();
            float[][] l = new float[numNodos][numNodos];
            scanner.nextInt();
            
            
            for(int i = 0; i < numNodos-1; ++i) {
                
                for(int j = 0; j < numNodos; ++j) {
                    l[i][j] = scanner.nextFloat();
                }
                scanner.nextInt();
                scanner.nextInt();
            }
            //Last isolated case
            for(int j = 0; j < numNodos; ++j) {
                    l[numNodos-1][j] = scanner.nextFloat();
            }
            
            for (int i = 0; i < numNodos; i++) {
                l[i][i] = Integer.MAX_VALUE;
            }
           
            return l;
        }
        catch (FileNotFoundException e) {
            System.out.println("File not Found");
        } 
        return null;
    }
}