package net.thumbtack.school.buscompany.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = Date2AfterDate1Validator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Date2AfterDate1 {
    String message() default "Date2 must be after date1";
    String date1();
    String date2();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
