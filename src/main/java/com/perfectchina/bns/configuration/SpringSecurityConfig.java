package com.perfectchina.bns.configuration;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import com.perfectchina.bns.model.Role;
import com.perfectchina.bns.model.User;
import com.perfectchina.bns.service.UserService;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
	
	private static Logger logger = LogManager.getLogger( SpringSecurityConfig.class );
	
	public static final String LOGIN = "/";
	
	@Resource(name = "userDetailService")
	private UserDetailsService userDetailsService;

	// [Start] For creating default user login 
	@Autowired
	UserService userService; //Service which will do all data retrieval/manipulation work
	// [End] For creating default user login 
	
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }	
	
    // From form login sample
	//@Override
	//protected void configure(HttpSecurity http) throws Exception {
	//	http.csrf().requireCsrfProtectionMatcher(new AntPathRequestMatcher("**/login")).and().authorizeRequests()
	//			.antMatchers("/dashboard").hasRole("USER").and().formLogin().defaultSuccessUrl("/dashboard")
	//			.loginPage("/login").and().logout().permitAll();
	//}


    @Override
    protected void configure(HttpSecurity http) throws Exception {
		logger.info("Inside HttpSecurity.");
		
		  /*
	      http
	        .httpBasic()
	      .and()
	        .authorizeRequests()
	          .antMatchers("/", "/auth/**").permitAll()  // all api/user not works ?
	          .antMatchers("/api/**").hasAnyRole("ADMIN")
	          .anyRequest().authenticated()
          .and()
          	.formLogin().loginPage(LOGIN)
	      .and()
	      	.logout().permitAll()
	      .and()
	        .csrf().csrfTokenRepository(csrfTokenRepository())
          .and()
	        .addFilterAfter(new CsrfHeaderFilter(), CsrfFilter.class)
	      	.exceptionHandling().authenticationEntryPoint(new AjaxAwareEntryPoint(LOGIN));
		*/
	      
		/*
        http
                .authorizeRequests()
                    .antMatchers("/resources/**", "/register").permitAll()
                    // .antMatchers("/**").permitAll()
                    .anyRequest().authenticated()
                    .and()
                .formLogin()
                    .loginPage("/login")
                    .permitAll()
                    .and()
                .logout()
                    .permitAll();
        */
	      
//        http.exceptionHandling().authenticationEntryPoint(new AjaxAwareEntryPoint(LOGIN))
//        .and()
//        .authorizeRequests()
//        .antMatchers("/", "/auth/**", "/api/**", "/console/**").permitAll()
//        // .antMatchers("/", "/auth/**" ).permitAll()  // all api/user not works ?
//        .anyRequest().authenticated()
//        .and()
//        .formLogin().loginPage(LOGIN)
//        .and()
//        .logout().permitAll()
//        .and()
//        .csrf().csrfTokenRepository(csrfTokenRepository())
//        .and()
//        .addFilterAfter(new CsrfHeaderFilter(), CsrfFilter.class);

	      http.csrf().disable(); // this one only used to debug , should be remove in production
	      http.headers().frameOptions().disable(); // this one only used to debug , should be remove in production
		
	      
        if ("true".equals(System.getProperty("httpsOnly"))) {
            logger.info("launching the application in HTTPS-only mode");
            http.requiresChannel().anyRequest().requiresSecure();
        }        
        
    }

    // Method to create default user at start up
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
    	
    	User user = new User();
    	user.setUsername( "admin" );
    	user.setPassword( "admin" );
    	
    	Set<Role> roles = new HashSet<Role>();
    	Role role = new Role();
    	role.setName("ADMIN"); 
    	roles.add( role );
    	user.setRoles( roles );    	
		userService.saveUser(user);

        //auth.inMemoryAuthentication()
        //       .withUser("admin").password( adminPasswd ).roles("ADMIN", "USER")
        //        .and()
        //        .withUser("user").password( userPasswd ) .roles("USER");
    }
    
    
    private CsrfTokenRepository csrfTokenRepository() {
        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        repository.setHeaderName("X-XSRF-TOKEN");
        return repository;
    }
    
    
    
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
    }	

	@Override
	public void configure(WebSecurity web) throws Exception {
		logger.info("Inside WebSecurity.");
		web.ignoring().antMatchers("/css/**");
		web.ignoring().antMatchers("/js/**");
	}
    
    
    class AjaxAwareEntryPoint extends LoginUrlAuthenticationEntryPoint {

        private static final String XML_HTTP_REQUEST = "XMLHttpRequest";
        private static final String X_REQUESTED_WITH = "X-Requested-With";

        public AjaxAwareEntryPoint(String loginFormUrl) {
            super(loginFormUrl);
        }

        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
                throws IOException, ServletException {
            if (XML_HTTP_REQUEST.equals(request.getHeader(X_REQUESTED_WITH))) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            } else {
                super.commence(request, response, exception);
            }
        }
    }

    class CsrfHeaderFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(HttpServletRequest request,
                                        HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {
            CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
            if (csrf != null) {
                Cookie cookie = WebUtils.getCookie(request, "XSRF-TOKEN");
                String token = csrf.getToken();
                if (cookie==null || token!=null && !token.equals(cookie.getValue())) {
                    cookie = new Cookie("XSRF-TOKEN", token);
                    // logger.debug( "doFilterInternal, contextPath="+ request.getContextPath() );
                    cookie.setPath( request.getContextPath() );
                    response.addCookie(cookie);
                }
            }
            filterChain.doFilter(request, response);
        }
    }
    
    
}
