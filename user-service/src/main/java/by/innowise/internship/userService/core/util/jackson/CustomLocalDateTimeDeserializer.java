package by.innowise.internship.userService.core.util.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class CustomLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    private final DateTimeFormatter formatter;

    public CustomLocalDateTimeDeserializer(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        String dateRaw = jsonParser.getText();
        LocalDateTime formattedDate = LocalDateTime.parse(jsonParser.getText(), formatter);
        log.info("Deserialized a text [{}] to LocalDateTime [{}]", dateRaw, formattedDate);
        return formattedDate;
    }
}
