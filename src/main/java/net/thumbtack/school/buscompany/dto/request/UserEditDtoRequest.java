package net.thumbtack.school.buscompany.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.thumbtack.school.buscompany.validator.MaxLength;
import net.thumbtack.school.buscompany.validator.MinLength;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEditDtoRequest extends NameDtoRequest {

    private String oldPassword;

    @NotEmpty
    @MinLength
    @MaxLength
    private String newPassword;

}
