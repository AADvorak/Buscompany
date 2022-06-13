package net.thumbtack.school.buscompany.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MinLengthValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface MinLength {
    String message() default "Min length violation";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
