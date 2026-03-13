import java.util.*;
class LRUCache<K,V> extends LinkedHashMap<K,V> {
    private int capacity;
    public LRUCache(int capacity) {
        super(capacity,0.75f,true);
        this.capacity = capacity;
    }
    protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
        return size() > capacity;
    }
}
public class MultiLevelCache {
    LRUCache<String,String> L1 = new LRUCache<>(10000);
    LRUCache<String,String> L2 = new LRUCache<>(100000);
    Map<String,String> database = new HashMap<>();
    int L1Hits = 0;
    int L2Hits = 0;
    int L3Hits = 0;
    public MultiLevelCache() {
        database.put("video_123","VideoData123");
        database.put("video_999","VideoData999");
    }
    public String getVideo(String id) {
        if (L1.containsKey(id)) {
            L1Hits++;
            System.out.println("L1 Cache HIT");
            return L1.get(id);
        }
        if (L2.containsKey(id)) {
            L2Hits++;
            System.out.println("L2 Cache HIT");
            String data = L2.get(id);
            L1.put(id,data);
            return data;
        }
        if (database.containsKey(id)) {
            L3Hits++;
            System.out.println("L3 Database HIT");
            String data = database.get(id);
            L2.put(id,data);
            return data;
        }
        return null;
    }
    public void getStatistics() {
        int total = L1Hits + L2Hits + L3Hits;
        System.out.println("L1 Hits: " + L1Hits);
        System.out.println("L2 Hits: " + L2Hits);
        System.out.println("L3 Hits: " + L3Hits);
        double hitRate = total == 0 ? 0 : ((L1Hits+L2Hits)*100.0)/total;
        System.out.println("Overall Hit Rate: " + hitRate + "%");
    }
    public static void main(String[] args) {
        MultiLevelCache cache = new MultiLevelCache();
        cache.getVideo("video_123");
        cache.getVideo("video_123");
        cache.getVideo("video_999");
        cache.getStatistics();
    }
}