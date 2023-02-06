package net.thumbtack.school.buscompany.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SettingsClientDtoResponse extends SettingsDtoResponse{

    public SettingsClientDtoResponse(int maxNameLength, int minPasswordLength) {
        super(maxNameLength, minPasswordLength);
    }
}
