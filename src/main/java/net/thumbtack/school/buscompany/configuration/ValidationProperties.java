package net.thumbtack.school.buscompany.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "valid")
@Component
@Data
public class ValidationProperties {
    public int max_name_length;
    public int min_password_length;
}
