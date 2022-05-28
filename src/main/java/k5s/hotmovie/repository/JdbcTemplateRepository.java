package k5s.hotmovie.repository;

import k5s.hotmovie.domain.HotMovie;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.util.List;

public class JdbcTemplateRepository implements MovieRepository{

    private final JdbcTemplate jdbcTemplate;
    public JdbcTemplateRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<HotMovie> findByCode(Long code) {
        return jdbcTemplate.query("select * from movie where code = ?", movieRowMapper(), code);
    }
    @Override
    public List<HotMovie> findMovieList() {
        return jdbcTemplate.query("select title, code from movie", movieListRowMapper());
    }

    @Override
    public List<HotMovie> findAll() {
        return jdbcTemplate.query("select * from movie", movieRowMapper());
    }

    @Override
    public List<HotMovie> findRecentUpdate() {
        return jdbcTemplate.query("select * from movie ORDER BY code DESC LIMIT 5", movieRowMapper());
    }

    @Override
    public List<HotMovie> findWithPage(int page) {
        int row = 12 * (page - 1);
        return jdbcTemplate.query("select * from movie LIMIT 12 OFFSET ?", movieRowMapper(), row);
    }

    @Override
    public void updateMovieScore(Long code, Double score){
        jdbcTemplate.update("update movie set score = ? where code = ?",score, code);
    }

    private RowMapper<HotMovie> movieRowMapper() {
        return (rs, rowNum) -> {
            HotMovie movie = new HotMovie();
            movie.setCode(rs.getLong("code"));
            movie.setTitle(rs.getString("title"));
            movie.setNation(rs.getString("nation"));
            movie.setRating(rs.getString("rating"));
            movie.setScore(rs.getDouble("score"));
            movie.setGenre(rs.getString("genre"));
            movie.setDirector(rs.getString("director"));
            movie.setActor(rs.getString("actor"));
            movie.setStory(rs.getString("story"));
            movie.setOpening_date(rs.getString("opening_date"));
            movie.setRunning_time(rs.getString("running_time"));
            movie.setImg(rs.getString("img"));
            return movie;
        };
    }

    private RowMapper<HotMovie> movieListRowMapper() {
        return (rs, rowNum) -> {
            HotMovie movie = new HotMovie();
            movie.setCode(rs.getLong("code"));
            movie.setTitle(rs.getString("title"));
            return movie;
        };
    }
}