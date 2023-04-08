package me.ejpark.demoinflearnrestapi.accounts;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @NoArgsConstructor @AllArgsConstructor
public class Account { // Event와 연관관계

    @Id @GeneratedValue
    private Integer id;
    private String email;
    private String password;

    // 이 위의 두 개 annotation 안 달면 에러 난다... basic type couldn't be a container
    @ElementCollection(fetch = FetchType.EAGER) // 기본: lazy fetch... 이번엔 적으니까 eager mode
    @Enumerated(value = EnumType.STRING)
    private Set<AccountRole> roles;
}
