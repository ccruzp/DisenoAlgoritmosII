import java.util.*;
import java.io.*;

public class Preprocessor {
    public static void main(String[] args) {
	Nodo[] lista = Reader.readInput();
	double[][] costos = Reader.calculateCosts(lista);
	Reader.writeCosts(costos);
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
	int numNodos = Integer.parseInt(scanner.nextLine().split(" ")[2]);
	Nodo[] l = new Nodo[numNodos];
	scanner.nextLine();
	scanner.nextLine();
	for(int i = 0; i < numNodos; ++i) {
	    l[i] = new Nodo(scanner.nextInt(), scanner.nextDouble(), scanner.nextDouble());
	    // l[i] = new Nodo(scanner.nextInt(), scanner.nextInt(), scanner.nextInt());
	}
	return l;
    }

    public static double[][] calculateCosts(Nodo[] lista) {
	double[][] costo = new double[lista.length][lista.length];
	for(int i = 0; i < lista.length; ++i) {
	    for(int j = i; j < lista.length; ++j) {
		if(i == j) {
		    costo[i][j] = 0;
		} else {
		    costo[i][j] = Reader.getCosto(lista[i], lista[j]);
		    costo[j][i] = costo[i][j];
		}
	    }
	}
	return costo;
    }

    public static void writeCosts(double[][] costos) {
	File file = new File("output.txt");
	try{
	    FileWriter fw = new FileWriter(file, true);
	    PrintWriter writer = new PrintWriter(fw);
	    writer.println(costos.length);
	    for(int i = 0; i < costos.length; ++i) {
		writer.println(i+1);
		for (int j = 0; j < costos.length; ++j) {
		    writer.print(costos[i][j] + " ");
		}
		writer.println();
	    }
	    writer.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    /*
      Método: getCosto.
      Función: calcula el costo de ir del nodo id1 al nodo id2.
    */
    public static double getCosto(Nodo n1, Nodo n2) {
	return (double) Math.sqrt(Math.pow(n2.x - n1.x, 2) + Math.pow(n2.y - n1.y, 2));
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
    // int x;
    // int y;
    double x;
    double y;
    public Nodo(int id, double x, double y) {
    	this.id = id;
    	this.x = x;
    	this.y = y;
    }

    // public Nodo(int id, int x, int y) {
    // 	this.id = id;
    // 	this.x = x;
    // 	this.y = y;
    // }

    public String toString() {
	return "Nodo " + Integer.toString(id);
    }
}