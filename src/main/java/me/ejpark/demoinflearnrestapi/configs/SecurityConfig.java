package me.ejpark.demoinflearnrestapi.configs;

import me.ejpark.demoinflearnrestapi.accounts.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter { // 이걸 상속받는 순간 springboot가 제공하는 spring security 설정은 더 이상 적용되지 않음!

    @Autowired
    AccountService accountService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Bean // OAuth token 저장소 (In-Memory token store 사용)
    public TokenStore tokenStore() {
        return new InMemoryTokenStore();
    }

    @Bean // 이게 있어야 bean으로 노출돼서 다른데서도 참조함
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(accountService)
                .passwordEncoder(passwordEncoder); // 내가 등록한 service랑 encoder 등록해서 만들어 달라고 요청
    }

    // http로 가기 전에 web security로 무시할지 말지 결정 (filter)
    // web에서 리소스 걸어
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().mvcMatchers("/docs/index.html"); // 여기로 들어오는 건 다 무시
        // 리소스 서버 설정 편 듣고 나야 ignoring이 제대로 됨...
        // favicon 등 정적 리소스 다 무시 (web에서 필터링해줘야 서버가 하는 일이 덜해짐)
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations()); // 정적 리소스들의 default 위치에 대해서는 적용 X

    }

    // 익명 사용자 허용 등 스프링 시큐리티 설정 가능
    //
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .anonymous()
                    .and()
                .formLogin()
                    .and()
                .authorizeRequests()
                    .mvcMatchers(HttpMethod.GET, "/api/**").authenticated() // 이걸 anonymous로 하면 인증요청 필터링에 안 걸림. authenticated로 바꾼다
                    // 앱 뜰 때 등록한 로그인 계정으로 로그인하면 됨
                    .anyRequest().authenticated();

                // 난 GET으로 들어오는 http 요청은 익명 요청 허용할거고, 나머지는 인증이 필요하다
        }


}
