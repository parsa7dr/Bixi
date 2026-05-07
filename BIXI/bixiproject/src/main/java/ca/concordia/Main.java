package ca.concordia;

import ca.concordia.view.BixiView;
import ca.concordia.controller.BixiController;
import ca.concordia.model.BixiTrip;

import ca.concordia.controller.IBixiController;

public class Main {
    public static void main(String[] args) {
        /*
        IBixiController controller = new BixiController();

        controller.loadFile("/Users/MarcoSalkica/Downloads/DonneesOuvertes2025_010203040506070809101112.csv");

        Iterable<BixiTrip> r = controller.getTripsByStartTime(
                "2025-06-01 00:00:00",
                "2025-06-01 23:59:59"
        );

        int c = 0;
        long prev = Long.MIN_VALUE;

        for (BixiTrip t : r) {
            if (t.getStartTimeMs() < prev) {
                System.out.println("NOT SORTED!");
                break;
            }
            prev = t.getStartTimeMs();
            c++;
        }

        System.out.println("Trips in interval: " + c);

        Iterable<String> top = controller.getTopStations(
                10,
                "2025-06-01 00:00:00",
                "2025-06-30 23:59:59"
        );

        for (String s : top) {
            System.out.println(s);
        }
        */

        BixiView view = new BixiView();
        view.start();

    }
}