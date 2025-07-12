package by.innowise.internship.userService.core.util.validation;

import by.innowise.internship.userService.core.exception.UpdateDtoVersionOutdatedException;
import by.innowise.internship.userService.core.util.api.Versionable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ValidationUtil {

    public <V, E extends Versionable> void checkIfDtoVersionIsOutdated(V entityVersion, E dto) {
        if (!Objects.equals(entityVersion, dto.getVersion())) {
            throw new UpdateDtoVersionOutdatedException(String.format("The provided dto version is outdated [%s]", dto),
                                                        HttpStatus.CONFLICT);
        }
    }

}
