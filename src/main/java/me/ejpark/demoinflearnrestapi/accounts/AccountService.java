package me.ejpark.demoinflearnrestapi.accounts;

import me.ejpark.demoinflearnrestapi.events.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service // 이거 안 붙이면 못 찾아서 test에서 could not autowire error 남...
public class AccountService implements UserDetailsService {

    @Autowired
    AccountRepository accountRepository;

    @Autowired // password encoding
    PasswordEncoder passwordEncoder;

    // account 저장
    public Account saveAccount(Account account) {
        account.setPassword(this.passwordEncoder.encode(account.getPassword()));
        return this.accountRepository.save(account);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(username)); // 이 username에 해당하는 user가 없다고 에러 메시지 출력됨
//        return (UserDetails) account; // UserDetails type으로 변환
        // 해야 하는데... userDetails type에 보면 User라는 class가 있음! 그 User class 사용하면 전체 인터페이스 다 구현하지 않아도 됨

//        return new User(account.getEmail(), account.getPassword(), authorities(account.getRoles())); // 이렇게 입력 후 create method 수행
        return new AccountAdapter(account); // 이렇게 입력 후 create method 수행

    }

    private Collection<? extends GrantedAuthority> authorities(Set<AccountRole> roles) {

//        return roles.stream().map(r -> {
//            return new SimpleGrantedAuthority("ROLE_" + r.name());
//        }).collect(Collectors.toSet());

        // 한줄로 간략하게 하면 안쪽 return 생략 가능
        // map 후 collect 해서 ROLE_ prefix 붙여 주고 grandauthority로 변환
        // 읽어 와서 spring security가 이해할 수 있도록 변환함
        return roles.stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r.name())).collect(Collectors.toSet());
    }



}
