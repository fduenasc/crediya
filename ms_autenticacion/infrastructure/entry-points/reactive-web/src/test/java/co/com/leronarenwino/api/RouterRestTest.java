package co.com.leronarenwino.api;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = {RouterRest.class, Handler.class})
@WebFluxTest
class RouterRestTest {

    @Autowired
    private RouterFunction<ServerResponse> routerFunction;

    @MockitoBean
    private Handler handler;

    @Test
    void routerFunctionShouldRoutePostToRegistrarUsuarioTest() {
        Mockito.when(handler.saveUser(org.mockito.ArgumentMatchers.any()))
                .thenReturn(ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).build());

        WebTestClient webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build();

        webTestClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON);

        assertThat(routerFunction).isNotNull();
    }
}