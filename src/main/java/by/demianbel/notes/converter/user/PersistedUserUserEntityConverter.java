package by.demianbel.notes.converter.user;

import by.demianbel.notes.converter.DtoToDboConverter;
import by.demianbel.notes.dbo.UserEntity;
import by.demianbel.notes.dto.user.PersistedUserDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class PersistedUserUserEntityConverter implements DtoToDboConverter<PersistedUserDTO, UserEntity> {
    @Override
    public PersistedUserDTO convertToDto(final UserEntity userEntity) {
        final PersistedUserDTO persistedUserDTO = new PersistedUserDTO();
        BeanUtils.copyProperties(userEntity, persistedUserDTO);
        return persistedUserDTO;
    }

    @Override
    public UserEntity convertToDbo(final PersistedUserDTO persistedUserDTO) {
        final UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(persistedUserDTO, userEntity);
        return userEntity;
    }
}
