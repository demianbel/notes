package by.demianbel.notes.converter.user;

import by.demianbel.notes.dbo.RoleEntity;
import by.demianbel.notes.dbo.UserEntity;
import by.demianbel.notes.dto.user.PersistedUserDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

public class PersistedUserUserEntityConverterTest {

    private static final String TEST_EMAIL = "test email";
    private static final String ROLE_NAME = "role name";

    private PersistedUserUserEntityConverter converter;

    @Before
    public void setUp() {
        converter = new PersistedUserUserEntityConverter();
    }

    @Test
    public void convertToDto() {
        final UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmail(TEST_EMAIL);

        final RoleEntity role = new RoleEntity();
        role.setName(ROLE_NAME);
        userEntity.setRoles(Collections.singleton(role));

        final PersistedUserDTO persistedUserDTO = converter.convertToDto(userEntity);

        Assert.assertEquals(Long.valueOf(1L), persistedUserDTO.getId());
        Assert.assertEquals(TEST_EMAIL, persistedUserDTO.getEmail());
        final List<String> roles = persistedUserDTO.getRoles();
        Assert.assertEquals(1, roles.size());
        Assert.assertEquals(ROLE_NAME, roles.get(0));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void convertToDbo() {
        converter.convertToDbo(null);
    }
}