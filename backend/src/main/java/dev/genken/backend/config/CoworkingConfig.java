package dev.genken.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoworkingConfig {
    @Value("${seat.rows}")
    private int rows;
    @Value("${seat.columns}")
    private int columns;

    public int getRows() { return rows; }

    public int getColumns() { return columns; }
}
