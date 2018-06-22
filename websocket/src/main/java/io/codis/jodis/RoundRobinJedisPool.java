package io.codis.jodis;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.collect.*;
import com.google.common.io.Closeables;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinJedisPool implements JedisResourcePool {
    private static final Logger LOG = LoggerFactory.getLogger(RoundRobinJedisPool.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String CODIS_PROXY_STATE_ONLINE = "online";
    private static final int CURATOR_RETRY_BASE_SLEEP_MS = 100;
    private static final int CURATOR_RETRY_MAX_SLEEP_MS = 30000;
    private static final ImmutableSet<Type> RESET_TYPES;
    private final CuratorFramework curatorClient;
    private final boolean closeCurator;
    private final PathChildrenCache watcher;
    private volatile ImmutableList<RoundRobinJedisPool.PooledObject> pools;
    private final AtomicInteger nextIdx;
    private final JedisPoolConfig poolConfig;
    private final int connectionTimeoutMs;
    private final int soTimeoutMs;
    private final String password;
    private final int database;
    private final String clientName;

    private final ThreadLocal<PooledObject> threadPooledObject = new ThreadLocal<PooledObject>();

    private RoundRobinJedisPool(CuratorFramework curatorClient, boolean closeCurator, String zkProxyDir, JedisPoolConfig poolConfig, int connectionTimeoutMs, int soTimeoutMs, String password, int database, String clientName) {
        this.pools = ImmutableList.of();
        this.nextIdx = new AtomicInteger(-1);
        this.poolConfig = poolConfig;
        this.connectionTimeoutMs = connectionTimeoutMs;
        this.soTimeoutMs = soTimeoutMs;
        this.password = password;
        this.database = database;
        this.clientName = clientName;
        this.curatorClient = curatorClient;
        this.closeCurator = closeCurator;
        this.watcher = new PathChildrenCache(curatorClient, zkProxyDir, true);
        this.watcher.getListenable().addListener(new PathChildrenCacheListener() {
            private void logEvent(PathChildrenCacheEvent event) {
                StringBuilder msg = new StringBuilder("Receive child event: ");
                msg.append("type=").append(event.getType());
                ChildData data = event.getData();
                msg.append(", path=").append(data.getPath());
                msg.append(", stat=").append(data.getStat());
                msg.append(", length=").append(data.getData().length);
                RoundRobinJedisPool.LOG.info(msg.toString());
            }

            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                this.logEvent(event);
                if(RoundRobinJedisPool.RESET_TYPES.contains(event.getType())) {
                    RoundRobinJedisPool.this.resetPools();
                }

            }
        });

        try {
            this.watcher.start(StartMode.BUILD_INITIAL_CACHE);
        } catch (Exception var11) {
            this.close();
            throw new JedisException(var11);
        }

        this.resetPools();
    }

    private void resetPools() {
        ImmutableList pools = this.pools;
        HashMap addr2Pool = Maps.newHashMapWithExpectedSize(pools.size());
        UnmodifiableIterator builder = pools.iterator();

        while(builder.hasNext()) {
            RoundRobinJedisPool.PooledObject pool = (RoundRobinJedisPool.PooledObject)builder.next();
            addr2Pool.put(pool.addr, pool);
        }

        com.google.common.collect.ImmutableList.Builder builder1 = ImmutableList.builder();
        Iterator pool3 = this.watcher.getCurrentData().iterator();

        while(pool3.hasNext()) {
            ChildData pool1 = (ChildData)pool3.next();

            try {
                CodisProxyInfo t = (CodisProxyInfo)MAPPER.readValue(pool1.getData(), CodisProxyInfo.class);
                if("online".equals(t.getState())) {
                    String addr = t.getAddr();
                    RoundRobinJedisPool.PooledObject pool2 = (RoundRobinJedisPool.PooledObject)addr2Pool.remove(addr);
                    if(pool2 == null) {
                        LOG.info("Add new proxy: " + addr);
                        String[] hostAndPort = addr.split(":");
                        String host = hostAndPort[0];
                        int port = Integer.parseInt(hostAndPort[1]);
                        pool2 = new RoundRobinJedisPool.PooledObject(addr, new JedisPool(this.poolConfig, host, port, this.connectionTimeoutMs, this.password, this.database, this.clientName));
                    }

                    builder1.add(pool2);
                }
            } catch (Throwable var12) {
                LOG.warn("parse " + pool1.getPath() + " failed", var12);
            }
        }

        this.pools = builder1.build();
        pool3 = addr2Pool.values().iterator();

        while(pool3.hasNext()) {
            RoundRobinJedisPool.PooledObject pool4 = (RoundRobinJedisPool.PooledObject)pool3.next();
            LOG.info("Remove proxy: " + pool4.addr);
            pool4.pool.close();
        }

    }

    public Jedis getResource() {
        ImmutableList pools = this.pools;
        if(pools.isEmpty()) {
            throw new JedisException("Proxy list empty");
        } else {
            int current;
            int next;
            do {
                current = this.nextIdx.get();
                next = current >= pools.size() - 1?0:current + 1;
            } while(!this.nextIdx.compareAndSet(current, next));

            PooledObject pooledObject = (PooledObject) pools.get(next);

            threadPooledObject.set(pooledObject);

            return pooledObject.pool.getResource();
        }
    }

    public JedisPool currentThreadPool() {
        return threadPooledObject.get().pool;
    }

    public void close() {
        try {
            Closeables.close(this.watcher, true);
        } catch (IOException var4) {
            throw new AssertionError("IOException should not have been thrown", var4);
        }

        if(this.closeCurator) {
            this.curatorClient.close();
        }

        ImmutableList pools = this.pools;
        this.pools = ImmutableList.of();
        Iterator var2 = pools.iterator();

        while(var2.hasNext()) {
            RoundRobinJedisPool.PooledObject pool = (RoundRobinJedisPool.PooledObject)var2.next();
            pool.pool.close();
        }

    }

    public static RoundRobinJedisPool.Builder create() {
        return new RoundRobinJedisPool.Builder(null);
    }

    static {
        RESET_TYPES = Sets.immutableEnumSet(Type.CHILD_ADDED, new Type[]{Type.CHILD_UPDATED, Type.CHILD_REMOVED});
    }

    public static final class Builder {
        private CuratorFramework curatorClient;
        private boolean closeCurator;
        private String zkProxyDir;
        private String zkAddr;
        private int zkSessionTimeoutMs;
        private JedisPoolConfig poolConfig;
        private int connectionTimeoutMs;
        private int soTimeoutMs;
        private String password;
        private int database;
        private String clientName;

        private Builder(Object o) {
            this.connectionTimeoutMs = 2000;
            this.soTimeoutMs = 2000;
            this.database = 0;
        }

        public RoundRobinJedisPool.Builder curatorClient(CuratorFramework curatorClient, boolean closeCurator) {
            this.curatorClient = curatorClient;
            this.closeCurator = closeCurator;
            return this;
        }

        public RoundRobinJedisPool.Builder zkProxyDir(String zkProxyDir) {
            this.zkProxyDir = zkProxyDir;
            return this;
        }

        public RoundRobinJedisPool.Builder curatorClient(String zkAddr, int zkSessionTimeoutMs) {
            this.zkAddr = zkAddr;
            this.zkSessionTimeoutMs = zkSessionTimeoutMs;
            return this;
        }

        public RoundRobinJedisPool.Builder poolConfig(JedisPoolConfig poolConfig) {
            this.poolConfig = poolConfig;
            return this;
        }

        public RoundRobinJedisPool.Builder timeoutMs(int timeoutMs) {
            this.connectionTimeoutMs = this.soTimeoutMs = timeoutMs;
            return this;
        }

        public RoundRobinJedisPool.Builder connectionTimeoutMs(int connectionTimeoutMs) {
            this.connectionTimeoutMs = connectionTimeoutMs;
            return this;
        }

        public RoundRobinJedisPool.Builder soTimeoutMs(int soTimeoutMs) {
            this.soTimeoutMs = soTimeoutMs;
            return this;
        }

        public RoundRobinJedisPool.Builder password(String password) {
            this.password = password;
            return this;
        }

        public RoundRobinJedisPool.Builder database(int database) {
            this.database = database;
            return this;
        }

        public RoundRobinJedisPool.Builder clientName(String clientName) {
            this.clientName = clientName;
            return this;
        }

        private void validate() {
            Preconditions.checkNotNull(this.zkProxyDir, "zkProxyDir can not be null");
            if(this.curatorClient == null) {
                Preconditions.checkNotNull(this.zkAddr, "zk client can not be null");
                this.curatorClient = CuratorFrameworkFactory.builder().connectString(this.zkAddr).sessionTimeoutMs(this.zkSessionTimeoutMs).retryPolicy(new BoundedExponentialBackoffRetryUntilElapsed(100, 30000, -1L)).build();
                this.curatorClient.start();
                this.closeCurator = true;
            } else if(this.curatorClient.getState() == CuratorFrameworkState.LATENT) {
                this.curatorClient.start();
            }

            if(this.poolConfig == null) {
                this.poolConfig = new JedisPoolConfig();
            }

        }

        public RoundRobinJedisPool build() {
            this.validate();
            return new RoundRobinJedisPool(this.curatorClient, this.closeCurator, this.zkProxyDir, this.poolConfig, this.connectionTimeoutMs, this.soTimeoutMs, this.password, this.database, this.clientName);
        }
    }

    private static final class PooledObject {
        public final String addr;
        public final JedisPool pool;

        public PooledObject(String addr, JedisPool pool) {
            this.addr = addr;
            this.pool = pool;
        }
    }
}
