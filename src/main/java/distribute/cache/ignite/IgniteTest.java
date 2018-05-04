package distribute.cache.ignite;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CacheWriteSynchronizationMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import java.util.Arrays;

public class IgniteTest {
    //测试的数据行数
    private static final Integer test_rows = 500000;
    private static final Integer thread_cnt = 200;
    private static final String cacheName = "Ignite Cache";
    private static Ignite ignite;
    private static boolean client_mode = true;
    
    static {
        getIgnite();
    }
    
    public static void main(String[] args) {
        multiThread();
    }
    
    private static Ignite getIgnite() {
        if (ignite == null) {
            TcpDiscoverySpi spi = new TcpDiscoverySpi();
            TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();
            ipFinder.setAddresses(Arrays.asList("127.0.0.1"," 192.168.21.32"));
            spi.setIpFinder(ipFinder);
            
            CacheConfiguration cacheConfiguration = new CacheConfiguration<String, DataClass>();
            cacheConfiguration.setCacheMode(CacheMode.PARTITIONED);
            cacheConfiguration.setBackups(1);
            cacheConfiguration.setWriteSynchronizationMode(CacheWriteSynchronizationMode.FULL_SYNC);
            cacheConfiguration.setName("test");
            IgniteConfiguration cfg = new IgniteConfiguration();
            cfg.setClientMode(client_mode);
            cfg.setDiscoverySpi(spi);
            cfg.setCacheConfiguration(cacheConfiguration);
            ignite = Ignition.start(cfg);
        }
        System.out.println("是否客户端模式：" + client_mode);
        return ignite;
    }

    public static void multiThread() {
        System.out.println("==================================================================");
        System.out.println("开始测试多线程写入[线程数："+thread_cnt+"]");
        Long startTime = System.currentTimeMillis();
        
        Thread[] threads = new Thread[thread_cnt];
        Ignite ignite = getIgnite();        
        IgniteCache<String, DataClass> cache = ignite.getOrCreateCache(cacheName);
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new TestThread(true, cache));
        }
        for (int i = 0; i< threads.length; i++) {
            threads[i].start();
        }
        
        for(Thread thread : threads){
            try {
                thread.join();
            } catch (InterruptedException e) { 
                e.printStackTrace();
            }
        }
        
        Long endTime=System.currentTimeMillis(); //获取结束时间       
        float interval = endTime-startTime == 0 ? 1 : endTime-startTime;
        float tpms = (float)test_rows/interval;
        System.out.println("程序运行时间： "+ interval+"ms");  
        System.out.println("每毫秒写入:"+tpms+"条。");
        System.out.println("每秒写入:"+tpms*1000+"条。");     
        
        System.out.println("==================================================================");
        System.out.println("开始测试多线程读取[线程数："+thread_cnt+"]");
        startTime = System.currentTimeMillis();
        Thread[] readthreads = new Thread[thread_cnt];
        for (int i = 0; i < readthreads.length; i++) {
            readthreads[i] = new Thread(new TestThread(false, cache));
        }
        for (int i = 0; i< readthreads.length; i++) {
            readthreads[i].start();
        }
        
        for(Thread thread : readthreads){
            try {
                thread.join();
            } catch (InterruptedException e) { 
                e.printStackTrace();
            }
        }
        
        endTime=System.currentTimeMillis(); //获取结束时间        
        interval = endTime-startTime == 0 ? 1 : endTime-startTime;
        tpms = (float)test_rows/interval;
        System.out.println("程序运行时间： "+ interval+"ms");  
        System.out.println("每毫秒读取:"+tpms+"条。");
        System.out.println("每秒读取:"+tpms*1000+"条。");
    }
    
    static class TestThread implements Runnable {
        private boolean readMode = true;
        private IgniteCache<String, DataClass> cache;
        public TestThread(boolean readMode, IgniteCache<String, DataClass> cache){
            this.readMode = readMode;
            this.cache = cache;
        }
        
        @Override
        public void run() {
            for (int i = 0; i < test_rows/thread_cnt; i++) {
                if (this.readMode) {
                    cache.get(Integer.toString(i));
                } else {
                    DataClass dc = new DataClass();
                    dc.setName(Integer.toString(i));
                    dc.setValue(i);
                    dc.setStrValue("asdfadsfasfda");
                    cache.put(Integer.toString(i), dc);
                }
            }
        }
    }
}


