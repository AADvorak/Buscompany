package net.thumbtack.school.buscompany.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDtoResponse {

    private int id;

    private int tripId;

    private String fromStation;

    private String toStation;

    private String busName;

    private String date;

    private String start;

    private String duration;

    private BigDecimal price;

    private BigDecimal totalPrice;

    private List<PassengerDtoResponse> passengers;

}
