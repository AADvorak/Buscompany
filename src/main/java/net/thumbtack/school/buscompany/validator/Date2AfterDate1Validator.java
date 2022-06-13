package net.thumbtack.school.buscompany.validator;

import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.util.Objects;

public class Date2AfterDate1Validator implements ConstraintValidator<Date2AfterDate1, Object> {

    private String date1;
    private String date2;

    public void initialize(Date2AfterDate1 constraintAnnotation) {
        this.date1 = constraintAnnotation.date1();
        this.date2 = constraintAnnotation.date2();
    }

    public boolean isValid(Object o, ConstraintValidatorContext context) {
        LocalDate date1Value = LocalDate.parse((String) Objects.requireNonNull(new BeanWrapperImpl(o).getPropertyValue(date1)));
        LocalDate date2Value = LocalDate.parse((String) Objects.requireNonNull(new BeanWrapperImpl(o).getPropertyValue(date2)));
        assert date2Value != null;
        assert date1Value != null;
        return date2Value.isAfter(date1Value);
    }

}
