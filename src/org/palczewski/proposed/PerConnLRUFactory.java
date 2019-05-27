package org.palczewski.proposed;

import java.sql.SQLException;
import java.util.Properties;
import java.util.Set;

import com.mysql.cj.CacheAdapter;
import com.mysql.cj.CacheAdapterFactory;
import com.mysql.cj.MysqlConnection;
import com.mysql.cj.ParseInfo;
import com.mysql.cj.util.LRUCache;
/*
Proposed CacheAdapterFactory implementation migrated tp Connector/J 8.0.
 */
public class PerConnLRUFactory implements CacheAdapterFactory<String, ParseInfo> {

    public CacheAdapter<String, ParseInfo> getInstance(MysqlConnection forConnection, String url, int cacheMaxSize, int maxKeySize, Properties connectionProperties)
            throws SQLException {

        return new PerConnectionLRU(forConnection,
                cacheMaxSize, maxKeySize);
    }

    @Override
    public CacheAdapter<String, ParseInfo> getInstance(Object o, String s, int i, int i1) {
        // Leaving this null. makes compiler happy.
        // Method defined above
        return null;
    }

    class PerConnectionLRU implements CacheAdapter<String, ParseInfo> {
        private final int cacheSqlLimit;
        private final LRUCache cache;
        private final MysqlConnection conn;

        protected PerConnectionLRU(MysqlConnection forConnection,
                                   int cacheMaxSize, int maxKeySize) {
            final int cacheSize = cacheMaxSize;
            cacheSqlLimit = maxKeySize;
            cache = new LRUCache(cacheSize);
            conn = forConnection;
        }

        public ParseInfo get(String key) {
            if (key == null || key.length() > this.cacheSqlLimit) {
                return null;
            }

            synchronized (conn.getConnectionMutex()) {
                return (ParseInfo) cache.get(key);
            }
        }

        public void put(String key, ParseInfo value) {
            if (key == null || key.length() > this.cacheSqlLimit) {
                return;
            }

            synchronized (conn.getConnectionMutex()) {
                cache.put(key, value);
            }
        }

        public void invalidate(String key) {
            synchronized (conn.getConnectionMutex()) {
                cache.remove(key);
            }
        }

        public void invalidateAll(Set<String> keys) {
            synchronized (conn.getConnectionMutex()) {
                for (String key : keys) {
                    cache.remove(key);
                }
            }

        }

        public void invalidateAll() {
            synchronized (conn.getConnectionMutex()) {
                cache.clear();
            }
        }
    }
}
