package co.com.leronarenwino.api;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class Handler {
    public Handler() {
        // TODO document why this constructor is empty
    }

    public Mono<ServerResponse> listenGETUseCase(ServerRequest serverRequest) {

        return ServerResponse.ok().bodyValue("");
    }
}
