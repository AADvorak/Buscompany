package net.thumbtack.school.buscompany.endpoint;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class EndpointBase {

    protected static final String JAVASESSIONID = "JAVASESSIONID";

    protected String decodeUTF8(String encoded) {
        if (encoded == null) return null;
        return URLDecoder.decode(encoded, StandardCharsets.UTF_8);
    }

}
