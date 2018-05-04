package distribute.cache;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.ext.web.Router;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;
import io.vertx.redis.impl.RedisClientImpl;
import io.vertx.spi.cluster.ignite.IgniteClusterManager;

public class Starter {
    public static void main(String[] args) {
        ClusterManager clusterManager = new IgniteClusterManager();
        VertxOptions options = new VertxOptions().setClusterManager(clusterManager);
//        options.setClusterHost("192.168.21.32");
//        options.setClustered(true);
        Vertx.clusteredVertx(options, res -> {
            if (res.succeeded()) {
                Router router = Router.router(res.result());
                router.route().handler(st -> {
                    System.out.println(st.getBodyAsJson());
                    //clusterManager.getSyncMap("test");
                    st.response().end("hello world");
                   /* clusterManager.getAsyncMap("test",map->{
                        map.result().put("hello","world",putMap->{
                            System.out.println(clusterManager.getSyncMap("test").get("hello"));
                        });
                    });*/
                    RedisClient redisClient = new RedisClientImpl(st.vertx(),new RedisOptions());
                    redisClient.set("hello","world", redis->{
                        st.request().bodyHandler(body->{
                            System.out.println(body.toString());
                        });
                        /*redisClient.get("hello",getRedis->{
                            st.pathParams();
                            System.out.println(getRedis.result());
                        });*/
                    });
                });
                res.result().createHttpServer().requestHandler(router::accept).listen(9000);
            }
        });

        //config route root handler 权限的AccessHandler


    }
}
