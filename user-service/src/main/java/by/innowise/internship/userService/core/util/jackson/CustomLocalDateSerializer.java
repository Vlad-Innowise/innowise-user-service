package by.innowise.internship.userService.core.util.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
public class CustomLocalDateSerializer extends JsonSerializer<LocalDate> {

    private final DateTimeFormatter formatter;

    public CustomLocalDateSerializer(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public void serialize(LocalDate date, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (date != null) {
            String formattedDate = date.format(formatter);
            log.info("Serialized a LocalDate [{}] to JSON [{}]", date, formattedDate);
            gen.writeString(formattedDate);
        }
    }
}
