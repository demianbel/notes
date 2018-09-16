package by.demianbel.notes.converter.user;

import by.demianbel.notes.converter.DtoToDboConverter;
import by.demianbel.notes.dbo.UserEntity;
import by.demianbel.notes.dto.user.UserToSaveDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class UserToSaveUserEntityConverter implements DtoToDboConverter<UserToSaveDTO, UserEntity> {

    @Override
    public UserToSaveDTO convertToDto(final UserEntity userEntity) {
        throw new UnsupportedOperationException("We shouldn't convert dbo to saving dbo");
    }

    @Override
    public UserEntity convertToDbo(final UserToSaveDTO userToSaveDTO) {
        final UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(userToSaveDTO, userEntity);
        userEntity.setActive(true);
        return userEntity;
    }
}
