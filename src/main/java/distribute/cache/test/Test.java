package distribute.cache.test;

import distribute.cache.ignite.IgniteTest;
import distribute.cache.redis.RedisTest;

public class Test {
    public static void main(String[] args) {
        IgniteTest.multiThread();
        System.out.println("=========================redis================================");
        RedisTest.multiThread();
    }
}
