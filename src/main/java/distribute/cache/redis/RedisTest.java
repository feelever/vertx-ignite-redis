package distribute.cache.redis;

import redis.clients.jedis.Jedis;

public class RedisTest {
    private static final String ip = "127.0.0.01";
    private static final String auth = "";
    private static final Integer port = 6379;
    //测试的数据行数
    private static final Integer test_rows = 500000;
    //线程数
    private static final Integer thread_cnt = 200;

    public static void main(String[] args) {
        multiThread();
    }

    public static void multiThread() {
        System.out.println("==================================================================");
        System.out.println("开始测试多线程写入[线程数：" + thread_cnt + "]");
        Long startTime = System.currentTimeMillis();

        Thread[] threads = new Thread[thread_cnt];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new TestThread(true));
        }
        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Long endTime = System.currentTimeMillis(); //获取结束时间
        float interval = endTime - startTime == 0 ? 1 : endTime - startTime;
        float tpms = (float) test_rows / interval;
        System.out.println("程序运行时间： " + interval + "ms");
        System.out.println("每毫秒写入:" + tpms + "条。");
        System.out.println("每秒写入:" + tpms * 1000 + "条。");

        System.out.println("==================================================================");
        System.out.println("开始测试多线程写入[线程数：" + thread_cnt + "]");
        startTime = System.currentTimeMillis();
        Thread[] readthreads = new Thread[thread_cnt];
        for (int i = 0; i < readthreads.length; i++) {
            readthreads[i] = new Thread(new TestThread(false));
        }
        for (int i = 0; i < readthreads.length; i++) {
            readthreads[i].start();
        }

        for (Thread thread : readthreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        endTime = System.currentTimeMillis(); //获取结束时间
        interval = endTime - startTime == 0 ? 1 : endTime - startTime;
        tpms = (float) test_rows / interval;
        System.out.println("程序运行时间： " + interval + "ms");
        System.out.println("每毫秒读取:" + tpms + "条。");
        System.out.println("每秒读取:" + tpms * 1000 + "条。");
    }

    static class TestThread implements Runnable {
        private boolean readMode = true;

        public TestThread(boolean readMode) {
            this.readMode = readMode;
        }

        @Override
        public void run() {
            Jedis j = new Jedis(ip, port);
            //j.auth(auth);
            for (int i = 0; i < test_rows / thread_cnt; i++) {
                if (this.readMode) {
                    j.get("foo" + i);
                } else {
                    j.set("foo" + i, "bar" + i);
                }
            }
            j.disconnect();
        }


    }
}