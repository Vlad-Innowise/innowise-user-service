package by.innowise.internship.userService.core.config;

import by.innowise.internship.userService.core.util.jackson.CustomLocalDateDeserializer;
import by.innowise.internship.userService.core.util.jackson.CustomLocalDateSerializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Configuration
public class JacksonConfig {

    public static final DateTimeFormatter LOCAL_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> {

            SimpleModule module = new SimpleModule();

            module.addDeserializer(LocalDate.class, new CustomLocalDateDeserializer(LOCAL_DATE_FORMATTER));
            module.addSerializer(LocalDate.class, new CustomLocalDateSerializer(LOCAL_DATE_FORMATTER));

            builder.modules(module);

            builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        };
    }
}
