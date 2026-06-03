package com.desafioTecnico.bff;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(properties = {
        "backend.url=http://localhost:9999",
        "frontend.origin=http://localhost:5173"
})
class BffApplicationTest {

    @Test
    void contextLoads() {
        // verifies that the Spring context starts successfully
    }

    @Test
    void main_runsWithoutException() {
        BffApplication.main(new String[]{
                "--server.port=0",
                "--backend.url=http://localhost:9999",
                "--frontend.origin=http://localhost:5173"
        });
    }
}
