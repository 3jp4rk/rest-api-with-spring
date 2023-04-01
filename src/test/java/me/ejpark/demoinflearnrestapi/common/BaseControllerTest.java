package me.ejpark.demoinflearnrestapi.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
//@WebMvcTest // 슬라이스 테스트라 웹용 빈만 등록.
@SpringBootTest // webenvironment param = 기본값: mock. 계속 mockmvc 테스트 사용 가능.
@AutoConfigureMockMvc // SpringBootTest랑 같이 쓰려면
// mocking할 게 많으면 테스트코드 짜기 어렵고 관리도 어려움. springbootTest 사용.
@AutoConfigureRestDocs // rest docs
@Import(RestDocConfiguration.class) // bean 설정 import해서 사용
@ActiveProfiles("test") // test 밑의 application-test.properties도 사용! 그러므로 test는 계속 h2를 사용하게 된다.
@Ignore // test를 가지고 있는 class가 아니다. test 실행하지 말 것
public class BaseControllerTest {

    @Autowired
    protected MockMvc mockMvc; // 가짜 디스패처 서블릿 만들어서 응답 보내기 테스트 가능. slicing test
    // 웹 서버 띄우지 않음 -> 빠름. 단위 테스트보다는 느림


    // content-type: json 변환
    @Autowired
    protected ObjectMapper objectMapper;


    @Autowired
    protected ModelMapper modelMapper;

}
