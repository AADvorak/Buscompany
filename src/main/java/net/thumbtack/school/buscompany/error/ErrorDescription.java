package net.thumbtack.school.buscompany.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ErrorDescription {

    private final String field;

    private final String description;

}
