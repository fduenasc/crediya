package co.com.leronarenwino.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/users",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "saveUser",
                    operation = @Operation(
                            summary = "Create user",
                            description = "Create a new user in the system",
                            security = @SecurityRequirement(name = "bearerAuth")
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/login",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "login",
                    operation = @Operation(
                            summary = "Login user",
                            description = "Login user in the system and return a JWT token"
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(GET("/api/v1/user/{email}"), handler::getUserDataByEmailPathVariable)
                .andRoute(POST("/api/v1/users"), handler::saveUser)
                .andRoute(POST("/api/v1/login"), handler::login)
                .andRoute(GET("/api/v1/validate"), handler::validateToken);
    }
}
