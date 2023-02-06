package net.thumbtack.school.buscompany.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SettingsAdminDtoResponse extends SettingsDtoResponse{
    public SettingsAdminDtoResponse(int maxNameLength, int minPasswordLength) {
        super(maxNameLength, minPasswordLength);
    }

}
