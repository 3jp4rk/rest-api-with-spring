package me.ejpark.demoinflearnrestapi.accounts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {

    @Override
    // null return할 수 있으므로 optional로 감쌈
    Optional<Account> findById(Integer integer);

    Optional<Account> findByEmail(String username);
}
