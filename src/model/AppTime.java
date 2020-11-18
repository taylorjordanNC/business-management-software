package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

/** This class contains the method for the creation of local time values for the business hours and available appointment times. */
public class AppTime {
    private static ObservableList<String> timeList = FXCollections.observableArrayList();

    /** This method creates the observable list of times that is called by the appointment forms.
     This method takes the local business hours in eastern standard time and converts them into a list of 30
     minute appointment slots for the user to select from to schedule appointments. The zoned times for EST convert
     to whichever time zone is set as the default on the user's computer. Therefore, the only times available they will see
     are the times between 8:00 AM and 10:00 PM (EST).

     @param date The local date passed to the method by the user to create the ZonedDateTime. */
    public static ObservableList<String> getTimeList(LocalDate date) {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("h:mm a").toFormatter();
        ZonedDateTime businessHourOpen = ZonedDateTime.of(date, LocalTime.of(8, 0), ZoneId.of("US/Eastern"));
        ZonedDateTime businessHourClose = ZonedDateTime.of(date, LocalTime.of(22, 0), ZoneId.of("US/Eastern"));

        ZonedDateTime open = businessHourOpen.withZoneSameInstant(ZoneId.systemDefault());
        ZonedDateTime close = businessHourClose.withZoneSameInstant(ZoneId.systemDefault());
        LocalTime estOpen = open.toLocalTime();
        LocalTime estClose = close.toLocalTime();

        int a = 0;
        int b = 29;
        while (a < b) {
            timeList.add(formatter.format(estOpen));
            estOpen = estOpen.plusMinutes(30);
            a++;
        }
        return timeList;
    }
}
