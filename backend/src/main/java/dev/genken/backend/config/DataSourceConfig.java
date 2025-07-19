package dev.genken.backend.config;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceConfig {
    @Bean
    public DataSource dataSource() {
        String host = System.getenv("DB_HOST");
        String port = System.getenv("DB_PORT");
        String user = System.getenv("DB_USER");
        String dbName = System.getenv("DB_NAME");
        String password = System.getenv("DB_PASS");

        String jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s", host, port, dbName);

        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(jdbcUrl);
        ds.setUsername(user);
        ds.setPassword(password);
        ds.setDriverClassName("org.postgresql.Driver");
        return ds;
    }
}
