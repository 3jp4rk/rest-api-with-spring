package me.ejpark.demoinflearnrestapi.configs;

import me.ejpark.demoinflearnrestapi.accounts.Account;
import me.ejpark.demoinflearnrestapi.accounts.AccountRole;
import me.ejpark.demoinflearnrestapi.accounts.AccountService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class AppConfig {


    // ModelMapper (공용) Bean 등록
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder(); // 다양한 encoding type을 지원하는 password. prefix를 붙여서 어떤 방식으로 encoding된 건지 알려줌
    }

    // 앱 구동될 때 user 만들어 줌
    @Bean
    public ApplicationRunner applicationRunner() {
        return new ApplicationRunner() {

            @Autowired
            AccountService accountService;
            @Override
            public void run(ApplicationArguments args) throws Exception {
                Account ejpark = Account.builder()
                        .email("ejpark@fasoo.com")
                        .password("ejpark")
                        .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                        .build();
                accountService.saveAccount(ejpark);
            }
        };
    }
}
