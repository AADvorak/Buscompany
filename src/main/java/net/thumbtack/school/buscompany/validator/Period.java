package net.thumbtack.school.buscompany.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PeriodValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Period {
    String message() default "Wrong period format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
