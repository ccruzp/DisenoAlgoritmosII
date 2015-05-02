import java.util.Scanner;

/*
  Clase: TSP
  Función: implementa el TSP. Esta es la clase principal.
  Parámetros:
  - numNodos: representa el número de nodos que tiene el grafo.
  - listaNodos: es un arreglo que almacena todos los nodos del grafo.
  - caminos: es una matriz de int para representar el camino óptimo.
             Se guarda 1 en la posición (i, j) si existe un subcamino
             que pase por los nodos i y j; si no, se guarda 0.
  - costos: Es una matriz de double que guarda en cada posición (i, j) 
            el costo de viajar entre el nodo i y el nodo j, y viceversa.
*/
public class TSP {
    static int numNodos;
    static int[][] caminos;
    static double[][] costos;
    static Nodo[] listaNodos;

    public static void main(String args[]) {
	listaNodos = Reader.readInput();
	TSP tsp = new TSP(listaNodos.length);
    }

    public TSP (int numNodos) {
	this.numNodos = numNodos;
	caminos = new int[numNodos][numNodos];
	costos = new double[numNodos][numNodos];
	for(int i = 0; i < numNodos; i++) {
	    for(int j = 0; j < numNodos; j++) {
		caminos[i][j] = 0;
		costos[i][j] = -1;
	    }
	}
    }

    /*
      Método: getCosto.
      Función: calcula el costo de ir del nodo id1 al nodo id2.
    */
    public double getCosto(int id1, int id2) {
	
	if (costos[id1-1][id2-1] == -1) {
	    costos[id1-1][id2-1] = Math.sqrt(Math.pow(listaNodos[id2-1].x - listaNodos[id1-1].x, 2) + Math.pow(listaNodos[id2-1].y - listaNodos[id1-1].y, 2));
	    costos[id2-1][id1-1] = costos[id1-1][id2-1];
	}
	return costos[id1-1][id2-1];
    }
}

/*
  Clase: Nodo
  Función: clase que implementa los nodos del grafo.
  Parámetros:
  - id: int que identifica al nodo.
  - x: coordenada X del nodo en un plano.
  - y: coordenada Y del nodo en un plano.
*/
class Nodo {
    int id;
    int x;
    int y;

    public Nodo(int id, int x, int y) {
	this.id = id;
	this.x = x;
	this.y = y;
    }

    public String toString() {
	return "Nodo " + Integer.toString(id);
    }
}

/*
  Clase: Reader
  Función: procesa el archivo de entrada y crea la lista de nodos.
*/

class Reader {
    public Reader() {

    }

    /*
      Método: readInput.
      Función: lee la información por entrada estándar y crea la lista de nodos.
    */
    public static Nodo[] readInput() {
	Scanner scanner = new Scanner(System.in);
	scanner.nextLine();
	scanner.nextLine();
	scanner.nextLine();
	int numNodos = Integer.parseInt(scanner.nextLine().split(" ")[1]);
	Nodo[] l = new Nodo[numNodos];
	scanner.nextLine();
	scanner.nextLine();
	for(int i = 0; i < numNodos; ++i) {
	    l[i] = new Nodo(scanner.nextInt(), scanner.nextInt(), scanner.nextInt());
	}
	return l;
    }
}