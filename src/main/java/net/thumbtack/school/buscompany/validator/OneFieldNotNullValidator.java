package net.thumbtack.school.buscompany.validator;

import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class OneFieldNotNullValidator implements ConstraintValidator<OneFieldNotNull, Object> {

    private String field1;
    private String field2;

    public void initialize(OneFieldNotNull constraintAnnotation) {
        this.field1 = constraintAnnotation.field1();
        this.field2 = constraintAnnotation.field2();
    }

    public boolean isValid(Object o, ConstraintValidatorContext context) {
        Object field1Value = new BeanWrapperImpl(o).getPropertyValue(field1);
        Object field2Value = new BeanWrapperImpl(o).getPropertyValue(field2);
        return !(field1Value != null && field2Value != null || field1Value == null && field2Value == null);
    }

}
