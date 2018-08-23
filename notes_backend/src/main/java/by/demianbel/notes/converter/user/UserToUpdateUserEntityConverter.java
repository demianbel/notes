package by.demianbel.notes.converter.user;

import by.demianbel.notes.converter.DtoToDboConverter;
import by.demianbel.notes.dbo.UserEntity;
import by.demianbel.notes.dto.user.UserToUpdateDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class UserToUpdateUserEntityConverter implements DtoToDboConverter<UserToUpdateDTO, UserEntity> {

    @Override
    public UserToUpdateDTO convertToDto(final UserEntity userEntity) {
        final UserToUpdateDTO userToUpdateDTO = new UserToUpdateDTO();
        BeanUtils.copyProperties(userEntity, userToUpdateDTO);
        return userToUpdateDTO;
    }

    @Override
    public UserEntity convertToDbo(final UserToUpdateDTO userToSaveDTO) {
        final UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(userToSaveDTO, userEntity);
        return userEntity;
    }
}
