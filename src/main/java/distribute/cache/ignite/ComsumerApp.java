package distribute.cache.ignite;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.ignite.IgniteClusterManager;

import java.util.Map;

public class ComsumerApp extends AbstractVerticle {
    static Map<String, RateLimiter> map = Maps.newHashMap();
    public static void main(String[] args) {
        RateLimiter rateLimiter = RateLimiter.create(50);
        map.put("test",rateLimiter);
        ClusterManager manager = new IgniteClusterManager();
        Vertx.clusteredVertx(new VertxOptions().setClustered(true).setClusterManager(manager), ar -> {
            if (ar.failed()) {
                System.err.println("Cannot create vert.x instance : " + ar.cause());
            } else {
                Vertx vertx = ar.result();
                manager.getSyncMap("test").put("hello","world");
                System.out.println(manager.getSyncMap("test").get("hello"));
                vertx.deployVerticle(ComsumerApp.class.getName());
            }
        });
    }
    @Override
    public void start() throws Exception {
        vertx.eventBus().consumer("news", message -> {
            map.get("test").acquire();
            System.out.println(">> " + message.body());
        });
    }
}
