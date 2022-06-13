package net.thumbtack.school.buscompany.error;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    private List<Error> errors = new ArrayList<>();

}
