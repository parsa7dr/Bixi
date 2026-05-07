package ca.concordia.model;

import java.time.Instant;
import java.time.ZoneId;

public class BixiTrip {

    private final String startStationName;
    private final String startStationArrondissement;
    private final double startStationLatitude;
    private final double startStationLongitude;

    private final String endStationName;
    private final String endStationArrondissement;
    private final double endStationLatitude;
    private final double endStationLongitude;

    private final long startTimeMs;
    private final long endTimeMs;

    public BixiTrip(
            String startStationName,
            String startStationArrondissement,
            double startStationLatitude,
            double startStationLongitude,
            String endStationName,
            String endStationArrondissement,
            double endStationLatitude,
            double endStationLongitude,
            long startTimeMs,
            long endTimeMs
    ) {
        this.startStationName = startStationName;
        this.startStationArrondissement = startStationArrondissement;
        this.startStationLatitude = startStationLatitude;
        this.startStationLongitude = startStationLongitude;

        this.endStationName = endStationName;
        this.endStationArrondissement = endStationArrondissement;
        this.endStationLatitude = endStationLatitude;
        this.endStationLongitude = endStationLongitude;

        this.startTimeMs = startTimeMs;
        this.endTimeMs = endTimeMs;
    }

    public String getStartStationName() { return startStationName; }
    public String getStartStationArrondissement() { return startStationArrondissement; }
    public double getStartStationLatitude() { return startStationLatitude; }
    public double getStartStationLongitude() { return startStationLongitude; }

    public String getEndStationName() { return endStationName; }
    public String getEndStationArrondissement() { return endStationArrondissement; }
    public double getEndStationLatitude() { return endStationLatitude; }
    public double getEndStationLongitude() { return endStationLongitude; }

    public long getStartTimeMs() { return startTimeMs; }
    public long getEndTimeMs() { return endTimeMs; }

    public double getDurationMinutes() {
        return (endTimeMs - startTimeMs) / (1000.0 * 60.0);
    }

    @Override
    public String toString() {
        ZoneId zone = ZoneId.systemDefault();

        String start = Instant.ofEpochMilli(startTimeMs).atZone(zone).toLocalDateTime().toString();
        String end   = Instant.ofEpochMilli(endTimeMs).atZone(zone).toLocalDateTime().toString();

        return startStationName + " -> " + endStationName
                + " | start=" + start + " (" + startTimeMs + ")"
                + " | end=" + end + " (" + endTimeMs + ")";
    }
}
