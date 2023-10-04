import java.util.*;

public class Prueba {
    public static void main(String[] args) {
        Nodo n1, n2;

        n1 = new Nodo(0, 5, 0, null, -11);
        n2 = new Nodo(0, 0, 0, null, 151);

        PriorityQueue<Nodo> pq = new PriorityQueue<>(new NodoComparator());
        pq.add(n1);
        pq.add(n2);

        while(!pq.isEmpty()) {
            System.out.println((Integer)pq.peek().getPrioridad());
            pq.poll();
        }
    }
}
