package net.thumbtack.school.buscompany.api;

import net.thumbtack.school.buscompany.dto.response.SettingsDtoResponse;
import net.thumbtack.school.buscompany.exception.ServerException;
import net.thumbtack.school.buscompany.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/settings")
public class SettingsEndPoint {

    private final UserService userService;

    public SettingsEndPoint(UserService userService) {
        this.userService = userService;
    }

    public SettingsDtoResponse getSettings(String javaSessionId) throws ServerException {

        return userService.getSettings(javaSessionId);

    }

}
