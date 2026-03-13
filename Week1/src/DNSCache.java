import java.util.*;
public class DNSCache {
    // DNS Entry class
    static class DNSEntry {
        String domain;
        String ipAddress;
        long expiryTime;
        DNSEntry(String domain, String ipAddress, long ttlSeconds) {
            this.domain = domain;
            this.ipAddress = ipAddress;
            this.expiryTime = System.currentTimeMillis() + (ttlSeconds * 1000);
        }
        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }
    // Cache using LinkedHashMap (LRU)
    private LinkedHashMap<String, DNSEntry> cache;
    private int capacity;
    // statistics
    private int hits = 0;
    private int misses = 0;
    public DNSCache(int capacity) {
        this.capacity = capacity;
        cache = new LinkedHashMap<String, DNSEntry>(capacity, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                return size() > DNSCache.this.capacity;
            }
        };
        startCleanupThread();
    }
    // Resolve domain
    public synchronized String resolve(String domain) {
        DNSEntry entry = cache.get(domain);
        if (entry != null && !entry.isExpired()) {
            hits++;
            return "Cache HIT → " + entry.ipAddress;
        }
        if (entry != null && entry.isExpired()) {
            cache.remove(domain);
            System.out.println("Cache EXPIRED for " + domain);
        }
        misses++;
        // simulate upstream DNS query
        String ip = queryUpstreamDNS(domain);
        DNSEntry newEntry = new DNSEntry(domain, ip, 5); // TTL = 5 sec (demo)
        cache.put(domain, newEntry);
        return "Cache MISS → Query upstream → " + ip;
    }
    // Simulated upstream DNS resolver
    private String queryUpstreamDNS(String domain) {
        Random rand = new Random();
        return "172.217.14." + rand.nextInt(255);
    }
    // Background thread to remove expired entries
    private void startCleanupThread() {
        Thread cleaner = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(3000);
                    synchronized (this) {
                        Iterator<Map.Entry<String, DNSEntry>> iterator = cache.entrySet().iterator();
                        while (iterator.hasNext()) {
                            Map.Entry<String, DNSEntry> entry = iterator.next();
                            if (entry.getValue().isExpired()) {
                                iterator.remove();
                                System.out.println("Removed expired entry: " + entry.getKey());
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        cleaner.setDaemon(true);
        cleaner.start();
    }
    // Cache statistics
    public void getCacheStats() {
        int total = hits + misses;
        double hitRate = total == 0 ? 0 : ((double) hits / total) * 100;
        System.out.println("\nCache Statistics:");
        System.out.println("Hits: " + hits);
        System.out.println("Misses: " + misses);
        System.out.println("Hit Rate: " + String.format("%.2f", hitRate) + "%");
    }
    // Display cache
    public void showCache() {
        System.out.println("\nCurrent Cache:");
        for (Map.Entry<String, DNSEntry> entry : cache.entrySet()) {
            System.out.println(entry.getKey() + " → " + entry.getValue().ipAddress);
        }
    }
    public static void main(String[] args) throws InterruptedException {
        DNSCache dns = new DNSCache(5);
        System.out.println(dns.resolve("google.com"));
        System.out.println(dns.resolve("facebook.com"));
        System.out.println(dns.resolve("google.com")); // hit
        dns.showCache();
        Thread.sleep(6000); // wait for TTL expiry
        System.out.println(dns.resolve("google.com")); // expired
        dns.getCacheStats();
    }
}
