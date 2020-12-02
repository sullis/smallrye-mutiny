package guides;

import io.smallrye.mutiny.TimeoutException;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Test;

import javax.naming.ServiceUnavailableException;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings("Convert2MethodRef")
public class UniTimeoutTest {

    @Test
    public void test() {
        Uni<String> uni = Uni.createFrom().nothing();
        // tag::code[]
        Uni<String> uniWithTimeout = uni
                .ifNoItem().after(Duration.ofMillis(100))
                .recoverWithItem("some fallback item");
        // end::code[]
        String item = uniWithTimeout
                .await().indefinitely();
        assertThat(item).isEqualTo("some fallback item");
    }

    @Test
    public void testFail() {
        Uni<String> uni = Uni.createFrom().nothing();
        // tag::fail[]
        Uni<String> uniWithTimeout = uni
                .ifNoItem().after(Duration.ofMillis(100)).fail();
        // end::fail[]
        assertThatThrownBy(() ->
            uniWithTimeout
                    .await().indefinitely()
        ).isInstanceOf(TimeoutException.class);
    }

    @Test
    public void testFailRecover() {
        Uni<String> uni = Uni.createFrom().nothing();
        // tag::fail-recover[]
        Uni<String> uniWithTimeout = uni
            .ifNoItem().after(Duration.ofMillis(100)).fail()
            .onFailure(TimeoutException.class).recoverWithItem("we got a timeout");
        // end::fail-recover[]
        String item = uniWithTimeout
                .await().indefinitely();
        assertThat(item).isEqualTo("we got a timeout");
    }

    @Test
    public void testFailWithSpecificException() {
        Uni<String> uni = Uni.createFrom().nothing();
        // tag::fail-with[]
        Uni<String> uniWithTimeout = uni
            .ifNoItem().after(Duration.ofMillis(100)).failWith(() -> new ServiceUnavailableException());
        // end::fail-with[]
        assertThatThrownBy(() ->
                uniWithTimeout
                        .await().indefinitely()
        ).isInstanceOf(ServiceUnavailableException.class);
    }

    @Test
    public void testFallback() {
        Uni<String> uni = Uni.createFrom().nothing();
        // tag::fallback[]
        Uni<String> uniWithTimeout = uni
                .ifNoItem().after(Duration.ofMillis(100)).recoverWithItem(() -> "fallback");
        // end::fallback[]
        String item = uniWithTimeout
                .await().indefinitely();
        assertThat(item).isEqualTo("fallback");
    }

    @Test
    public void testFallbackUni() {
        Uni<String> uni = Uni.createFrom().nothing();
        // tag::fallback-uni[]
        Uni<String> uniWithTimeout = uni
                .ifNoItem().after(Duration.ofMillis(100)).recoverWithUni(() -> someFallbackUni());
        // end::fallback-uni[]
        String item = uniWithTimeout
                .await().indefinitely();
        assertThat(item).isEqualTo("fallback");
    }

    private Uni<? extends String> someFallbackUni() {
        return Uni.createFrom().item("hello");
    }


    private static class ServiceUnavailableException extends RuntimeException {

    }


}
