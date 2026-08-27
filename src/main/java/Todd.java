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
                    System.out.println("\t  " + (i + 1) + "." + list.get(i));
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
                        System.out.println("\t    " + list.get(index));
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
                        System.out.println("\t    " + list.get(index));
                    }
                }
                System.out.println(line);
                txt = sc.nextLine();

            } else if (txt.startsWith("todo ")) {
                String description = txt.substring(5);
                Task newTask = new Todo(description);
                list.add(newTask);
                System.out.println(line);
                System.out.println("\t Got it. I've added this task:");
                System.out.println("\t   " + newTask);
                System.out.println("\t Now you have " + list.size() + " tasks in the list.");
                System.out.println(line);
                txt = sc.nextLine();

            } else if (txt.startsWith("deadline ")) {
                String rest = txt.substring(9);
                String[] parts = rest.split(" /by ", 2);
                String description = parts[0];
                String by = parts.length > 1 ? parts[1] : "";
                Task newTask = new Deadline(description, by);
                list.add(newTask);
                System.out.println(line);
                System.out.println("\t Got it. I've added this task:");
                System.out.println("\t   " + newTask);
                System.out.println("\t Now you have " + list.size() + " tasks in the list.");
                System.out.println(line);
                txt = sc.nextLine();

            } else if (txt.startsWith("event ")) {
                String rest = txt.substring(6);
                String[] fromSplit = rest.split(" /from ", 2);
                String description = fromSplit[0];
                String[] toSplit = fromSplit.length > 1 ? fromSplit[1].split(" /to ", 2) : new String[]{""};
                String from = toSplit[0];
                String to = toSplit.length > 1 ? toSplit[1] : "";
                Task newTask = new Event(description, from, to);
                list.add(newTask);
                System.out.println(line);
                System.out.println("\t Got it. I've added this task:");
                System.out.println("\t   " + newTask);
                System.out.println("\t Now you have " + list.size() + " tasks in the list.");
                System.out.println(line);
                txt = sc.nextLine();

            } else {
                System.out.println(line);
                System.out.println("\t I don't understand that command.");
                System.out.println(line);
                txt = sc.nextLine();
            }
        }
    }
}