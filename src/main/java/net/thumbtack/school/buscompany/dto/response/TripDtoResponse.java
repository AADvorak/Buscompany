package net.thumbtack.school.buscompany.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripDtoResponse {

    private int id;

    private String fromStation;

    private String toStation;

    private String start;

    private String duration;

    private BigDecimal price;

    private BusDtoResponse bus;

    private boolean approved;

    private ScheduleDtoResponse schedule;

    private List<String> dates;

}
