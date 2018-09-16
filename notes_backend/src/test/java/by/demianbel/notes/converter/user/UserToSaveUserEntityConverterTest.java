package by.demianbel.notes.converter.user;

import by.demianbel.notes.dbo.UserEntity;
import by.demianbel.notes.dto.user.UserToSaveDTO;
import org.junit.Assert;
import org.junit.Test;

public class UserToSaveUserEntityConverterTest {

    private static final String TEST_USER_NAME = "test user name";
    private static final String TEST_USER_EMAIL = "test user email";
    private static final String TEST_USER_PASSWORD = "test user password";

    @Test(expected = UnsupportedOperationException.class)
    public void convertToDto() {
        new UserToSaveUserEntityConverter().convertToDto(null);
    }


    @Test
    public void convertToDbo() {
        final UserToSaveDTO userToSaveDTO = new UserToSaveDTO();
        userToSaveDTO.setName(TEST_USER_NAME);
        userToSaveDTO.setEmail(TEST_USER_EMAIL);
        userToSaveDTO.setPassword(TEST_USER_PASSWORD);

        final UserToSaveUserEntityConverter converter = new UserToSaveUserEntityConverter();
        final UserEntity userEntity = converter.convertToDbo(userToSaveDTO);
        Assert.assertEquals(TEST_USER_EMAIL, userEntity.getEmail());
        Assert.assertEquals(TEST_USER_NAME, userEntity.getName());
        Assert.assertEquals(TEST_USER_PASSWORD, userEntity.getPassword());

    }
}