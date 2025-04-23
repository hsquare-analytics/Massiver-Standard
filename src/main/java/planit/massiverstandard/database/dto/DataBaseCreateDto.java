package planit.massiverstandard.database.dto;

public record DataBaseCreateDto (
        String name,
        String type,
        String host,
        String port,
        String username,
        String password,
        String description
){
}
