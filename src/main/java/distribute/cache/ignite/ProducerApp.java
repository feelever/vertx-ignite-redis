package distribute.cache.ignite;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.ignite.IgniteClusterManager;

import java.util.Map;

public class ProducerApp extends AbstractVerticle {
    static Map<String, RateLimiter> map = Maps.newHashMap();


    public static void main(String[] args) {
        ClusterManager manager = new IgniteClusterManager();
        Vertx.clusteredVertx(new VertxOptions().setClustered(true).setClusterManager(manager), ar -> {
            if (ar.failed()) {
                System.err.println("Cannot create vert.x instance : " + ar.cause());
            } else {
                Vertx vertx = ar.result();
                vertx.deployVerticle(ProducerApp.class.getName());
            }
        });
    }

    @Override
    public void start() throws Exception {
        vertx.setPeriodic(1,st->{
            long a = System.currentTimeMillis();
            System.out.println(System.currentTimeMillis() - a);
            vertx.eventBus().send("news", "hello vert.x");
        });
    }
}