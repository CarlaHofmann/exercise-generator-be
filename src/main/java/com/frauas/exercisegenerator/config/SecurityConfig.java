package com.frauas.exercisegenerator.config;

import com.frauas.exercisegenerator.filter.CustomAuthenticationFilter;
import com.frauas.exercisegenerator.filter.CustomAuthorizationFilter;
import com.frauas.exercisegenerator.util.TokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter
{
	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private TokenUtil tokenUtil;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception
	{
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception
	{
		http.csrf().disable();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.authorizeRequests().antMatchers(GET, "/users").hasAnyAuthority("admin");
		http.authorizeRequests().antMatchers(POST, "/users").hasAnyAuthority("admin");
		http.authorizeRequests().antMatchers(GET, "/users/refreshToken").permitAll();
		http.authorizeRequests().antMatchers(POST, "/sheet").hasAnyAuthority("simpleUser");
		http.authorizeRequests().antMatchers(DELETE, "/sheet").hasAnyAuthority("simpleUser");
		http.authorizeRequests().antMatchers(POST, "/exercise").hasAnyAuthority("simpleUser");
		http.authorizeRequests().antMatchers(DELETE, "/exercise").hasAnyAuthority("simpleUser");
		//http.authorizeRequests().antMatchers(GET, "/*").hasAnyAuthority("simpleUser");
		//http.authorizeRequests().anyRequest().permitAll();
		//http.authorizeRequests().antMatchers(GET, "/*").authenticated();
		http.authorizeRequests().antMatchers(GET, "/v3/*").permitAll();
		http.authorizeRequests().antMatchers(GET, "/swagger-ui/*").permitAll();
		http.cors();
		http.addFilter(new CustomAuthenticationFilter(tokenUtil, authenticationManagerBean()));
		http.addFilterBefore(new CustomAuthorizationFilter(tokenUtil), UsernamePasswordAuthenticationFilter.class);
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception
	{
		return super.authenticationManagerBean();
	}
}
