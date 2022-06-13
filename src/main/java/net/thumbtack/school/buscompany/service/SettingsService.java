package net.thumbtack.school.buscompany.service;

import net.thumbtack.school.buscompany.ApplicationProperties;
import net.thumbtack.school.buscompany.dto.response.SettingsDtoResponse;
import net.thumbtack.school.buscompany.error.BusAppErrorCode;
import net.thumbtack.school.buscompany.error.BusAppException;
import net.thumbtack.school.buscompany.mapper.SettingsMapper;
import net.thumbtack.school.buscompany.model.Admin;
import net.thumbtack.school.buscompany.model.User;
import net.thumbtack.school.buscompany.repository.UserSessionRepository;
import org.springframework.stereotype.Service;

@Service
public class SettingsService extends ServiceBase {

    public SettingsService(UserSessionRepository userSessionRepository, ApplicationProperties applicationProperties) {
        super(userSessionRepository, applicationProperties);
    }

    public SettingsDtoResponse getSettings(String sessionId) throws BusAppException {
        User user;
        try {
            user = getUserBySessionId(sessionId);
        } catch (BusAppException ex) {
            if (ex.getErrorCode().equals(BusAppErrorCode.SESSION_NOT_FOUND)) {
                return SettingsMapper.INSTANCE.settingsToDto(applicationProperties);
            }
            throw ex;
        }
        if (user instanceof Admin) {
            return SettingsMapper.INSTANCE.settingsToDtoAdmin(applicationProperties);
        }
        return SettingsMapper.INSTANCE.settingsToDtoClient(applicationProperties);
    }

}
