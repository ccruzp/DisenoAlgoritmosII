public class tsp {
    int numNodos;
    int[][] caminos;
    int[][] costos;

    public static void main(String args[]) {
	tsp t = new tsp(10);
	System.out.println("Número de nodos");
	for(int i = 0; i < t.numNodos; ++i) {
	    for(int j = 0; j < t.numNodos; ++i) {
		System.out.println("Caminos " + i + " " + j + ": " + t.caminos[i][j]);
		System.out.println("Costo " + i + " " + j + ": " + t.costos[i][j]);
	    }
	}

    }

    public tsp (int n) {
	numNodos = n;
	caminos = new int[numNodos][numNodos];
	costos = new int[numNodos][numNodos];
	for(int i = 0; i < numNodos; ++i) {
	    for(int j = 0; j < numNodos; ++i) {
		caminos[i][j] = 0;
		costos[i][j] = 0;
	    }
	}
    }
}