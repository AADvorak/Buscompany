package net.thumbtack.school.buscompany.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = OneFieldNotNullValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface OneFieldNotNull {
    String message() default "One of two fields must be not null";
    String field1();
    String field2();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
