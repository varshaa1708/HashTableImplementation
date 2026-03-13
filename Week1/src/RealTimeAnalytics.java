import java.util.*;
import java.util.concurrent.*;
public class RealTimeAnalytics {
    // Event class
    static class PageViewEvent {
        String url;
        String userId;
        String source;
        PageViewEvent(String url, String userId, String source) {
            this.url = url;
            this.userId = userId;
            this.source = source;
        }
    }
    // pageUrl -> visit count
    private ConcurrentHashMap<String, Integer> pageViews;
    // pageUrl -> unique users
    private ConcurrentHashMap<String, Set<String>> uniqueVisitors;
    // traffic source -> count
    private ConcurrentHashMap<String, Integer> trafficSources;
    public RealTimeAnalytics() {
        pageViews = new ConcurrentHashMap<>();
        uniqueVisitors = new ConcurrentHashMap<>();
        trafficSources = new ConcurrentHashMap<>();
    }
    // Process incoming event
    public void processEvent(PageViewEvent event) {
        // Count page views
        pageViews.put(event.url,
                pageViews.getOrDefault(event.url, 0) + 1);
        // Track unique visitors
        uniqueVisitors.putIfAbsent(event.url,
                ConcurrentHashMap.newKeySet());
        uniqueVisitors.get(event.url).add(event.userId);
        // Track traffic sources
        trafficSources.put(event.source,
                trafficSources.getOrDefault(event.source, 0) + 1);
    }
    // Get top N pages
    public List<Map.Entry<String, Integer>> getTopPages(int n) {
        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>(Map.Entry.comparingByValue());
        for (Map.Entry<String, Integer> entry : pageViews.entrySet()) {
            pq.add(entry);
            if (pq.size() > n) {
                pq.poll();
            }
        }
        List<Map.Entry<String, Integer>> result = new ArrayList<>();
        while (!pq.isEmpty()) {
            result.add(pq.poll());
        }
        Collections.reverse(result);
        return result;
    }
    // Display dashboard
    public void getDashboard() {
        System.out.println("\n===== REAL-TIME DASHBOARD =====");
        System.out.println("\nTop Pages:");
        List<Map.Entry<String, Integer>> topPages = getTopPages(10);
        int rank = 1;
        for (Map.Entry<String, Integer> entry : topPages) {
            int uniqueCount =
                    uniqueVisitors.get(entry.getKey()).size();
            System.out.println(rank + ". " + entry.getKey() +
                    " - " + entry.getValue() +
                    " views (" + uniqueCount + " unique)");
            rank++;
        }
        System.out.println("\nTraffic Sources:");
        int total = trafficSources.values()
                .stream().mapToInt(i -> i).sum();
        for (Map.Entry<String, Integer> entry :
                trafficSources.entrySet()) {
            double percent =
                    (double) entry.getValue() / total * 100;
            System.out.printf("%s: %.2f%%\n",
                    entry.getKey(), percent);
        }
    }
    public static void main(String[] args)
            throws InterruptedException {
        RealTimeAnalytics analytics = new RealTimeAnalytics();
        // simulate streaming events
        List<PageViewEvent> events = List.of(
                new PageViewEvent("/article/breaking-news","user1","google"),
                new PageViewEvent("/article/breaking-news","user2","facebook"),
                new PageViewEvent("/sports/championship","user3","direct"),
                new PageViewEvent("/sports/championship","user4","google"),
                new PageViewEvent("/article/breaking-news","user5","google"),
                new PageViewEvent("/tech/ai-future","user6","direct"),
                new PageViewEvent("/tech/ai-future","user1","google"),
                new PageViewEvent("/tech/ai-future","user7","facebook")
        );
        for (PageViewEvent event : events) {
            analytics.processEvent(event);
        }
        // update dashboard every 5 seconds
        ScheduledExecutorService scheduler =
                Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(
                analytics::getDashboard,
                0,
                5,
                TimeUnit.SECONDS
        );
        Thread.sleep(15000);
        scheduler.shutdown();
    }
}