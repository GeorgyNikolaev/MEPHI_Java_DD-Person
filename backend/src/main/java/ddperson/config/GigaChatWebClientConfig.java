package ddperson.config;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;
import java.time.Duration;

@Configuration
public class GigaChatWebClientConfig {

    @Bean
    public WebClient gigaChatWebClient(AppProperties properties) throws SSLException {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofMillis(properties.gigachat().readTimeoutMs()));

        if (properties.gigachat().insecureSsl()) {
            SslContext sslContext = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();
            httpClient = httpClient.secure(spec -> spec.sslContext(sslContext));
        }

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
