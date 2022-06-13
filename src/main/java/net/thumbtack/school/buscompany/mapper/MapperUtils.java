package net.thumbtack.school.buscompany.mapper;

import java.sql.Time;

public class MapperUtils {

    public static int minutesFromTimeStr(String timeStr) {
        String[] arr = timeStr.split(":");
        return Integer.parseInt(arr[0]) * 60 + Integer.parseInt(arr[1]);
    }

    public static String timeStrFromMinutes(int minutes) {
        int hh = minutes / 60;
        int mm = minutes % 60;
        return intToTwoSymbolStr(hh) + ":" + intToTwoSymbolStr(mm);
    }

    public static String strFromTime(Time time) {
        String fullStr = time.toString();
        return fullStr.substring(0, fullStr.lastIndexOf(":"));
    }

    private static String intToTwoSymbolStr(int value) {
        return (value < 10 ? "0" : "") + value;
    }

}
