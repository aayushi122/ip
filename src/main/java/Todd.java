import java.util.ArrayList;
import java.util.Scanner;

public class Todd {
    public static void main(String[] args) {
        String line = "\t____________________________________________________________";
        String banner = "   ______          __     __\n"
                + "  /_  __/___  ____/ /____/ /\n"
                + "   / / / __ \\/ __  / __  / \n"
                + "  / / / /_/ / /_/ / /_/ /  \n"
                + " /_/  \\____/\\__,_/\\__,_/   \n";
        ArrayList<Task> list = new ArrayList<>();

        System.out.println(banner);
        System.out.println("Hello There! I'm Todd, a NPC Chatbot :P\nWhat can I do for you today?");

        Scanner sc = new Scanner(System.in);
        String txt = sc.nextLine();

        while (true) {
            if (txt.equals("bye")) {
                System.out.println("Noo don't go, come back. Ok fine bye. See you soon.");
                return;

            } else if (txt.equals("list")) {
                System.out.println(line);
                System.out.println("\t Here are the tasks in your list:");
                for (int i = 0; i < list.size(); i++) {
                    Task t = list.get(i);
                    System.out.println("\t  " + (i + 1) + ". [" + t.getStatusIcon() + "] " + t.getDescription());
                }
                System.out.println(line);
                txt = sc.nextLine();

            } else if (txt.startsWith("mark ")) {
                String numberPart = txt.substring(5);
                boolean isValidNumber = !numberPart.isEmpty();
                for (int i = 0; i < numberPart.length(); i++) {
                    if (!Character.isDigit(numberPart.charAt(i))) {
                        isValidNumber = false;
                    }
                }

                System.out.println(line);
                if (!isValidNumber) {
                    System.out.println("\t Enter the task number pls");
                } else {
                    int index = Integer.parseInt(numberPart) - 1;
                    if (index < 0 || index >= list.size()) {
                        System.out.println("\t That task number doesn't exist!");
                    } else if (list.get(index).isDone()) {
                        System.out.println("\t That task is already marked.");
                    } else {
                        list.get(index).markAsDone();
                        System.out.println("\t Hooray! You are on fire! Task crossed off.");
                        System.out.println("\t    [X] " + list.get(index).getDescription());
                    }
                }
                System.out.println(line);
                txt = sc.nextLine();

            } else if (txt.startsWith("unmark ")) {
                String numberPart = txt.substring(7);
                boolean isValidNumber = !numberPart.isEmpty();
                for (int i = 0; i < numberPart.length(); i++) {
                    if (!Character.isDigit(numberPart.charAt(i))) {
                        isValidNumber = false;
                    }
                }

                System.out.println(line);
                if (!isValidNumber) {
                    System.out.println("\t Enter the task number pls");
                } else {
                    int index = Integer.parseInt(numberPart) - 1;
                    if (index < 0 || index >= list.size()) {
                        System.out.println("\t That task number doesn't exist!");
                    } else if (!list.get(index).isDone()) {
                        System.out.println("\t That task is already unmarked.");
                    } else {
                        list.get(index).markAsUndone();
                        System.out.println("\t Oh no! Okay unmarked.");
                        System.out.println("\t    [ ] " + list.get(index).getDescription());
                    }
                }
                System.out.println(line);
                txt = sc.nextLine();

            } else {
                System.out.println(line);
                list.add(new Task(txt));
                System.out.println("\t added: " + txt);
                System.out.println(line);
                txt = sc.nextLine();
            }
        }
    }
}