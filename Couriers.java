import java.io.*;
import java.util.*;
import java.util.stream.*;

public class Couriers {
    // CO1: variables, constants, operators
    static final double SPEED = 40.0;
    static int nextId = 101;

    // CO3: 1D array (hubs) and 2D array (distances)
    static String[] hubs = {"Hub-A", "Hub-B", "Hub-C", "Hub-D", "Hub-E"};
    static int INF = 9999;
    static int[][] road = {
        {0, 12, INF, 30, INF},
        {12, 0, 9, INF, INF},
        {INF, 9, 0, 20, 15},
        {30, INF, 20, 0, 8},
        {INF, INF, 15, 8, 0}};

    // CO6: ArrayList and HashMap
    static ArrayList<Pkg> list = new ArrayList<>();
    static HashMap<Integer, Pkg> map = new HashMap<>();

    // CO3: method with parameters and return value
    // CO2: nested for loops (Floyd-Warshall)
    static int shortest(int s, int d) {
        int[][] x = new int[5][5];
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                x[i][j] = road[i][j];
        for (int k = 0; k < 5; k++)
            for (int i = 0; i < 5; i++)
                for (int j = 0; j < 5; j++)
                    if (x[i][k] + x[k][j] < x[i][j])
                        x[i][j] = x[i][k] + x[k][j];
        return x[s][d];
    }

    // CO4: custom exception thrown for wrong id
    static Pkg find(int id) throws InvalidIdException {
        if (!map.containsKey(id))
            throw new InvalidIdException("Package not found: " + id);
        return map.get(id);
    }

    // CO5: file saving
    static void save() {
        try (BufferedWriter w = new BufferedWriter(new FileWriter("packages.txt"))) {
            for (Pkg p : list) {
                w.write(p.id + "," + p.sender + "," + p.receiver + "," + p.from + "," + p.to + "," + p.status);
                w.newLine();
            }
        } catch (IOException e) {
            System.out.println("Save error");
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        boolean running = true;

        // CO2: while loop + switch
        while (running) {
            System.out.println("\n1.Register 2.Track 3.Update 4.Search Sender 5.Summary 6.Exit");
            System.out.print("Enter choice: ");
            int ch = sc.nextInt();
            sc.nextLine();

            switch (ch) {
                case 1:
                    System.out.print("Sender name: ");
                    String s = sc.nextLine().trim().toUpperCase();   // CO5 string methods
                    System.out.print("Receiver name: ");
                    String r = sc.nextLine().trim().toUpperCase();
                    System.out.print("Source hub (0-4): ");
                    int a = sc.nextInt();
                    System.out.print("Destination hub (0-4): ");
                    int b = sc.nextInt();
                    if (a < 0 || a > 4||  b < 0 || b > 4 || a == b) {   // CO1 operators
                        System.out.println("Invalid hubs");
                        break;
                    }
                    int km = shortest(a, b);
                    double eta = (double) km / SPEED;                  // CO1 casting
                    Pkg p = new Pkg(nextId++, s, r, a, b);             // CO4 object
                    list.add(p);
                    map.put(p.id, p);
                    System.out.println("Package ID: " + p.id);
                    System.out.println("Distance: " + km + " km");
                    System.out.printf("Estimated time: %.2f hours%n", eta);
                    break;
                    case 2:
                    System.out.print("Enter package ID: ");
                    int id = sc.nextInt();
                    try {                                              // CO4 exception handling
                        Pkg t = find(id);
                        StringBuilder sb = new StringBuilder();        // CO5 StringBuilder
                        sb.append("Sender: ").append(t.sender).append("\n");
                        sb.append("Receiver: ").append(t.receiver).append("\n");
                        sb.append("Route: ").append(hubs[t.from]).append(" -> ").append(hubs[t.to]).append("\n");
                        sb.append("Status: ").append(t.status);
                        System.out.println(sb);
                    } catch (InvalidIdException e) {
                        System.out.println(e.getMessage());
                    }
                    break;

                case 3:
                    System.out.print("Enter package ID: ");
                    try {
                        Pkg u = find(sc.nextInt());
                        if (u.status.equals("Pending")) u.status = "In Transit";
                        else if (u.status.equals("In Transit")) u.status = "Delivered";
                        System.out.println("Status: " + u.status);
                    } catch (InvalidIdException e) {
                        System.out.println(e.getMessage());
                    }
                    break;

                case 4:
                    System.out.print("Sender name: ");
                    String name = sc.nextLine().trim();
                    for (Pkg q : list)                                 // CO5 linear search
                        if (q.sender.equalsIgnoreCase(name))
                            System.out.println(q.id + " " + q.receiver + " " + q.status);
                    break;

                case 5:
                    // CO6: lambda + streams
                    list.stream().filter(q -> q.status.equals("In Transit"))
                        .forEach(q -> System.out.println("In Transit: " + q.id));
                    Map<String, Long> count = list.stream()
                        .collect(Collectors.groupingBy(q -> q.status, Collectors.counting()));
                    System.out.println(count);
                    break;

                case 6:
                    running = false;
                    break;

                default:
                    System.out.println("Wrong choice");
            }
            save();
        }
    }
}

// CO4: class with constructor
class Pkg {
    int id, from, to;
    String sender, receiver, status = "Pending";

    Pkg(int id, String sender, String receiver, int from, int to) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.from = from;
        this.to = to;
    }
}

// CO4: custom exception
class InvalidIdException extends Exception {
    InvalidIdException(String msg) {
        super(msg);
    }
}