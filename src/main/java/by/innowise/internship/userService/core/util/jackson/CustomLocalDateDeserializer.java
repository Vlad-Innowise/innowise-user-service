package by.innowise.internship.userService.core.util.jackson;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
public class CustomLocalDateDeserializer extends JsonDeserializer<LocalDate> {

    private final DateTimeFormatter formatter;

    public CustomLocalDateDeserializer(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public LocalDate deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException, JacksonException {
        String dateRaw = parser.getText();
        LocalDate formattedDate = LocalDate.parse(dateRaw, formatter);
        log.info("Deserialized a text [{}] to LocalDate [{}]", dateRaw, formattedDate);
        return formattedDate;
    }
}
