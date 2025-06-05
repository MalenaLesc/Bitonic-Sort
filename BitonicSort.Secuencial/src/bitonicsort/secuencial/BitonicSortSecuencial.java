
package bitonicsort.secuencial;

import java.util.Random;

public class BitonicSortSecuencial {

    static final int ASCENDENTE = 1;    //Constante para indicar orden ascendente
    static final int DESCENDENTE = 0;  //Constante para indicar orden descendente

    // Método principal 
    public static void main(String[] args) {
        
        int tam = 33554432; // El tamaño debe ser potencia de 2
        int[] array = new int[tam];
        Random rand = new Random();

        //Llenado del arreglo con números aleatorios entre 0 y 99
        for (int i = 0; i < tam; i++) {
            array[i] = rand.nextInt(100);
        }

        //System.out.println("Arreglo original:");
        //mostrarArreglo(array);

        //Inicia cronómetro en nanosegundos
        long inicio = System.nanoTime();

        //Llamada al método de ordenamiento 
        bitonicSort(array, 0, tam, ASCENDENTE);

        long fin = System.nanoTime();

        //Muestra cantidad de núcleos disponibles
        System.out.println("Procesadores disponibles: " + Runtime.getRuntime().availableProcessors());

        //Calcula el tiempo en milisegundos con mayor precisión
        double tiempoMs = (fin - inicio) / 1_000_000.0;
        System.out.printf("Tiempo de ejecución: %.3f ms%n", tiempoMs);

        System.out.println("Arreglo ya ordenado");
        //mostrarArreglo(array);

        
    }

    //Método principal de Bitonic Sort (recursivo)
    static void bitonicSort(int[] array, int low, int cnt, int dir) {
        
        if (cnt > 1) {
            int k = cnt / 2;

            //Ordena la primera mitad en orden ascendente
            bitonicSort(array, low, k, ASCENDENTE);

            //Ordena la segunda mitad en orden descendente
            bitonicSort(array, low + k, k, DESCENDENTE);

            //Junta ambas mitades en la dirección deseada
            bitonicMerge(array, low, cnt, dir);
        }
    }

    //Método de fusión bitónica (bitonic merge)
    static void bitonicMerge(int[] array, int low, int cnt, int dir) {
        if (cnt > 1) {
            // Realiza las comparaciones e intercambios necesarios
            bitonicCompare(array, low, cnt, dir);
            int k = cnt / 2;

            // Fusión recursiva de ambas mitades
            bitonicMerge(array, low, k, dir);
            bitonicMerge(array, low + k, k, dir);
        }
    }

    //Método que compara e intercambia los elementos
    static void bitonicCompare(int[] array, int low, int cnt, int dir) {
        int k = cnt / 2;
        for (int i = low; i < low + k; i++) {
            // Compara según la dirección: ASCENDENTE o DESCENDENTE
            if ((dir == ASCENDENTE && array[i] > array[i + k]) ||
                (dir == DESCENDENTE && array[i] < array[i + k])) {
                // Intercambio de elementos
                int temp = array[i];
                array[i] = array[i + k];
                array[i + k] = temp;
            }
        }
    }

    //Método para mostrar el arreglo
    static void mostrarArreglo(int[] array) {
        for (int j : array) {
            System.out.print(j + " ");
        }
        System.out.println();
    }
}
    

