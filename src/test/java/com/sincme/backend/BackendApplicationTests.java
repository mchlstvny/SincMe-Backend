package com.sincme.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import com.sincme.backend.config.TestConfig;

@SpringBootTest(properties = {
    "spring.config.location=classpath:application.properties",
    "spring.profiles.active=test"
})
@Import(TestConfig.class)
class BackendApplicationTests {

    @Test
    void contextLoads() {
        // This test verifies that the Spring application context can load successfully
    }

}
