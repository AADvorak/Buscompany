package net.thumbtack.school.buscompany.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.stream.Collectors;

public class PeriodValidator implements ConstraintValidator<Period, String> {

    private final String[] DAYS_OF_WEEK = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
    private final String[] DAYS_OF_MONTH = {"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15",
            "16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31"};

    private boolean isDayOfWeek(String s) {
        return Arrays.asList(DAYS_OF_WEEK).contains(s);
    }

    private boolean isDayOfMonth(String s) {
        return Arrays.asList(DAYS_OF_MONTH).contains(s);
    }

    @Override
    public boolean isValid(String period, ConstraintValidatorContext constraintValidatorContext) {
        if (period == null || period.isEmpty()) {
            return false;
        }
        if (period.equals("daily") || period.equals("odd") || period.equals("even")) {
            return true;
        }
        String[] items = period.split(",");
        boolean daysOfMonth = false, daysOfWeek = false;
        if (isDayOfMonth(items[0])) {
            daysOfMonth = true;
        }
        if (isDayOfWeek(items[0])) {
            daysOfWeek = true;
        }
        if (!daysOfMonth && !daysOfWeek) {
            return false;
        }
        for (String item : items) {
            if (daysOfMonth && !isDayOfMonth(item) || daysOfWeek && !isDayOfWeek(item)) {
                return false;
            }
        }
        return items.length == Arrays.stream(items).collect(Collectors.toSet()).size();
    }

}
