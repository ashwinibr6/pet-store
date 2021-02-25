package com.petstore.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    Environment env;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers(HttpMethod.GET,"/home")
                .permitAll()
                .antMatchers(HttpMethod.GET,"/animals")
                .permitAll()
                .antMatchers(HttpMethod.POST,"/adopt")
                .permitAll()
                .antMatchers("/h2-console/**")
                .permitAll()
                .antMatchers(HttpMethod.GET,"/animal/{shelternateID}")
                .permitAll()
                .antMatchers(HttpMethod.GET,"/items/**")
                .permitAll()
                .antMatchers(HttpMethod.PATCH,"/storeCatalog/purchaseItem/credit/")
                .permitAll()
                .anyRequest().authenticated().and()
                .httpBasic().and().csrf().disable();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        String encodedPassword = passwordEncoder().encode(env.getProperty("user.security.password"));
        manager.createUser(User.withUsername(env.getProperty("user.security.username")).password(encodedPassword)
                .roles("USER").build());

        return manager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
       // return NoOpPasswordEncoder.getInstance();
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {

        web.ignoring()
                .antMatchers("/h2-console/**");
    }


}
