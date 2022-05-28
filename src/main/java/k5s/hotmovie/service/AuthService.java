package k5s.hotmovie.service;

import k5s.hotmovie.dto.AuthenticationRequestDto;
import k5s.hotmovie.dto.AuthenticationResponseDto;
import k5s.hotmovie.dto.MemberNicknameResponseDto;
import k5s.hotmovie.error.InvalidAuthenticationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class AuthService {

    private final String authServerUrl = "http://54.180.3.74:8083";
    private final String memberConvenienceServerUrl = "http://54.180.106.16:8081";

    //private final String authServerUrl = "k5smovie-auth-svc.default.svc.cluster.local";
    //private final String memberConvenienceServerUrl = "k5smovie-member-convenience-svc.default.svc.cluster.local";

    @Transactional
    public AuthenticationResponseDto requestAuthentication(String accessToken) {
        AuthenticationRequestDto dto = new AuthenticationRequestDto(accessToken);

//        log.info("reqeustAuthentication: " + accessToken);

        WebClient webClient = WebClient.builder().baseUrl(authServerUrl).build();

        AuthenticationResponseDto result = webClient.post()
                .uri("/auth/access-token-valid")
                .body(Mono.just(dto), AuthenticationRequestDto.class)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, error -> Mono.error(new InvalidAuthenticationException("인증 정보가 존재하지 않습니다. 토큰 없음")))
                .bodyToMono(AuthenticationResponseDto.class)
                .onErrorReturn(new AuthenticationResponseDto(null,null))
                .block();
        return result;
    }

    @Transactional
    public String getMemberNickname(Long memberId) {
        WebClient webClient = WebClient.builder().baseUrl(memberConvenienceServerUrl).build();

        MemberNicknameResponseDto result = webClient.get()
                .uri("/member/" + memberId + "/nickname")
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, error -> Mono.error(new InvalidAuthenticationException("사용자 정보가 존재하지 않습니다.")))
                .bodyToMono(MemberNicknameResponseDto.class)
                .block();

        return result.getMemberNickname();
    }
    @Transactional
    public void requestLogout(String accessToken) {
        AuthenticationRequestDto dto = new AuthenticationRequestDto(accessToken);
        WebClient webClient = WebClient.builder().baseUrl(authServerUrl).build();
        webClient.post()
                .uri("/auth/logout")
                .body(Mono.just(dto), AuthenticationRequestDto.class)
                .retrieve()
                .onStatus(HttpStatus::is5xxServerError, error -> Mono.error(new InvalidAuthenticationException("인증 정보가 존재하지 않습니다.")))
                .toBodilessEntity()
                .block();
//        System.out.println("리퀘스트로그아웃 함수에서 토큰값 : " + accessToken);
    }
}
