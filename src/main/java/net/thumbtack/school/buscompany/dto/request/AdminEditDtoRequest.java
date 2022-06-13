package net.thumbtack.school.buscompany.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.thumbtack.school.buscompany.validator.MaxLength;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminEditDtoRequest extends UserEditDtoRequest {

    @MaxLength
    private String position;

}
