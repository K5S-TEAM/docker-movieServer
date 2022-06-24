package k5s.hotmovie.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import k5s.hotmovie.domain.HotMovie;
import k5s.hotmovie.dto.MovieNameRequestDto;
import k5s.hotmovie.dto.MovieNameResponseDto;
import k5s.hotmovie.dto.ScoreUpdateRequestDto;
import k5s.hotmovie.service.MovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MovieApiController {

    private final MovieService movieService;

    @PostMapping("/movies")
    public ResponseEntity getMovieName(@RequestBody MovieNameRequestDto dto) {
        List<HotMovie> result = movieService.findOne(dto.getId());

        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new MovieNameResponseDto(result.get(0).getTitle()));
        }
    }

    @GetMapping("/movie/{id}")
    public ResponseEntity getMovieNames(@PathVariable Long id) {
        List<HotMovie> result = movieService.findOne(id);

        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new MovieNameResponseDto(result.get(0).getTitle()));
        }
    }

    /**
     * @param movieCode
     * @param dto
     * 특정 영화 평점 update api
     * @return
     */
    @PatchMapping("/movies/{movieCode}")
    public ResponseEntity updateMovieScore(@PathVariable("movieCode") Long movieCode
            , @RequestBody ScoreUpdateRequestDto dto) {
        movieService.updateMovieScore(movieCode, dto.getAverageScore());
        return ResponseEntity.status(HttpStatus.OK).body("ok");
    }

    @PostMapping("/search")
    public String getMovieList() {
        List<HotMovie> result = movieService.findMovieList();

        JsonArray movieList = new JsonArray();
        for(int i=0; i<result.size(); i++) {
            JsonObject jObj = new JsonObject();
            jObj.addProperty("title", result.get(i).getTitle());
            jObj.addProperty("code", result.get(i).getCode());
            movieList.add(jObj);
        }
//        log.info(movieList.toString().replaceAll("\"title\"", "title").replaceAll("\"code\"","code"));
        return movieList.toString();
    }
}

