package net.thumbtack.school.buscompany;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "server")
@Component
@Data
public class ServerProperties {

    private String port;

}
