package me.ejpark.demoinflearnrestapi.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        // resource configurer
        // resource id 설정

        // 접근권한이 없는 경우 어떻게 핸들링할지 등등
        resources.resourceId("event");
    }

    // 중요함 (anonymous 허용)
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .anonymous()
                    .and()
                .authorizeRequests()

                    // eventControllertest에서 GET 요청 말고 다 실패함
                    .mvcMatchers(HttpMethod.GET, "/api/**")
                        // .anonymous() // anonymous로 하면 anonymous만 사용할 수 있게 됨 ;;
                        .permitAll()
                    .anyRequest()
                        .authenticated()
                    .and()
                .exceptionHandling()
                    .accessDeniedHandler(new OAuth2AccessDeniedHandler()); // 접근 권한이 없는 경우 이 handler를 사용하겠다
    }
}
