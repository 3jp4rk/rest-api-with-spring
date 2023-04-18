package me.ejpark.demoinflearnrestapi.configs;

import me.ejpark.demoinflearnrestapi.accounts.Account;
import me.ejpark.demoinflearnrestapi.accounts.AccountRepository;
import me.ejpark.demoinflearnrestapi.accounts.AccountRole;
import me.ejpark.demoinflearnrestapi.accounts.AccountService;
import me.ejpark.demoinflearnrestapi.common.AppProperties;
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

            // 동일한 email로 같은 유저가 계속 생기면 안 됨 -> 동일한 이메일로 저장이 안되도록 해야 됨
            // create-drop option 쓰고 있으므로 괜찮을 듯? (애플리케이션 종료될 떄마다 삭제됨)
            // demo run 하고 db 가서 \dt 하면 테이블 뜬다
            @Autowired
            AccountService accountService;



            @Autowired
            AppProperties appProperties;

            // 이렇게 하드코딩할 필요 없음... (AppProperties 사용)
//            @Override
//            public void run(ApplicationArguments args) throws Exception {
//
//                Account admin = Account.builder()
//                        .email("admin@fasoo.com")
//                        .password("admin")
//                        .roles(Set.of(AccountRole.ADMIN))
//                        .build();
//                accountService.saveAccount(admin);
//
//                Account user = Account.builder()
//                        .email("user@fasoo.com")
//                        .password("user")
//                        .roles(Set.of(AccountRole.USER))
//                        .build();
//                accountService.saveAccount(user);
//            }
//        };

            // 기본 계정 설정 정보 -> application.properties
            @Override
            public void run(ApplicationArguments args) throws Exception {

                Account admin = Account.builder()
                        .email(appProperties.getAdminUsername())
                        .password(appProperties.getAdminPassword())
                        .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                        .build();
                accountService.saveAccount(admin);

                Account user = Account.builder()
                        .email(appProperties.getUserUsername())
                        .password(appProperties.getUserPassword())
                        .roles(Set.of(AccountRole.USER))
                        .build();
                accountService.saveAccount(user);
            }
        };
    }
}
