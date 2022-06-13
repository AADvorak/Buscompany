package net.thumbtack.school.buscompany.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.thumbtack.school.buscompany.validator.Message;
import net.thumbtack.school.buscompany.validator.OneFieldNotNull;
import net.thumbtack.school.buscompany.validator.Regexp;
import org.hibernate.validator.constraints.UniqueElements;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@OneFieldNotNull(
        field1 = "schedule",
        field2 = "dates",
        message = "Only one of fields schedule and dates must be not null"
)
public class TripDtoRequest {

    @Positive
    private int busId;

    @NotEmpty
    private String fromStation;

    @NotEmpty
    private String toStation;

    @NotNull
    @Pattern(regexp = Regexp.TIME_24, message = Message.TIME_24)
    private String start;

    @NotNull
    @Pattern(regexp = Regexp.TIME, message = Message.TIME)
    private String duration;

    @DecimalMin(value = "0.0", inclusive = false)
    @Digits(integer = 11, fraction = 2)
    private BigDecimal price;

    @Valid
    private ScheduleDtoRequest schedule;

    @UniqueElements
    private List<@NotNull @Pattern(regexp = Regexp.DATE, message = Message.DATE) String> dates;

}
