package net.thumbtack.school.buscompany.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.thumbtack.school.buscompany.validator.MaxLength;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassengerDtoRequest {

    @MaxLength
    @NotEmpty
    private String firstName;

    @MaxLength
    @NotEmpty
    private String lastName;

    @MaxLength
    @NotEmpty
    private String passport;

}
