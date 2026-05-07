package ca.concordia.controller;

import ca.concordia.datastructures.MyArrayList;
import ca.concordia.datastructures.MyHashTable;
import ca.concordia.model.BixiTrip;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.ZonedDateTime;

public class BixiController implements IBixiController {

    private MyArrayList<BixiTrip> trips;

    public BixiController() {
        trips = new MyArrayList<>();
    }

    private static final DateTimeFormatter DT_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private long parseDateTimeToMs(String s) {
        // expects "YYYY-MM-DD hh:mm:ss"
        LocalDateTime ldt = LocalDateTime.parse(s.trim(), DT_FMT);
        return ldt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    @Override
    public void loadFile(String filePath) {
        /*
        // Implementation to load the file

        System.out.println("Loading file from: " + filePath);
        Path path = Path.of("data/bixi2025.csv");

        try (var lines = Files.lines(path)) {
            lines.forEach(this::parseLine);
        } catch (IOException e) {
            e.printStackTrace();
        }
        */





        trips = new MyArrayList<>(); //resets each load
        System.out.println("Loading file: " + filePath);

        long lineCount = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath), 1 << 20)) {//1mb
            String line = br.readLine(); //skips the header

            //reads each trip line-by-line
            while ((line = br.readLine()) != null) {
                parseLine(line);
                lineCount++;

                //(progress print every 1,000,000 lines), might remove later just for testing
                if (lineCount % 1_000_000 == 0) {
                    System.out.println("Read lines: " + lineCount + " | Trips stored: " + trips.size());
                }
            }

            System.out.println("Done.");
            System.out.println("Total trips loaded: " + trips.size());

        } catch (IOException e) {
            System.out.println("ERROR reading file: " + e.getMessage());
        }
        System.out.println("Unique stations loaded: " + countUniqueStations());
    }

    private int countUniqueStations() { //for counting each individual station (name), uses hash tables so is faster
        MyHashTable<String, Boolean> seen =
                new MyHashTable<>(200_003);  //prime-ish bucket count

        int unique = 0;

        for (int i = 0; i < trips.size(); i++) {
            BixiTrip t = trips.get(i);

            unique += addIfNew(seen, t.getStartStationName());
            unique += addIfNew(seen, t.getEndStationName());
        }

        return unique;
    }

    //adds a station if not previously added
    private int addIfNew(MyHashTable<String, Boolean> seen, String name) {
        if (name == null) return 0;
        name = name.trim();
        if (name.isEmpty()) return 0;

        if (seen.get(name) != null) return 0;

        seen.put(name, true);
        return 1;
    }

     @Override
public Iterable<BixiTrip> getTripsByStation(String stationName, String mode) {

    MyArrayList<BixiTrip> result = new MyArrayList<>();

    if (stationName == null || mode == null) return result;

    stationName = stationName.trim();

    for (int i = 0; i < trips.size(); i++) {

        BixiTrip t = trips.get(i);

        boolean matchStart = t.getStartStationName().equals(stationName);
        boolean matchEnd = t.getEndStationName().equals(stationName);

        if (mode.equalsIgnoreCase("start") && matchStart) {
            result.add(t);
        }
        else if (mode.equalsIgnoreCase("end") && matchEnd) {
            result.add(t);
        }
        else if (mode.equalsIgnoreCase("both") && (matchStart || matchEnd)) {
            result.add(t);
        }
    }

    return result;
}


    @Override
    public Iterable<BixiTrip> getTripsByMonth(String month) {
        // month format: "YYYY-MM"
        String s = month.trim();
        if (!s.matches("\\d{4}-\\d{2}")) throw new IllegalArgumentException("Expected YYYY-MM");

        int year = Integer.parseInt(s.substring(0, 4));
        int mon  = Integer.parseInt(s.substring(5, 7)); // 1..12

        MyArrayList<BixiTrip> out = new MyArrayList<>();

        for (int i = 0; i < trips.size(); i++) {
            BixiTrip t = trips.get(i);

            long startMs = t.getStartTimeMs();
            ZonedDateTime zdt = Instant.ofEpochMilli(startMs).atZone(ZoneId.systemDefault());

            if (zdt.getYear() == year && zdt.getMonthValue() == mon) {
                out.add(t);
            }
        }

        // sort ascending by start time
        mergeSortByStartTime(out);
        return out;
    }

    //methods for merge sort, quicker for REQ 2
    private void mergeSortByStartTime(MyArrayList<BixiTrip> list) {
        if (list.size() <= 1) return;
        BixiTrip[] temp = new BixiTrip[list.size()];
        mergeSortRec(list, temp, 0, list.size() - 1);
    }

    private void mergeSortRec(MyArrayList<BixiTrip> list, BixiTrip[] temp, int left, int right) {
        if (left >= right) return;

        int mid = left + (right - left) / 2;
        mergeSortRec(list, temp, left, mid);
        mergeSortRec(list, temp, mid + 1, right);
        merge(list, temp, left, mid, right);
    }

    private void merge(MyArrayList<BixiTrip> list, BixiTrip[] temp, int left, int mid, int right) {
        int i = left;      // left half pointer
        int j = mid + 1;   // right half pointer
        int k = left;      // temp pointer

        while (i <= mid && j <= right) {
            long a = list.get(i).getStartTimeMs();
            long b = list.get(j).getStartTimeMs();

            if (a <= b) {
                temp[k++] = list.get(i++);
            } else {
                temp[k++] = list.get(j++);
            }
        }

        while (i <= mid) temp[k++] = list.get(i++);
        while (j <= right) temp[k++] = list.get(j++);

        // copy back
        for (int idx = left; idx <= right; idx++) {
            list.set(idx, temp[idx]);
        }
    }

   
@Override
public Iterable<BixiTrip> getTripsByDuration(float minDuration) {

    MyArrayList<BixiTrip> filtered = new MyArrayList<>();

    for (int i = 0; i < trips.size(); i++) {

        BixiTrip t = trips.get(i);

        if (t.getDurationMinutes() >= minDuration) {
            filtered.add(t);
        }
    }

    BixiTrip[] arr = filtered.toArray();

    for (int i = 0; i < arr.length - 1; i++) {
        for (int j = 0; j < arr.length - i - 1; j++) {

            if (arr[j].getDurationMinutes()
                    < arr[j + 1].getDurationMinutes()) {

                BixiTrip temp = arr[j];
                arr[j] = arr[j + 1];
                arr[j + 1] = temp;
            }
        }
    }

    MyArrayList<BixiTrip> result = new MyArrayList<>();
    for (BixiTrip t : arr) {
        result.add(t);
    }

    return result;
}
    @Override
    public Iterable<BixiTrip> getTripsByStartTime(String startTime, String finalTime) {
        long start = parseDateTimeToMs(startTime);
        long end   = parseDateTimeToMs(finalTime);

        // (optional safety) swap if user reversed them
        if (start > end) {
            long tmp = start; start = end; end = tmp;
        }

        MyArrayList<BixiTrip> out = new MyArrayList<>();

        for (int i = 0; i < trips.size(); i++) {
            BixiTrip t = trips.get(i);
            long ms = t.getStartTimeMs();
            if (ms >= start && ms <= end) {
                out.add(t);
            }
        }

        // sorts by start time ascending
        mergeSortByStartTime(out);   // reusing the merge sort from earlier
        return out;
        //return null;
    }

 @Override
public Iterable<String> getTopArrondissements(int k) {

    MyArrayList<String> empty = new MyArrayList<>();
    if (k <= 0) return empty;

    MyHashTable<String, Integer> counts = new MyHashTable<>(5003);

    
    for (int i = 0; i < trips.size(); i++) {

        BixiTrip t = trips.get(i);
        String arr = t.getStartStationArrondissement();

        if (arr == null) continue;
        arr = arr.trim();
        if (arr.isEmpty()) continue;

        Integer cur = counts.get(arr);
        if (cur == null) counts.put(arr, 1);
        else counts.put(arr, cur + 1);
    }

    MyArrayList<MyHashTable.EntryView<String, Integer>> entries = counts.entries();
    MyArrayList<StationCount> all = new MyArrayList<>();

    for (int i = 0; i < entries.size(); i++) {
        MyHashTable.EntryView<String, Integer> e = entries.get(i);
        all.add(new StationCount(e.key, e.value));
    }

    if (all.size() == 0) return empty;

    
    mergeSortByCountDesc(all);

    int take = Math.min(k, all.size());
    MyArrayList<StationCount> top = new MyArrayList<>();
    for (int i = 0; i < take; i++) top.add(all.get(i));

    
    mergeSortByNameAsc(top);

    MyArrayList<String> result = new MyArrayList<>();
    for (int i = 0; i < top.size(); i++) {
        StationCount sc = top.get(i);
        result.add(sc.name + " : " + sc.count);
    }

    return result;
}
    private static class StationCount {
        String name;
        int count;

        StationCount(String name, int count) {
            this.name = name;
            this.count = count;
        }
    }

    @Override
    public Iterable<String> getTopStations(int k, String startDate, String endDate) {
        MyArrayList<String> empty = new MyArrayList<>();
        if (k <= 0 || startDate == null || endDate == null) return empty;

        long startMs;
        long endMs;
        try {
            startMs = parseDateTimeToMs(startDate);
            endMs = parseDateTimeToMs(endDate);
        } catch (Exception e) {
            return empty;
        }

        if (startMs > endMs) {
            long tmp = startMs;
            startMs = endMs;
            endMs = tmp;
        }

        // 1) Count start-station usage within interval
        // 5003 is plenty for ~1300 stations; prime-ish helps distribution
        MyHashTable<String, Integer> counts = new MyHashTable<>(5003);

        for (int i = 0; i < trips.size(); i++) {
            BixiTrip t = trips.get(i);

            long ms = t.getStartTimeMs();
            if (ms < startMs || ms > endMs) continue;

            String station = t.getStartStationName();
            if (station == null) continue;

            station = station.trim();
            if (station.isEmpty()) continue;

            Integer cur = counts.get(station);
            if (cur == null) counts.put(station, 1);
            else counts.put(station, cur + 1);
        }

        // 2) Convert hashtable entries -> list of StationCount
        MyArrayList<MyHashTable.EntryView<String, Integer>> entries = counts.entries();
        MyArrayList<StationCount> all = new MyArrayList<>();

        for (int i = 0; i < entries.size(); i++) {
            MyHashTable.EntryView<String, Integer> e = entries.get(i);
            all.add(new StationCount(e.key, e.value));
        }

        if (all.size() == 0) return empty;

        // 3) Sort ALL stations by count DESC
        mergeSortByCountDesc(all);

        // 4) Take top K
        int take = Math.min(k, all.size());
        MyArrayList<StationCount> top = new MyArrayList<>();
        for (int i = 0; i < take; i++) top.add(all.get(i));

        // 5) Sort top K alphabetically by station name ASC (as required)
        mergeSortByNameAsc(top);

        // 6) Build output strings (name + count)
        MyArrayList<String> result = new MyArrayList<>();
        for (int i = 0; i < top.size(); i++) {
            StationCount sc = top.get(i);
            result.add(sc.name + " : " + sc.count);
        }
        return result;
        //return null;
    }

    //merge helper methods for REQ6
    private void mergeSortByCountDesc(MyArrayList<StationCount> list) {
        if (list.size() <= 1) return;
        StationCount[] temp = new StationCount[list.size()];
        mergeSortCountRec(list, temp, 0, list.size() - 1);
    }

    private void mergeSortCountRec(MyArrayList<StationCount> list, StationCount[] temp, int left, int right) {
        if (left >= right) return;
        int mid = left + (right - left) / 2;
        mergeSortCountRec(list, temp, left, mid);
        mergeSortCountRec(list, temp, mid + 1, right);
        mergeCount(list, temp, left, mid, right);
    }

    private void mergeCount(MyArrayList<StationCount> list, StationCount[] temp, int left, int mid, int right) {
        int i = left, j = mid + 1, k = left;

        while (i <= mid && j <= right) {
            // DESC by count
            if (list.get(i).count >= list.get(j).count) temp[k++] = list.get(i++);
            else temp[k++] = list.get(j++);
        }
        while (i <= mid) temp[k++] = list.get(i++);
        while (j <= right) temp[k++] = list.get(j++);

        for (int idx = left; idx <= right; idx++) {
            list.set(idx, temp[idx]);
        }
    }

    private void mergeSortByNameAsc(MyArrayList<StationCount> list) {
        if (list.size() <= 1) return;
        StationCount[] temp = new StationCount[list.size()];
        mergeSortNameRec(list, temp, 0, list.size() - 1);
    }

    private void mergeSortNameRec(MyArrayList<StationCount> list, StationCount[] temp, int left, int right) {
        if (left >= right) return;
        int mid = left + (right - left) / 2;
        mergeSortNameRec(list, temp, left, mid);
        mergeSortNameRec(list, temp, mid + 1, right);
        mergeName(list, temp, left, mid, right);
    }

    private void mergeName(MyArrayList<StationCount> list, StationCount[] temp, int left, int mid, int right) {
        int i = left, j = mid + 1, k = left;

        while (i <= mid && j <= right) {
            // ASC by station name (case-insensitive)
            String a = list.get(i).name;
            String b = list.get(j).name;

            if (a.compareToIgnoreCase(b) <= 0) temp[k++] = list.get(i++);
            else temp[k++] = list.get(j++);
        }
        while (i <= mid) temp[k++] = list.get(i++);
        while (j <= right) temp[k++] = list.get(j++);

        for (int idx = left; idx <= right; idx++) {
            list.set(idx, temp[idx]);
        }
    }

 @Override
public int getRushHourOfMonth(int month) {

    int[] totalTrips = new int[24];
    int[] dayCounts = new int[24];
    boolean[][] seenDayHour = new boolean[24][32];

    for (int i = 0; i < trips.size(); i++) {

        BixiTrip t = trips.get(i);

        java.time.LocalDateTime dateTime =
                java.time.Instant.ofEpochMilli(t.getStartTimeMs())
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDateTime();

        if (dateTime.getMonthValue() == month) {

            int hour = dateTime.getHour();
            int day = dateTime.getDayOfMonth();

            totalTrips[hour]++;

            if (!seenDayHour[hour][day]) {
                seenDayHour[hour][day] = true;
                dayCounts[hour]++;
            }
        }
    }

    double maxAverage = 0;
    int rushHour = 0;

    for (int h = 0; h < 24; h++) {

        if (dayCounts[h] > 0) {

            double avg = (double) totalTrips[h] / dayCounts[h];

            if (avg > maxAverage) {
                maxAverage = avg;
                rushHour = h;
            }
        }
    }

    return rushHour;
}


    private void parseLine(String line) {

        //Implementation to parse datA
        String[] p = line.split(",", -1); //keeps empty fields

        if (p.length < 10) return;

        String startName = p[0].trim();
        String startArr = p[1].trim();
        double startLat = parseDoubleSafe(p[2]);
        double startLon = parseDoubleSafe(p[3]);

        String endName = p[4].trim();
        String endArr = p[5].trim();
        double endLat = parseDoubleSafe(p[6]);
        double endLon = parseDoubleSafe(p[7]);

        long startMs = parseLongSafe(p[8]);
        long endMs = parseLongSafe(p[9]);

        BixiTrip trip = new BixiTrip(
                startName, startArr, startLat, startLon,
                endName, endArr, endLat, endLon,
                startMs, endMs
        );

        trips.add(trip);

        /*
        //OG code (keeping it just in case)
        String[] fields = data.split(",");
        for (String field : fields) {
            System.out.println(field);
        }
         */
    }

    //Added these (2) to deal with empty parameters in the data file. vv
    private double parseDoubleSafe(String s) {
        if (s == null || s.isBlank()) return 0.0;
        return Double.parseDouble(s.trim());
    }

    private long parseLongSafe(String s) {
        if (s == null || s.isBlank()) return 0L;
        return Long.parseLong(s.trim());
    }

}
