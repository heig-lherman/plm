package ch.vd.ptep.mrq.engine.configuration;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class JsonConfig {

    @Bean
    public Module jsonModule() {
        return new JavaTimeModule();
    }
}
