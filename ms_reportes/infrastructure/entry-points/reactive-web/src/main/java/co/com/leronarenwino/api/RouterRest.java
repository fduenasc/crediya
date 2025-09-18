package co.com.leronarenwino.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    private static final String BASE_PATH = "/api/v1";
    private static final String REPORT_URL = BASE_PATH + "/reports";

    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(GET(REPORT_URL), handler::listenGETUseCase);
    }
}
