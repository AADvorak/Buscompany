package net.thumbtack.school.buscompany.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.thumbtack.school.buscompany.validator.MaxLength;
import net.thumbtack.school.buscompany.validator.Phone;

import javax.validation.constraints.Email;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientDtoRequest extends UserDtoRequest {

    @MaxLength
    @Email
    private String email;

    @Phone
    private String phone;

    public String getPhone() {
        String phoneReplaced = phone.replace("-", "");
        if (phoneReplaced.startsWith("+7")) {
            phoneReplaced = phoneReplaced.substring(2);
        } else if (phoneReplaced.startsWith("8")) {
            phoneReplaced = phoneReplaced.substring(1);
        }
        return phoneReplaced;
    }

}
