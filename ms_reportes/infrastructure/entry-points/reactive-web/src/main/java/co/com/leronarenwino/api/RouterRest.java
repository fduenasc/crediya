package co.com.leronarenwino.api;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    private static final String BASE_PATH = "/api/v1";
    private static final String REPORTS_URL = BASE_PATH + "/reports";

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/reports",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.GET,
                    beanClass = Handler.class,
                    beanMethod = "getApprovedLoanApplications"
            )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(GET(REPORTS_URL), handler::getApprovedLoanApplications);
    }
}