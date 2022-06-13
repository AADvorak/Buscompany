package net.thumbtack.school.buscompany.validator;

import lombok.RequiredArgsConstructor;
import net.thumbtack.school.buscompany.ApplicationProperties;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
public class MaxLengthValidator implements ConstraintValidator<MaxLength, String> {

    private final ApplicationProperties applicationProperties;

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return s.length() <= applicationProperties.getMaxNameLength();
    }

}
