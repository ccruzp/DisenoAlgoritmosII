import java.util.Scanner;

public class TSP {
    static int numNodos;
    static int[][] caminos;
    static float[][] costos;

    public static void main(String args[]) {
	Nodo[] listaNodos = Reader.readInput();
	TSP tsp = new TSP(listaNodos.length);
    }

    public TSP (int numNodos) {
	this.numNodos = numNodos;
	caminos = new int[numNodos][numNodos];
	costos = new float[numNodos][numNodos];
	for(int i = 0; i < numNodos; i++) {
	    for(int j = 0; j < numNodos; j++) {
		caminos[i][j] = 0;
		costos[i][j] = -1;
	    }
	}
    }
}

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

class Reader {
    public Reader() {

    }

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