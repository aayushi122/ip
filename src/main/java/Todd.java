import java.util.Scanner;
public class Todd {
    static void main(String[] args) {
        String banner = "   ______          __     __\n"
                + "  /_  __/___  ____/ /____/ /\n"
                + "   / / / __ \\/ __  / __  / \n"
                + "  / / / /_/ / /_/ / /_/ /  \n"
                + " /_/  \\____/\\__,_/\\__,_/   \n";
        System.out.println(banner);
        System.out.println("Hello There! I'm Todd, a NPC Chatbot :P\nWhat can I do for you today?");
        Scanner sc = new Scanner(System.in);
        if (sc.nextLine().equals("bye")) {
            System.out.println("Noo don't go, come back. Ok fine bye. See you soon.");
        }
    }
}
