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
    private static final String REPORTS_URL = BASE_PATH + "/reports";

    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(GET(REPORTS_URL), serverRequest -> handler.getTotalApprovedLoanApplications())
                .andRoute(GET("/api/v1/report"), serverRequest -> {
                    String token = serverRequest.headers().firstHeader("Authorization");
                    return handler.getApprovedLoanApplications(token);
                });
    }
}