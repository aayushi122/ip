import java.util.ArrayList;
import java.util.Scanner;
public class Todd {
    static void main(String[] args) {
        String line = "____________________________________________________________";
        String banner = "   ______          __     __\n"
                + "  /_  __/___  ____/ /____/ /\n"
                + "   / / / __ \\/ __  / __  / \n"
                + "  / / / /_/ / /_/ / /_/ /  \n"
                + " /_/  \\____/\\__,_/\\__,_/   \n";
        ArrayList<String> list = new ArrayList<>();

        System.out.println(banner);
        System.out.println("Hello There! I'm Todd, a NPC Chatbot :P\nWhat can I do for you today?");

        Scanner sc = new Scanner(System.in);
        String txt = sc.nextLine();

        while (true) {
            switch (txt) {
                case "bye":
                    System.out.println("Noo don't go, come back. Ok fine bye. See you soon.");
                    return;
                case "list":
                    if (list.isEmpty()) {
                        System.out.println("\t" + line);
                        System.out.println("\tThe list is empty");
                        System.out.println("\t" + line);
                    } else {
                        int i = 1;
                        for (String s : list) {
                            System.out.println("\t" + line);
                            System.out.println("\t" + i + ". " + s);
                            System.out.println("\t" + line);
                            i++;
                        }
                    }
                    txt = sc.nextLine();
                    break;
                default:
                    System.out.println("\t" + line);
                    list.add(txt);
                    System.out.println("\t" + "added: " + txt);
                    System.out.println("\t" + line);
                    txt = sc.nextLine();
            }
        }
    }
}