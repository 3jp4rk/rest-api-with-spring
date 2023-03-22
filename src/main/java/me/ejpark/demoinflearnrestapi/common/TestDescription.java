package me.ejpark.demoinflearnrestapi.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE) // 컴파일에도 유지해야 할까?.. 그냥 source로
// 그냥 interface 쓰면 위에 annotation 에러 남. @interface로 써야 한다.
public @interface TestDescription {
    String value(); // 기본값 설정할 수도 있지만 일단은 그냥 무조건 값 입력하도록 설정.
}
