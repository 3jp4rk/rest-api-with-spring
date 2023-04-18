package me.ejpark.demoinflearnrestapi.configs;

import me.ejpark.demoinflearnrestapi.accounts.Account;
import me.ejpark.demoinflearnrestapi.accounts.AccountRole;
import me.ejpark.demoinflearnrestapi.accounts.AccountService;
import me.ejpark.demoinflearnrestapi.common.AppProperties;
import me.ejpark.demoinflearnrestapi.common.BaseControllerTest;
import me.ejpark.demoinflearnrestapi.common.TestDescription;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.Set;

import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthServerConfigTest extends BaseControllerTest {

    @Autowired
    AccountService accountService;

    @Autowired
    AppProperties appProperties;

    @Test
    @TestDescription("인증 토큰을 발급받는 테스트")
    public void getAuthToken() throws Exception {

        // appProperties 활용
//        String username = "ejpark@email.com"; // ejpark@fasoo.com 이 이미 있나? 그래서 unique result가 아니라고 error가 남...
//        String password = "ejpark";
        // Given (user 필요하므로 생성)

        // runner가 app run할 떄 저장함
//        Account ejpark = Account.builder()
//                .email(appProperties.getUserUsername())
//                .password(appProperties.getUserPassword())
//                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
//                .build();
//        this.accountService.saveAccount(ejpark);

//        String clientId = "myApp";
//        String clientSecret = "pass";

        // appProperties 활용
//        String clientId = appProperties.getClientId();
//        String clientSecret = appProperties.getClientSecret();

        // grant type이 중요하다
        // password라는 grand type으로 받을 것임
        // hop이 한 번!
        // 인증을 제공하는 그 서비스들이 만든 앱. (서드파티 X)
        // 인증 정보를 보유하고 있는 앱에서만 제공해야 하는 서비스
        this.mockMvc.perform(MockMvcRequestBuilders.post("/oauth/token")
                .with(httpBasic(appProperties.getClientId(), appProperties.getClientSecret()))
                .param("username", appProperties.getUserUsername())
                .param("password", appProperties.getUserPassword())
                .param("grant_type", "password"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token").exists());
    }

}