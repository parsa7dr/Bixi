package ca.concordia.view;

import ca.concordia.controller.BixiController;
import ca.concordia.controller.IBixiController;
import ca.concordia.model.BixiTrip;

import java.util.Scanner;

public class BixiView {

    private IBixiController controller;

    /**
     * Constructor for BixiView.
     * Initializes the controller
     */
    public BixiView(){
        controller = new BixiController();
    }

    /*
    Allows the user to view the list data segment by segment.
     */
    private void printPaginated(Iterable<?> data, int pageSize, Scanner scanner) {
        var iter = data.iterator();
        int shown = 0;

        while (true) {
            int printedThisPage = 0;

            while (iter.hasNext() && printedThisPage < pageSize) {
                System.out.println(iter.next());
                printedThisPage++;
                shown++;
            }

            if (!iter.hasNext()) {
                System.out.println("\n(total results: " + shown + ")");
                return; // done, back to menu
            }

            System.out.print("\n-- Showing " + shown + " so far. Press Enter for more, or type 'q' to stop: ");
            String cmd = scanner.nextLine(); // nextLine, so Enter = ""
            if (cmd.equalsIgnoreCase("q")) {
                System.out.println("(stopped early; shown: " + shown + ")");
                return; // back to menu
            }
            // if Enter, continue
        }
    }

    /*
    Shows 20 results within the whole results set
     */
    private void printIterablePreview(Iterable<?> it, int maxToPrint) {
        int count = 0;
        for (Object obj : it) {
            if (count < maxToPrint) {
                System.out.println(obj);
            }
            count++;
        }
        if (count > maxToPrint) {
            System.out.println("... (printed " + maxToPrint + " of " + count + " results)");
        } else {
            System.out.println("(total results: " + count + ")");
        }
    }

    /*
    Helper method added to prevent the program from crashing if a user enters an invalid character
     */
    private String readDateTime(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = scanner.nextLine().trim();

            // allows user to cancel
            if (s.equalsIgnoreCase("q")) return null;

            try {
                // yyyy-MM-dd HH:mm:ss
                java.time.LocalDateTime.parse(
                        s,
                        java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                );
                return s; // valid
            } catch (java.time.format.DateTimeParseException e) {
                System.out.println("Invalid format. Use: yyyy-MM-dd HH:mm:ss (example: 2025-02-01 02:01:01) or 'q' to cancel.");
            }
        }
    }

    /**
     * Starts the Bixi data viewer application.
     */
    public void start() {
        String message = "Welcome to the Bixi Data Viewer!";
        System.out.println(message);
        Scanner scanner = new Scanner(System.in);
        boolean loaded = false;

        while (!loaded) {
            System.out.print("Please enter the path to the Bixi data file (or 'q' to quit): ");
            String filePath = scanner.nextLine().trim();

            if (filePath.equalsIgnoreCase("q")) {
                System.out.println("Exiting program.");
                return;
            }

            try {
                controller.loadFile(filePath);

                // Optional: if you have a method that returns number of stations
                // and it’s 0 when load fails, you can check here.

                loaded = true;
                System.out.println("File loaded successfully.\n");

            } catch (Exception e) {
                System.out.println("ERROR reading file: " + filePath);
                System.out.println("Please try again.\n");
            }
        }

        //TODO - Complete
        while (true) {
System.out.println("\n===== MENU =====");
System.out.println("1) REQ1 - Trips by Station");
System.out.println("2) REQ2 - Trips by Month");
System.out.println("3) REQ3 - Trips by Minimum Duration");
System.out.println("4) REQ4 - Trips by Start Time Interval");
System.out.println("5) REQ6 - Top K Stations in Period");
System.out.println("6) REQ7 - Rush Hour of Month");
System.out.println("0) Exit");
            System.out.print("Choice: ");

            String choice = scanner.nextLine().trim();

            if (choice.equals("0")) {
                System.out.println("Goodbye!");
                break;
            }

            switch (choice) {
               case "1": {
                    System.out.print("Enter station name: ");
                    String station = scanner.nextLine().trim();

                    System.out.print("Mode (start / end / both): ");
                    String mode = scanner.nextLine().trim();

                    Iterable<BixiTrip> trips = controller.getTripsByStation(station, mode);
                    printPaginated(trips, 20, scanner);
                    break;
                }
                case "2": {
                    System.out.print("Enter month (YYYY-MM): ");
                    String ym = scanner.nextLine().trim();
                    Iterable<BixiTrip> trips = controller.getTripsByMonth(ym);
                    printPaginated(trips, 20, scanner);
                    break;
                }

                case "3": {
                   System.out.print("Enter minimum duration in minutes: ");
                   float minDuration;

                   try {
                   minDuration = Float.parseFloat(scanner.nextLine().trim());
                   } catch (Exception e) {
                       System.out.println("Invalid number.");
                        break;
                    }
                    

                    Iterable<BixiTrip> trips = controller.getTripsByDuration(minDuration);
                    printPaginated(trips, 20, scanner);
                    break;
                } 
                case "4": {
                    String start = readDateTime(scanner,
                            "Start date (yyyy-MM-dd HH:mm:ss) or 'q' to cancel: ");
                    if (start == null) break;

                    String end = readDateTime(scanner,
                            "End date (yyyy-MM-dd HH:mm:ss) or 'q' to cancel: ");
                    if (end == null) break;

                    Iterable<?> trips = controller.getTripsByStartTime(start, end);
                    printPaginated(trips, 20, scanner);
                    break;
                }

                case "5": {
                    // 1) Read K (must be a positive int)
                    System.out.print("Enter K: ");
                    int k;
                    try {
                        k = Integer.parseInt(scanner.nextLine().trim());
                    } catch (Exception e) {
                        System.out.println("Invalid K. Please enter a positive integer.");
                        break;
                    }
                    if (k <= 0) {
                        System.out.println("K must be greater than 0.");
                        break;
                    }

                    // 2) Read start date (allows 'q' to cancel the program)
                    System.out.print("Start date (yyyy-MM-dd HH:mm:ss) or 'q' to cancel: ");
                    String start = scanner.nextLine().trim();
                    if (start.equalsIgnoreCase("q")) {
                        System.out.println("Cancelled.");
                        break;
                    }

                    // 3) Read end date (allows 'q' to cancel the program if user wants)
                    System.out.print("End date (yyyy-MM-dd HH:mm:ss) or 'q' to cancel: ");
                    String end = scanner.nextLine().trim();
                    if (end.equalsIgnoreCase("q")) {
                        System.out.println("Cancelled.");
                        break;
                    }

                    // 4) Call controller + print results
                    Iterable<String> top = controller.getTopStations(k, start, end);

                    int count = 0;
                    for (String s : top) {
                        System.out.println(s);
                        count++;
                    }
                    System.out.println("(total results: " + count + ")");
                    break;
                }
                    
                case "6": {
                    System.out.print("Enter month (1-12): ");
                    int month;

                    try {
                    month = Integer.parseInt(scanner.nextLine().trim());
                     } catch (Exception e) {
                       System.out.println("Invalid month.");
                       break;
                 }

    int rushHour = controller.getRushHourOfMonth(month);
    System.out.println("Rush hour for month " + month + " is: " + rushHour + ":00");
    break;
}    
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }
}
