package com.example.pisioconf_backend.security;

import com.example.pisioconf_backend.security.models.AuthorizationRules;
import com.example.pisioconf_backend.security.models.Rule;
import com.example.pisioconf_backend.services.JwtUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;


@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    private final AuthorizationFilter authorizationFilter;
    private final JwtUserDetailsService jwtUserDetailsService;

    public WebSecurityConfig(AuthorizationFilter authorizationFilter, JwtUserDetailsService jwtUserDetailsService) {
        this.authorizationFilter = authorizationFilter;
        this.jwtUserDetailsService = jwtUserDetailsService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(jwtUserDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http = http.cors().and().csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and();
        http = createAuthorizationRules(http);
        http.addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class);
    }

  @Override
  @Bean
  public AuthenticationManager authenticationManagerBean() throws Exception
  {
      return super.authenticationManagerBean();
  }

    private HttpSecurity createAuthorizationRules(HttpSecurity http) throws Exception {

        AuthorizationRules authorizationRules = new ObjectMapper().readValue(new ClassPathResource("rules.json").getInputStream(), AuthorizationRules.class);
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry interceptor = http.authorizeRequests();
        interceptor = interceptor.antMatchers(HttpMethod.POST, "/login").permitAll().antMatchers(HttpMethod.POST, "/sign-up").permitAll();
        for (Rule rule : authorizationRules.getRules()) {
            if (rule.getMethods().isEmpty())
                interceptor = interceptor.antMatchers(rule.getPattern()).hasAnyAuthority(rule.getRoles().toArray(String[]::new));
            else for (String method : rule.getMethods()) {
                interceptor = interceptor.antMatchers(HttpMethod.resolve(method), rule.getPattern()).hasAnyAuthority(rule.getRoles().toArray(String[]::new));
            }
        }
        return interceptor.anyRequest().denyAll().and();
    }


//    @Bean
//    public CorsFilter corsFilter() {
//        UrlBasedCorsConfigurationSource source =
//                new UrlBasedCorsConfigurationSource();
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowCredentials(true);
//        config.addAllowedOrigin("*");
//        config.addAllowedHeader("*");
//        config.addAllowedMethod("*");
//        config.addAllowedMethod("PATCH");
//        source.registerCorsConfiguration("/**", config);
//        return new CorsFilter(source);
//    }
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.addAllowedOrigin("http://localhost:3000");
    configuration.addAllowedHeader("*");
    configuration.addAllowedMethod("*");

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/v2/api-docs",
                "/configuration/ui",
                "/swagger-resources/**",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**");
    }

    @Bean
    GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults(""); // Remove the ROLE_ prefix
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}