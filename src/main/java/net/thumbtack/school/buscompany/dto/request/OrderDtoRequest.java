package net.thumbtack.school.buscompany.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.thumbtack.school.buscompany.validator.Message;
import net.thumbtack.school.buscompany.validator.Regexp;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDtoRequest {

    @Positive
    private int tripId;

    @NotNull
    @Pattern(regexp = Regexp.DATE, message = Message.DATE)
    private String date;

    @Valid
    @NotEmpty
    private List<PassengerDtoRequest> passengers;

}
