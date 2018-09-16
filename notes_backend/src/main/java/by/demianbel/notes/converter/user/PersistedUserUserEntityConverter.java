package by.demianbel.notes.converter.user;

import by.demianbel.notes.converter.DtoToDboConverter;
import by.demianbel.notes.dbo.RoleEntity;
import by.demianbel.notes.dbo.UserEntity;
import by.demianbel.notes.dto.user.PersistedUserDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersistedUserUserEntityConverter implements DtoToDboConverter<PersistedUserDTO, UserEntity> {
    @Override
    public PersistedUserDTO convertToDto(final UserEntity userEntity) {
        final PersistedUserDTO persistedUserDTO = new PersistedUserDTO();
        BeanUtils.copyProperties(userEntity, persistedUserDTO);
        final List<String> roles = userEntity.getRoles().stream().map(RoleEntity::getName).collect(Collectors.toList());
        persistedUserDTO.setRoles(roles);
        return persistedUserDTO;
    }

    @Override
    public UserEntity convertToDbo(final PersistedUserDTO persistedUserDTO) {
        throw new UnsupportedOperationException("We shouldn't convert persisted dto to dbo");
    }
}
