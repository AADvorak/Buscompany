package net.thumbtack.school.buscompany.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.thumbtack.school.buscompany.validator.MaxLength;
import net.thumbtack.school.buscompany.validator.Message;
import net.thumbtack.school.buscompany.validator.MinLength;
import net.thumbtack.school.buscompany.validator.Regexp;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDtoRequest extends NameDtoRequest {

    @MaxLength
    @NotEmpty
    @Pattern(regexp = Regexp.LOGIN, message = Message.LOGIN)
    private String login;

    @NotEmpty
    @MinLength
    @MaxLength
    private String password;

}
