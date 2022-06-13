package net.thumbtack.school.buscompany.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.thumbtack.school.buscompany.validator.MaxLength;
import net.thumbtack.school.buscompany.validator.Message;
import net.thumbtack.school.buscompany.validator.Regexp;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NameDtoRequest {

    @NotEmpty
    @MaxLength
    @Pattern(regexp = Regexp.NAME, message = Message.NAME)
    private String firstName;

    @NotEmpty
    @MaxLength
    @Pattern(regexp = Regexp.NAME, message = Message.NAME)
    private String lastName;

    @MaxLength
    @Pattern(regexp = Regexp.NAME, message = Message.NAME)
    private String patronymic;

}
