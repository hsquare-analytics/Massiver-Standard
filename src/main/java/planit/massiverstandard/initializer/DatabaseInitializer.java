package planit.massiverstandard.initializer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import planit.massiverstandard.database.DataBase;
import planit.massiverstandard.database.repository.DataBaseRepository;

@Component
@RequiredArgsConstructor
public class DatabaseInitializer {

    private final DataBaseRepository dataBaseRepository;

    public DataBase init(String name, String type, String host, String port, String username, String password, String description) {
        DataBase build = DataBase.builder()
            .name(name)
            .type(type)
            .host(host)
            .port(port)
            .username(username)
            .password(password)
            .description(description)
            .build();

        return dataBaseRepository.save(build);
    }



}
