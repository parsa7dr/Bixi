package ca.concordia.controller;

import ca.concordia.model.BixiTrip;

public interface IBixiController {

    /**
     * Loads a Bixi data file from the specified file path.
     * @param filePath the path to the Bixi data file
     */
    void loadFile(String filePath);

    /**
     * Retrieves trips by station name and mode (start or end).
     * @param stationName
     * @param mode
     * @return
     */
    Iterable<BixiTrip> getTripsByStation(String stationName, String mode);

    /**
     * Retrieves trips by month.
     * @param month the month in "YYYY-MM" format
     * @return an iterable of BixiTrip objects for the specified month
     */
    Iterable<BixiTrip> getTripsByMonth(String month);


    /**
     * Retrieves trips with a duration greater than or equal to the specified minimum duration.
     * @param minDuration the minimum duration in minutes
     * @return an iterable of BixiTrip objects with duration >= minDuration
     */
    Iterable<BixiTrip> getTripsByDuration(float minDuration);

    /**
     * Retrieves trips that started within the specified time range.
     * @param startTime the start time in "YYYY-MM-DD HH:MM:SS" format
     * @param finalTime the end time in "YYYY-MM-DD HH:MM:SS" format
     * @return an iterable of BixiTrip objects that started within the specified time range
     */
    Iterable<BixiTrip> getTripsByStartTime(String startTime, String finalTime);


    /**
     * Retrieves the top k arrondissements with the highest number of trips.
     * @param k the number of top arrondissements to retrieve
     * @return an iterable of arrondissement names
     */
    Iterable<String> getTopArrondissements(int k);


    /**
     * Retrieves the top k stations with the highest number of trips within the specified date range.
     * @param k the number of top stations to retrieve
     * @param startDate  the start date in "YYYY-MM-DD" format
     * @param endDate the end date in "YYYY-MM-DD" format
     * @return an iterable of station names
     */
    Iterable<String> getTopStations(int k, String startDate, String endDate);

    /**
     * Retrieves the rush hour (hour with the highest average number of trips) for the specified month.
     * @param month the month as an integer (1-12)
     * @return the hour (0-23) with the highest number of trips in the specified month
     */
    int getRushHourOfMonth(int month);

    //TODO define method for requirement 8 - define type of output

}
