package net.thumbtack.school.buscompany.validator.trip;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TripValidator.class)
@Target( { ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Trip {

    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
