package net.thumbtack.school.buscompany.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class BusAppException extends Exception {

    private final BusAppErrorCode errorCode;

}
