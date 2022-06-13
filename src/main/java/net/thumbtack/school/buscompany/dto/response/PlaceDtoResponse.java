package net.thumbtack.school.buscompany.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceDtoResponse {

    private int orderId;

    private String firstName;

    private String lastName;

    private String passport;

    private int place;

    private String ticket;

}
