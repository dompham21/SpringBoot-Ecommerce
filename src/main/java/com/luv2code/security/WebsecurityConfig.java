package com.luv2code.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;



@Configuration
@EnableWebSecurity
public class WebsecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public UserDetailsService userDetailsService() {
        return new WebUserDetailsService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http.authorizeRequests()
                .antMatchers("/users/**", "/settings/**", "/countries/**", "/states/**").hasAuthority("Admin")
                .antMatchers("/categories/**","/brands/**","/menus/**").hasAnyAuthority("Admin", "Editor")
                .antMatchers("/products/new", "/products/delete/**").hasAnyAuthority("Admin", "Editor")
                .antMatchers("/products/edit/**", "/products/save", "/products/check_unique")
                    .hasAnyAuthority("Admin", "Editor", "Salesperson")
                .antMatchers("/products", "/products/", "/products/detail/**", "/products/page/**")
                    .hasAnyAuthority("Admin", "Editor", "Salesperson", "Shipper")
                .antMatchers("/products/**").hasAnyAuthority("Admin", "Editor")
                .antMatchers("/customers/**","/shipping/**","/articles/**", "/get_shipping_cost").hasAnyAuthority("Admin", "Salesperson")
                .antMatchers("/orders", "/orders/", "/orders/page/**", "/orders/detail/**").hasAnyAuthority("Admin", "Salesperson", "Shipper")
                .antMatchers("/states/list_by_country/**").hasAnyAuthority("Admin", "Salesperson")
                .antMatchers("/orders_shipper/update/**").hasAuthority("Shipper")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                    .usernameParameter("email")
                    .passwordParameter("password")
                    .permitAll()
                .and()
                    .logout()
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .permitAll()
                .and()
                    .rememberMe()
                    .key("AbcDefgKLDSLmvop_0123456789")
                    .tokenValiditySeconds(7 * 24 * 60 * 60); // 7days


    }


    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/images/**", "/js/**", "/webjars/**" , "/css/**");
    }

    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

}
