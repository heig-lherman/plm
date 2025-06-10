package ch.vd.ptep.mrq.compiler.configuration;

import ch.vd.ptep.mrq.compiler.core.CompilerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnBooleanProperty(prefix = "mrq.compiler", name = "enabled", matchIfMissing = true)
public class CompilerAutoConfiguration {

    @Bean
    public CompilerService compilerService(
        ObjectMapper objectMapper
    ) {
        return new CompilerService(objectMapper);
    }
}
