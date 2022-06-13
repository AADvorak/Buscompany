package net.thumbtack.school.buscompany.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.thumbtack.school.buscompany.validator.Date2AfterDate1;
import net.thumbtack.school.buscompany.validator.Message;
import net.thumbtack.school.buscompany.validator.Period;
import net.thumbtack.school.buscompany.validator.Regexp;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Date2AfterDate1(
        date1 = "fromDate",
        date2 = "toDate",
        message = "toDate must be after fromDate"
)
public class ScheduleDtoRequest {

    @NotNull
    @Pattern(regexp = Regexp.DATE, message = Message.DATE)
    private String fromDate;

    @NotNull
    @Pattern(regexp = Regexp.DATE, message = Message.DATE)
    private String toDate;

    @Period
    private String period;

}
