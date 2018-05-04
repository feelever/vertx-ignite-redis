package distribute.cache.ignite;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CacheWriteSynchronizationMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;

import java.util.Arrays;

public class IgniteFactory {
    static Ignite ignite;
    static {
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setClientMode(true);
        TcpDiscoverySpi spi = new TcpDiscoverySpi();
        TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();
        ipFinder.setAddresses(Arrays.asList("127.0.0.1"," 192.168.21.32"));
        spi.setIpFinder(ipFinder);

        CacheConfiguration cacheConfiguration = new CacheConfiguration<String, DataClass>();
        cacheConfiguration.setCacheMode(CacheMode.PARTITIONED);
        cacheConfiguration.setBackups(1);
        cacheConfiguration.setWriteSynchronizationMode(CacheWriteSynchronizationMode.FULL_SYNC);
        cacheConfiguration.setName("test");
        cfg.setClientMode(true);
        cfg.setDiscoverySpi(spi);
        cfg.setCacheConfiguration(cacheConfiguration);
        ignite = Ignition.start(cfg);
        ignite = Ignition.start(cfg);
    }
    private IgniteFactory() {

    }

    public static Ignite newInstance() {
        return ignite;
    }

}
