package planit.massiverstandard.filter.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import planit.massiverstandard.unit.entity.Unit;

import java.util.Map;

@Getter
@Entity
@DiscriminatorValue(value = "API")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "massiver_st_api_filter")
public class ApiFilter extends Filter {

    private String method; // GET, POST, PUT, DELETE
    private String url;

    @Builder
    public ApiFilter(Unit unit, String name, int order, String method, String url) {
        super(unit, name, order);
        this.url = url;
        this.method = method;
    }

    // todo: implement process method
    @Override
    public Map<String, Object> process(Map<String, Object> item) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(item, headers);

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.valueOf(method.toUpperCase()), entity, String.class);

            // 응답 상태 확인: 200이 아니면 예외 발생
            if (responseEntity.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("API Error: " + responseEntity.getStatusCode() + " - " + responseEntity.getBody());
            }

            // 응답 데이터 로그 출력
            System.out.println("API Response: " + responseEntity.getBody());

            return item;

        } catch (Exception e) {
            throw new RuntimeException("API 필터 처리 중 오류 발생", e);
        }
    }
}
