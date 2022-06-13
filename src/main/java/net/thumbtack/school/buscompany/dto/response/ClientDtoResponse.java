package net.thumbtack.school.buscompany.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientDtoResponse extends UserDtoResponse {

    private String email;

    private String phone;

    private final String userType = "client";

    public void setPhone(String phone) {
        this.phone = phone.startsWith("+7") ? phone : "+7" + phone;
    }

}
