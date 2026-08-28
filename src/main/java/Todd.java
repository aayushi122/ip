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
            try {
                if (txt.equals("bye")) {
                    System.out.println("Noo don't go, come back. Ok fine bye. See you soon.");
                    return;

                } else if (txt.equals("list")) {
                    System.out.println(line);
                    if (list.isEmpty()) {
                        System.out.println("\t Your list is empty! Add some tasks first.");
                    } else {
                        System.out.println("\t Here are the tasks in your list:");
                        for (int i = 0; i < list.size(); i++) {
                            System.out.println("\t  " + (i + 1) + "." + list.get(i));
                        }
                    }
                    System.out.println(line);

                } else if (txt.startsWith("mark")) {
                    int index = parseIndex(txt, "mark", list.size());
                    if (list.get(index).isDone()) {
                        throw new TodException("That task is already marked.");
                    }
                    list.get(index).markAsDone();
                    System.out.println(line);
                    System.out.println("\t Hooray! You are on fire! Task crossed off.");
                    System.out.println("\t    " + list.get(index));
                    System.out.println(line);

                } else if (txt.startsWith("unmark")) {
                    int index = parseIndex(txt, "unmark", list.size());
                    if (!list.get(index).isDone()) {
                        throw new TodException("That task is already unmarked.");
                    }
                    list.get(index).markAsUndone();
                    System.out.println(line);
                    System.out.println("\t Oh no! Okay unmarked.");
                    System.out.println("\t    " + list.get(index));
                    System.out.println(line);

                } else if (txt.equals("todo") || txt.startsWith("todo ")) {
                    String description = txt.length() > 4 ? txt.substring(5).trim() : "";
                    if (description.isEmpty()) {
                        throw new TodException("Wait you didn't even tell me what is the to-do");
                    }
                    Task newTask = new Todo(description);
                    list.add(newTask);
                    printAdded(line, newTask, list.size());

                } else if (txt.equals("deadline") || txt.startsWith("deadline ")) {
                    String rest = txt.length() > 8 ? txt.substring(9).trim() : "";
                    if (rest.isEmpty()) {
                        throw new TodException("Wait you didn't even tell me anything about this deadline");
                    }
                    String[] parts = rest.split(" /by ", 2);
                    String description = parts[0].trim();
                    if (description.isEmpty()) {
                        throw new TodException("Try Again! The description is literally empty.");
                    }
                    if (parts.length < 2 || parts[1].trim().isEmpty()) {
                        throw new TodException("Try Again! There is no /by date or time.");
                    }
                    Task newTask = new Deadline(description, parts[1].trim());
                    list.add(newTask);
                    printAdded(line, newTask, list.size());

                } else if (txt.equals("event") || txt.startsWith("event ")) {
                    String rest = txt.length() > 5 ? txt.substring(6).trim() : "";
                    if (rest.isEmpty()) {
                        throw new TodException("Wait you didn't even tell me anything about this event");
                    }
                    String[] fromSplit = rest.split(" /from ", 2);
                    String description = fromSplit[0].trim();
                    if (description.isEmpty()) {
                        throw new TodException("Try Again! The description is literally empty.");
                    }
                    if (fromSplit.length < 2 || fromSplit[1].trim().isEmpty()) {
                        throw new TodException("Try Again! There is no /from time.");
                    }
                    String[] toSplit = fromSplit[1].split(" /to ", 2);
                    String from = toSplit[0].trim();
                    if (toSplit.length < 2 || toSplit[1].trim().isEmpty()) {
                        throw new TodException("Try Again! There is no /to time.");
                    }
                    String to = toSplit[1].trim();
                    Task newTask = new Event(description, from, to);
                    list.add(newTask);
                    printAdded(line, newTask, list.size());

                } else {
                    throw new TodException("What does that mean dawg");
                }

            } catch (TodException e) {
                System.out.println(line);
                System.out.println("\t " + e.getMessage());
                System.out.println(line);
            }

            txt = sc.nextLine();
        }
    }

    private static int parseIndex(String txt, String command, int listSize) throws TodException {
        String numberPart = txt.length() > command.length() ? txt.substring(command.length()).trim() : "";
        boolean isValidNumber = !numberPart.isEmpty();
        for (int i = 0; i < numberPart.length(); i++) {
            if (!Character.isDigit(numberPart.charAt(i))) {
                isValidNumber = false;
            }
        }
        if (!isValidNumber) {
            throw new TodException("OOPS!!! Please provide a valid task number to " + command + ".");
        }
        int index = Integer.parseInt(numberPart) - 1;
        if (index < 0 || index >= listSize) {
            throw new TodException("OOPS!!! That task number doesn't exist!");
        }
        return index;
    }

    private static void printAdded(String line, Task newTask, int totalTasks) {
        System.out.println(line);
        System.out.println("\t Got it. I've added this task:");
        System.out.println("\t   " + newTask);
        System.out.println("\t Now you have " + totalTasks + " tasks in the list.");
        System.out.println(line);
    }
}