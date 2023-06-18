package mk.ukim.finki.wp.sep2022.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 *  This class is used to configure user login on path '/login' and logout on path '/logout'.
 *  The only public page in the application should be '/'.
 *  All other pages should be visible only for a user with role 'ROLE_ADMIN'.
 *  Furthermore, in the "list.html" template, the 'Edit', 'Delete', 'Add' buttons should only be
 *  visible for a user with role 'ROLE_ADMIN'.
 *  The 'Follow Match' button should only be visible for a user with role 'ROLE_USER'.
 *
 *  For login inMemory users should be used. Their credentials are given below:
 *  [{
 *      username: "user",
 *      password: "user",
 *      role: "ROLE_USER"
 *  },
 *
 *  {
 *      username: "admin",
 *      password: "admin",
 *      role: "ROLE_ADMIN"
 *  }]
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected  void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("user")
                .password(passwordEncoder.encode("user"))
                .authorities("ROLE_USER")
                .and()
                .withUser("admin")
                .password(passwordEncoder.encode("admin"))
                .authorities("ROLE_ADMIN");
    }
    @Override
    public void configure(WebSecurity web) throws Exception {
        // TODO: If you are implementing the security requirements, remove this following line
       // web.ignoring().antMatchers("/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception{

        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/", "/matches").permitAll()
                .anyRequest().hasAnyRole("ADMIN","USER")
                .and()
                .formLogin()
                .failureUrl("/login?error=BadCredentials")
                .defaultSuccessUrl("/matches",true)
                .and()
                .logout()
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl("/")
                .and()
                .exceptionHandling().accessDeniedPage("/");

    }
}
