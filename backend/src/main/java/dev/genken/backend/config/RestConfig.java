package dev.genken.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class RestConfig {
    @Bean
    public RestTemplate authRestTemplate(@Value("${auth.service.url}") String baseUrl) {
        var rt  = new RestTemplate();
        rt.setUriTemplateHandler(new DefaultUriBuilderFactory(baseUrl));
        return rt;
    }
}