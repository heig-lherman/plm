package ch.vd.ptep.mrq.compiler.core;

import static java.util.Objects.requireNonNull;

import ch.vd.ptep.mrq.compiler.exception.CompilerException;
import ch.vd.ptep.mrq.compiler.model.Statement;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.buildobjects.process.ExternalProcessFailureException;
import org.buildobjects.process.ProcBuilder;
import org.buildobjects.process.TimeoutException;

@RequiredArgsConstructor
public class CompilerService {

    private final ObjectMapper objectMapper;

    public Statement compile(String expression) throws CompilerException {
        try {
            return objectMapper.readValue(
                execute(
                    requireNonNull(getClass().getResource("/compiler/standardql-compiler")),
                    new String[]{},
                    expression
                ),
                Statement.class
            );
        } catch (JacksonException e) {
            throw new CompilerException("Unable to map compiler output to StandardQL statement.", e);
        }
    }

    private static String execute(
        URL executablePath,
        String[] execArgv,
        String parameters
    ) throws CompilerException {
        try {
            var res = new ProcBuilder(Paths.get(executablePath.toURI()).toAbsolutePath().toString(), execArgv)
                .clearEnvironment()
                .withInput(parameters)
                .withTimeoutMillis(Duration.of(1, ChronoUnit.MINUTES).toMillis())
                .run();

            return res.getOutputString();
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        } catch (TimeoutException e) {
            throw new CompilerException("Compiler was forcibly interrupted due to timeout.", e);
        } catch (ExternalProcessFailureException e) {
            throw new CompilerException(
                "Compiler error ended with non-zero return code (%d)".formatted(e.getExitValue()),
                e.getMessage(),
                e
            );
        }
    }

}
