package by.innowise.internship.userService.integration.core;

import by.innowise.internship.userService.integration.config.DbOnlyTestConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = DbOnlyTestConfig.class)
@ActiveProfiles("testDbOnly")
public class IntegrationTestBaseDbOnly extends IntegrationTestBase {
}
