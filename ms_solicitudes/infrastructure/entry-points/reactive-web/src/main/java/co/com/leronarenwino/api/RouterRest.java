package co.com.leronarenwino.api;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    private static final String BASE_URL = "/api/v1";
    private static final String LOAN_APPLICATION_URL = BASE_URL + "/loan-application";
    private static final String USER_URL = BASE_URL + "/user";
    private static final String LOAN_APPLICATION_ID_URL = LOAN_APPLICATION_URL + "/{id}";

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/loan-application",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.GET,
                    beanClass = Handler.class,
                    beanMethod = "getAllLoanApplications"
            ),
            @RouterOperation(
                    path = "/api/v1/loan-application",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "saveLoanApplication"
            )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST(LOAN_APPLICATION_URL), handler::saveLoanApplication)
                .andRoute(PUT(LOAN_APPLICATION_ID_URL), handler::updateLoanApplicationStatus)
                .andRoute(GET(LOAN_APPLICATION_URL), handler::getAllLoanApplications)
                .andRoute(GET(USER_URL), handler::getUserData);
    }
}