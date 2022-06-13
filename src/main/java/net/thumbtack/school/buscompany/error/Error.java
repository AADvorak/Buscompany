package net.thumbtack.school.buscompany.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Error {

    private String errorCode;

    private String field;

    private String message;

}
