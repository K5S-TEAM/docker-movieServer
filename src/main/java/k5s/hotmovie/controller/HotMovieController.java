package k5s.hotmovie.controller;

import k5s.hotmovie.domain.HotMovie;
import k5s.hotmovie.dto.AuthenticationResponseDto;
import k5s.hotmovie.error.InvalidAuthenticationException;
import k5s.hotmovie.service.AuthService;
import k5s.hotmovie.service.MovieService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
public class HotMovieController {
    private final MovieService movieService;
    private final AuthService authService;

    @Autowired
    public HotMovieController(MovieService movieService, AuthService authService){
        this.movieService = movieService;
        this.authService = authService;
    }

//    @GetMapping("/movies")
//    public String list(Model model){
//        List<HotMovie> movies = movieService.findMovies();
//        model.addAttribute("movies", movies);
//        return "movies/moviePreviewList";
//    }

    @GetMapping("/movies")
    public String list(@RequestParam(required = false, defaultValue = "1") int page, Model model
            , @CookieValue(value = "accessToken", required = false) String accessToken) {

        if (page < 1) {
            page = 1;
        }

        System.out.println("무비스홈에서 토큰값 : " + accessToken);


        if (accessToken==null) {
            System.out.println("토큰없다~!~!~!~!~!~!");
            List<HotMovie> movies = movieService.findWithPage(page);
            model.addAttribute("movies", movies);
            model.addAttribute("page", page);
            model.addAttribute("memberName", null);
        }
        else {
            System.out.println("토큰있다~!~!~!~!~!~!");
            AuthenticationResponseDto result = authService.requestAuthentication(accessToken);

            Long memberId = result.getId();
            String memberName = result.getName();
            if (memberName==null){
                System.out.println("하지만 인증 끝난 토큰");
                List<HotMovie> movies = movieService.findWithPage(page);
                model.addAttribute("movies", movies);
                model.addAttribute("page", page);
                model.addAttribute("memberName", null);
            }
            else{
                System.out.println("제대로 된 토큰");
                List<HotMovie> movies = movieService.findWithPage(page);
                model.addAttribute("movies", movies);
                model.addAttribute("page", page);
                model.addAttribute("memberName", memberName);
            }
        }
        return "movies/moviePreviewList";
    }

    @GetMapping("/movies/{movieCode}")
    public String getMovie(@PathVariable("movieCode") Long movieCode, Model model
            , @CookieValue(value = "accessToken", required = false) String accessToken) {

        System.out.println("무비인포에서 토큰값 : " + accessToken);

        if (accessToken==null) {
            System.out.println("토큰없다~!~!~!~!~!~!");
            List<HotMovie> movie = movieService.findOne(movieCode);
            model.addAttribute("movies", movie);
            model.addAttribute("movieCode", movieCode);
            model.addAttribute("memberName", null);
        }
        else {
            System.out.println("토큰있다~!~!~!~!~!~!");
            AuthenticationResponseDto result = authService.requestAuthentication(accessToken);
            String memberName = result.getName();

            List<HotMovie> movie = movieService.findOne(movieCode);
            model.addAttribute("movies", movie);
            model.addAttribute("movieCode", movieCode);
            model.addAttribute("memberName", memberName);
        }
        return "movies/movieInfo";
    }

    @GetMapping("/movies/request-logout")
    @ResponseBody
    public void logout(@CookieValue(value = "accessToken", required = false) String accessToken) {
        System.out.println("로그아웃 함수에서 토큰값 : " + accessToken);
        authService.requestLogout(accessToken);
    }
}
