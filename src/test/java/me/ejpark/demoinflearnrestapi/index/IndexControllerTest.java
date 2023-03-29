package me.ejpark.demoinflearnrestapi.index;

import me.ejpark.demoinflearnrestapi.common.RestDocConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest // webenvironment param = 기본값: mock. 계속 mockmvc 테스트 사용 가능.
@AutoConfigureMockMvc // SpringBootTest랑 같이 쓰려면
// mocking할 게 많으면 테스트코드 짜기 어렵고 관리도 어려움. springbootTest 사용.
@AutoConfigureRestDocs // rest docs
@Import(RestDocConfiguration.class) // bean 설정 import해서 사용
@ActiveProfiles("test") // test 밑의 application-test.properties도 사용! 그러므로 test는 계속 h2를 사용하게 된다.
public class IndexControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    public void index() throws Exception {
        // event resource에 대한 root가 나오길 바람
        this.mockMvc.perform(get("/api/")) // /api/에 요청을 보냈을 때
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.events").exists());
    }
}
