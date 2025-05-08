package planit.massiverstandard.filter.processor;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import planit.massiverstandard.filter.entity.ApiFilter;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ApiFilterProcessor {

//    private final RestTemplate restTemplate;
//
////    @Retry(name = "apiFilter", fallbackMethod = "onFailure")
////    @CircuitBreaker(name = "apiFilter", fallbackMethod = "onFailure")
//    public Map<String,Object> process(ApiFilter filter, Map<String,Object> item) {
//        HttpEntity<Map<String,Object>> req = new HttpEntity<>(item, defaultJsonHeaders());
//        ResponseEntity<Map> resp = restTemplate.exchange(
//            filter.getUrl(),
//            HttpMethod.valueOf(filter.getMethod().toUpperCase()),
//            req,
//            Map.class
//        );
//        if (!resp.getStatusCode().is2xxSuccessful()) {
//            throw new ApiFilterException(filter, resp);
//        }
//        log.debug("API 응답: {}", resp.getBody());
//        // 필요 시 item에 응답값 병합 후 반환
//        return resp.getBody();
//    }
//
//    private HttpHeaders defaultJsonHeaders() {
//        HttpHeaders h = new HttpHeaders();
//        h.setContentType(MediaType.APPLICATION_JSON);
//        return h;
//    }
//
//    private Map<String,Object> onFailure(ApiFilter filter, Map<String,Object> item, Throwable t) {
//        log.error("API 필터 실패 (id={}, url={})", filter.getId(), filter.getUrl(), t);
//        // fallback 전략: 로컬 캐시, 빈 맵 반환 등
//        return item;
//    }
}
