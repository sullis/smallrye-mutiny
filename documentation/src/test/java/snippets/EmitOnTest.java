package snippets;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

public class EmitOnTest {

    @Test
    public void test() {
        Uni<String> uni = Uni.createFrom().item("hello");
        Multi<String> multi = Multi.createFrom().items("a", "b", "c");

        ExecutorService executor = Executors.newFixedThreadPool(4);
        // tag::code[]

        String res0 = uni.emitOn(executor)
                .onItem().invoke(s ->
                        System.out.println("Received item `" + s + "` on thread: " + Thread.currentThread().getName())
                )
                .await().indefinitely();

        String res1 = multi.emitOn(executor)
                .onItem().invoke(s ->
                        System.out.println("Received item `" + s + "` on thread: " + Thread.currentThread().getName())
                )
                .collectItems().first()
                .await().indefinitely();

        // end::code[]

        assertThat(res0).isEqualTo("hello");
        assertThat(res1).isEqualTo("a");
    }
}
