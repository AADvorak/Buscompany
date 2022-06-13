package net.thumbtack.school.buscompany.mapper;

import net.thumbtack.school.buscompany.ApplicationProperties;
import net.thumbtack.school.buscompany.dto.response.SettingsAdminDtoResponse;
import net.thumbtack.school.buscompany.dto.response.SettingsClientDtoResponse;
import net.thumbtack.school.buscompany.dto.response.SettingsDtoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SettingsMapper {

    SettingsMapper INSTANCE = Mappers.getMapper(SettingsMapper.class);

    SettingsDtoResponse settingsToDto(ApplicationProperties settings);

    SettingsClientDtoResponse settingsToDtoClient(ApplicationProperties settings);

    SettingsAdminDtoResponse settingsToDtoAdmin(ApplicationProperties settings);

}
