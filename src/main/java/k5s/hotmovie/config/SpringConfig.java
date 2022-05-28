package k5s.hotmovie.config;

import k5s.hotmovie.repository.*;
import k5s.hotmovie.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class SpringConfig {

    DataSource dataSource;

    @Autowired
    public SpringConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

//    private EntityManager em;
//
//    @Autowired
//    public SpringConfig(EntityManager em) {
//        this.em = em;
//    }

    @Bean
    public MovieService movieService() {
        return new MovieService(movieRepository());
    }
    @Bean
    public MovieRepository movieRepository() {
//        return new MemoryMovieRepository();
//        return new JdbcMovieRepository(dataSource);
        return new JdbcTemplateRepository(dataSource);
//        return new JpaMovieRepository(em);
    }


}
