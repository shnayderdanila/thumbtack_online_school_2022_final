package net.thumbtack.school.buscompany.validator.phone;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class ValidatorPhoneNumber implements ConstraintValidator<PhoneNumber, String> {

    @Override
    public void initialize(PhoneNumber constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if(s == null) {
            return false;
        }
        constraintValidatorContext.disableDefaultConstraintViolation();

        constraintValidatorContext
                .buildConstraintViolationWithTemplate("Incorrect phone number:" + s)
                .addConstraintViolation();

        if(Pattern.matches("^((8|\\+7)[\\- ]?)(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{10}$",s)){

            s = s.replace("-", "").replace(" ", "");
            
            return true;
        }
        return false;
    }
}
