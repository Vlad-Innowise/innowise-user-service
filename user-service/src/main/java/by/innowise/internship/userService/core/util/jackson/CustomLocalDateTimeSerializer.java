package by.innowise.internship.userService.core.util.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class CustomLocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {

    private final DateTimeFormatter formatter;

    public CustomLocalDateTimeSerializer(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public void serialize(LocalDateTime date, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (date != null) {
            String formattedDate = date.format(formatter);
            log.info("Serialized a LocalDateTime [{}] to JSON [{}]", date, formattedDate);
            gen.writeString(formattedDate);
        }
    }
}
