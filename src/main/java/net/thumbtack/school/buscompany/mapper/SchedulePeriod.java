package net.thumbtack.school.buscompany.mapper;

import lombok.Getter;

import java.time.DayOfWeek;

@Getter
public class SchedulePeriod {

    private String daysType = null;

    private DayOfWeek[] daysOfWeek = null;

    private Integer[] daysOfMonth = null;

    public SchedulePeriod(String period) {
        if (period.equals("daily") || period.equals("odd") || period.equals("even")) {
            daysType = period;
            return;
        }
        String[] items = period.split(",");
        try {
            Integer[] days = new Integer[items.length];
            for (int i = 0; i < items.length; i++) {
                days[i] = Integer.parseInt(items[i]);
            }
            daysOfMonth = days;
        } catch (NumberFormatException e) {
            DayOfWeek[] days = new DayOfWeek[items.length];
            for (int i = 0; i < items.length; i++) {
                switch (items[i]) {
                    case "Mon":
                        days[i] = DayOfWeek.MONDAY;
                        break;
                    case "Tue":
                        days[i] = DayOfWeek.TUESDAY;
                        break;
                    case "Wed":
                        days[i] = DayOfWeek.WEDNESDAY;
                        break;
                    case "Thu":
                        days[i] = DayOfWeek.THURSDAY;
                        break;
                    case "Fri":
                        days[i] = DayOfWeek.FRIDAY;
                        break;
                    case "Sat":
                        days[i] = DayOfWeek.SATURDAY;
                        break;
                    case "Sun":
                        days[i] = DayOfWeek.SUNDAY;
                        break;
                }
            }
            daysOfWeek = days;
        }
    }

}
