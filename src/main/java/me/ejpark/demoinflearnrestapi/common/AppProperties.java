package me.ejpark.demoinflearnrestapi.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;

// pom.xml에 properties-processor 추가

@Component
@ConfigurationProperties(prefix = "my-app") // canonical form으로 만들라고 함
@Getter @Setter
public class AppProperties {

    // 이 값들은 전부 외부에서 받아올 수 있게 해야 함 = 바인딩 가능하도록
    @NotEmpty
    private String adminUsername;
    @NotEmpty
    private String adminPassword;

    @NotEmpty
    private String userUsername;

    @NotEmpty
    private String userPassword;

    @NotEmpty
    private String clientId;

    @NotEmpty
    private String clientSecret;
}
