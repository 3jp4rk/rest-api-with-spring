package me.ejpark.demoinflearnrestapi.accounts;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;


@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test") // service test이므로 base controller test 상속받을 필요 X, mock 사용 X
public class AccountServiceTest {


    @Rule
    public ExpectedException expectedException = ExpectedException.none(); // 빈 exception으로 등록



    @Autowired
    AccountService accountService;

    // 저장하기 위해서 accountRepository 필요
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    public void findByUserName() {

        // Given
        String password = "ejpark";
        String username = "ejpark@fasoo.com";
        Account account = Account.builder()
                        .email(username)
                        .password(password)
                        .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                        .build();

        // save
//        this.accountRepository.save(account);

        // service 사용해서 저장
        this.accountService.saveAccount(account);
        // When
        UserDetailsService userDetailsService = accountService;
        UserDetails userDetails = userDetailsService.loadUserByUsername(username); // 이 user를 읽어올 수 있는지.
        // 읽어 왔다면 type은 UserDetailsService일 것

        // Then

//        assertThat(userDetails.getPassword()).isEqualTo(password);
        assertThat(this.passwordEncoder.matches(password, userDetails.getPassword())).isTrue(); // 함수 기억 안 나면 Ctrl + B 해서 download sources 하고 parameter 확인해 보기


    }
//
//    @Test(expected = UsernameNotFoundException.class) // 예외 타입만 확인하게 됨
//    public void findByUsernameFail() {
//        String username = "random@email.com";
//
//        try {
//            accountService.loadUserByUsername(username);
//            // test를 실패하게 만들기
//            fail("supposed to be failed");
//        } catch (UsernameNotFoundException e) {
////            assertThat(e instanceof UsernameNotFoundException).isTrue();
//            assertThat(e.getMessage()).containsSequence(username); // 이 에러 메시지에 username이 담겨 있는지 확인 가능함
//        }
//    }

    // @Rule 활용한 test
    @Test
    public void findByUsernameFail() {

        // Expected
        String username = "random@email.com";
        // 어떤 예외가 발생하길 바라는지 먼저 적어 줘야 함
        // 여기서 예측한 것과 다르면 테스트 실패하게 됨
        // 예측이므로 이걸 When 뒤에 Then으로 쓰면 동작 안 함 ㅎㅎ... (예외 발생해서 실패함) 위에 꼭 먼저 적어 줘야 함
        // thrown.expect 이것도 적절하지 않아 보임
        expectedException.expect(UsernameNotFoundException.class);
        expectedException.expectMessage(Matchers.containsString(username));

        // When
        accountService.loadUserByUsername(username);

    }

}