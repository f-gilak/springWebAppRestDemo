package com.fariba.restclient.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class DemoSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.jdbcAuthentication().dataSource(dataSource);
//        User.UserBuilder user = User.withDefaultPasswordEncoder();
//        auth.inMemoryAuthentication()
//                .withUser(User.withUsername("john").password("{noop}fun123").roles("EMPLOYEE"))
//                .withUser(User.withUsername("mary").password("{noop}fun123").roles("MANAGER"))
//                .withUser(User.withUsername("fari").password("{noop}fun123").roles("ADMIN", "MANAGER"));

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/customer").hasRole("EMPLOYEE")
                .antMatchers("/customer/leaders/**").hasRole("MANAGER")
                .antMatchers("/customer/systems/**").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().accessDeniedPage("/access-denied")
                .and()
                .formLogin()
                .loginPage("/showMyLoginPage")
                .loginProcessingUrl("/authenticateTheUser")
                .permitAll()
                .and()
                .logout().permitAll();
    }
}
