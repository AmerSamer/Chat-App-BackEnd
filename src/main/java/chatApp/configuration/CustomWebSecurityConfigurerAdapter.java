package chatApp.configuration;

import chatApp.filter.AuthFilter;
import chatApp.filter.PermissionFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
public class CustomWebSecurityConfigurerAdapter {

    @Autowired
    private AuthFilter authFilter;

    @Autowired
    private PermissionFilter permissionFilter;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("*").authenticated().and().httpBasic().and().csrf().disable();
        http.addFilterAfter(
                authFilter, BasicAuthenticationFilter.class);
        http.addFilterAfter(
                permissionFilter, AuthFilter.class);
        return http.build();
    }

}