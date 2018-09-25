package by.demianbel.notes.controller;

import by.demianbel.notes.dto.user.PersistedUserDTO;
import by.demianbel.notes.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@RunWith(SpringRunner.class)
@WebMvcTest(AccountController.class)
public class AccountControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AccountService service;

    @Test
    public void signUp() throws Exception {
        // TODO add authorization
        final PersistedUserDTO user = new PersistedUserDTO();
        user.setName("name");
        user.setEmail("email");
        Mockito.when(service.signUp(Mockito.any())).thenReturn(user);
        mvc.perform(MockMvcRequestBuilders.post("notes/rest/account/signup").contentType(
                MediaType.APPLICATION_JSON_UTF8_VALUE).content(asJsonString(user)))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Is.is("name")))
                .andExpect(MockMvcResultMatchers.status().isForbidden());

    }

    @Test
    public void restore() {
        // TODO write test
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}