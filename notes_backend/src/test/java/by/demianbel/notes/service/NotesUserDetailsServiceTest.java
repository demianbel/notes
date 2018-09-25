package by.demianbel.notes.service;

import by.demianbel.notes.converter.tag.PersistedTagTagEntityConverter;
import by.demianbel.notes.dbo.UserEntity;
import by.demianbel.notes.dto.NotesUserDetails;
import by.demianbel.notes.repository.UserRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@RunWith(SpringRunner.class)
public class NotesUserDetailsServiceTest {

    public static final String USER_NAME = "name";
    public static final String USER_EMAIL = "user email";
    public static final String USER_PASSWORD = "user password";

    @TestConfiguration
    static class NotesUserDetailsServiceTestConfiguration {
        @Bean
        public NotesUserDetailsService notesUserDetailsService(final UserRepository userRepository) {
            return new NotesUserDetailsService(userRepository);
        }

        @Bean
        public PersistedTagTagEntityConverter persistedTagTagEntityConverter() {
            return new PersistedTagTagEntityConverter();
        }
    }

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private NotesUserDetailsService notesUserDetailsService;

    @Test
    public void loadUserByUsername() {

        Mockito.when(userRepository.findByName(USER_NAME)).thenReturn(Optional.of(createUser()));

        final UserDetails userDetails = notesUserDetailsService.loadUserByUsername(USER_NAME);
        Assert.assertNotNull(userDetails);
        Assert.assertEquals(USER_NAME, userDetails.getUsername());
        Assert.assertEquals(USER_PASSWORD, userDetails.getPassword());
        Assert.assertTrue(userDetails.isEnabled());
        Assert.assertTrue(userDetails instanceof NotesUserDetails);

        final NotesUserDetails castUserDetails = (NotesUserDetails) userDetails;
        final UserEntity user = castUserDetails.getUser();

        Assert.assertNotNull(user);
        Assert.assertEquals(1L, user.getId());
    }

    private UserEntity createUser() {
        final UserEntity user = new UserEntity();
        user.setName(USER_NAME);
        user.setEmail(USER_EMAIL);
        user.setPassword(USER_PASSWORD);
        user.setActive(true);
        user.setId(1L);
        return user;
    }
}