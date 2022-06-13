package net.thumbtack.school.buscompany.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.thumbtack.school.buscompany.validator.MaxLength;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceDtoRequest {

    @Positive
    private int orderId;

    @MaxLength
    @NotEmpty
    private String firstName;

    @MaxLength
    @NotEmpty
    private String lastName;

    @MaxLength
    @NotEmpty
    private String passport;

    @Positive
    private int place;
}
