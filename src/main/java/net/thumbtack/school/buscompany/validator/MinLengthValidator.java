package net.thumbtack.school.buscompany.validator;

import lombok.RequiredArgsConstructor;
import net.thumbtack.school.buscompany.ApplicationProperties;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
public class MinLengthValidator implements ConstraintValidator<MinLength, String> {

    private final ApplicationProperties applicationProperties;

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return s.length() >= applicationProperties.getMinPasswordLength();
    }
}
