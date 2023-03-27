package me.ejpark.demoinflearnrestapi.common;

import com.fasterxml.jackson.core.PrettyPrinter;
import org.springframework.boot.test.autoconfigure.restdocs.RestDocsMockMvcConfigurationCustomizer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentationConfigurer;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

@TestConfiguration
public class RestDocConfiguration {
    @Bean
    public RestDocsMockMvcConfigurationCustomizer restDocsMockMvcConfigurationCustomizer () {
//        return new RestDocsMockMvcConfigurationCustomizer() {
//            @Override
//            public void customize(MockMvcRestDocumentationConfigurer configurer) {
//                configurer.operationPreprocessors()
//                        .withRequestDefaults(prettyPrint())
//                        .withResponseDefaults(prettyPrint());
//            }
//        };

        // 마우스 가져다대고 alt + shift + enter -> lambda shift (IDE한테 맡기기. 처음부터 이렇게 코딩하기 어려움)
        return configurer -> configurer.operationPreprocessors()
                .withRequestDefaults(prettyPrint()) // processor: 다양한 패턴 교체 가능함
                .withResponseDefaults(prettyPrint());
    }
}
