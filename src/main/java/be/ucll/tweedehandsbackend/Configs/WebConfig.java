package be.ucll.tweedehandsbackend.Configs;

import be.ucll.tweedehandsbackend.DTOs.CourseDTO;
import be.ucll.tweedehandsbackend.DTOs.OfferDTO;
import be.ucll.tweedehandsbackend.DTOs.ProgramDTO;
import be.ucll.tweedehandsbackend.DTOs.UserDTO;
import be.ucll.tweedehandsbackend.Models.Course;
import be.ucll.tweedehandsbackend.Services.UserService;
import be.ucll.tweedehandsbackend.Util.CustomAuthenticationFailureHandler;
import be.ucll.tweedehandsbackend.Util.CustomAuthenticationSuccessHandler;
import be.ucll.tweedehandsbackend.Util.JWT.AuthEntryPointJwt;
import be.ucll.tweedehandsbackend.Util.JWT.AuthTokenFilter;
import be.ucll.tweedehandsbackend.Util.UCLLOAuthRequestEntityConverter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class WebConfig {

    @EnableWebSecurity
    public static class WebSecurityConfig extends WebSecurityConfigurerAdapter {

        @Autowired
        UserService userDetailsService;

        @Autowired
        private AuthEntryPointJwt unauthorizedHandler;

        @Bean
        public AuthTokenFilter authenticationJwtTokenFilter() {
            return new AuthTokenFilter();
        }

        @Override
        public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
            authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        }

        @Bean
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new Argon2PasswordEncoder();
        }

        @Bean
        public AuthenticationFailureHandler authenticationFailureHandler() {
            return new CustomAuthenticationFailureHandler();
        }

        @Bean
        public AuthenticationSuccessHandler authenticationSuccessHandler() {
            return new CustomAuthenticationSuccessHandler();
        }

        /*
          A custom request entity converter was needed because the default one did not correctly create the Authentication
          header for the request. The ClientId and ClientSecret were URL Encoded which UCLL does not support.
         */
        @Bean
        public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient() {
            DefaultAuthorizationCodeTokenResponseClient accessTokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();
            accessTokenResponseClient.setRequestEntityConverter(new UCLLOAuthRequestEntityConverter());

            return accessTokenResponseClient;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().disable()
                    .headers().frameOptions().sameOrigin()
                    .and()

                    .formLogin().loginPage("/auth/login").defaultSuccessUrl("/").and()
                    .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()


                    .authorizeRequests((authorizeRequests) ->
                            authorizeRequests
                                    .antMatchers(("/storage/uploads/**")).permitAll()
                                    .antMatchers("/api/oauth2/callback/**", "/api/oauth2/provider/*", "/api/auth/login", "/api/auth/error", "/api/health").permitAll()
                                    .antMatchers("/auth/**").permitAll()
                                    .antMatchers("/api/auth/**").permitAll()
                                    .antMatchers(HttpMethod.POST, "/api/users/").permitAll()
                                    .antMatchers("/api/test/**").permitAll()
                                    .antMatchers(HttpMethod.GET, "/api/offers/**").permitAll()
                                    .antMatchers(HttpMethod.GET, "/api/courses/**").permitAll()
                                    .anyRequest().authenticated()
                    )

                    .oauth2Client()
                    .and()
                    .oauth2Login()
                    .loginPage("/api/auth/login")
                    .redirectionEndpoint(redirectionEndpointConfig -> {
                        redirectionEndpointConfig.baseUri("/api/oauth2/callback/*");
                    })
                    .authorizationEndpoint(authorizationEndpointConfig -> {
                        authorizationEndpointConfig.baseUri("/api/oauth2/provider");
                    })
                    .tokenEndpoint(tokenEndpoint ->
                            tokenEndpoint.accessTokenResponseClient(this.accessTokenResponseClient())
                    )
                    .successHandler(authenticationSuccessHandler())
                    //.defaultSuccessUrl("/api/auth/success")
                    .failureHandler(authenticationFailureHandler());
            http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        }

    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService(ClientRegistrationRepository clientRegistrationRepository) {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
    }

    @Bean
    public OAuth2AuthorizedClientRepository authorizedClientRepository(OAuth2AuthorizedClientService authorizedClientService) {
        return new AuthenticatedPrincipalOAuth2AuthorizedClientRepository(authorizedClientService);
    }

    @Bean
    public InMemoryClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(this.ucllClientRegistration());
    }

    /**
     * This is a custom client registration for the UCLL OAuth2 provider.
     *
     * @return ClientRegistration
     */
    private ClientRegistration ucllClientRegistration() {
        return ClientRegistration
                .withRegistrationId("ucll")
                .clientId(this.clientId)
                .clientSecret(this.clientSecret)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/api/oauth2/callback/{registrationId}")
                .authorizationUri(this.authorizationUri)
                .tokenUri(this.tokenUri)
                .userInfoUri(this.userInfoUri)
                .userNameAttributeName(this.userNameAttribute)
                .clientName("UCLL")
                .build();
    }

    @Value("${oauth2.client-id}")
    private String clientId;

    @Value("${oauth2.client-secret}")
    private String clientSecret;

    @Value("${oauth2.token-uri}")
    private String tokenUri;

    @Value("${oauth2.user-info-uri}")
    private String userInfoUri;

    @Value("${oauth2.authorization-uri}")
    private String authorizationUri;

    @Value("${oauth2.user-name-attribute}")
    private String userNameAttribute;


}
