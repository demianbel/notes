package by.demianbel.notes.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityJavaConfig extends WebSecurityConfigurerAdapter {


    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    private final MySavedRequestAwareAuthenticationSuccessHandler authenticationSuccessHandler;

    private final NotesUserDetailsService notesUserDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth)
            throws Exception {
        // TODO change encoder to not deprecated
        auth.userDetailsService(notesUserDetailsService).passwordEncoder(NoOpPasswordEncoder.getInstance());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .exceptionHandling()
                .and()
                .authorizeRequests()
//                .antMatchers("/swagger-ui.html").permitAll()
//                .antMatchers("/v2/api-docs/**").permitAll()
//                .antMatchers("/configuration/ui").permitAll()
//                .antMatchers("/swagger-resources").permitAll()
//                .antMatchers("/configuration/security").permitAll()
//                .antMatchers("/v2/api-docs/**").permitAll()
                .antMatchers("/**")
//                .permitAll()
                .hasAnyAuthority("admin", "user")
                .and()
                .formLogin()
                .and()
//                .httpBasic()
//                .and()
                .logout();
    }

    @Bean
    public MySavedRequestAwareAuthenticationSuccessHandler mySuccessHandler() {
        return new MySavedRequestAwareAuthenticationSuccessHandler();
    }

    @Bean
    public SimpleUrlAuthenticationFailureHandler myFailureHandler() {
        return new SimpleUrlAuthenticationFailureHandler();
    }
}
