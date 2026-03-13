import java.util.concurrent.ConcurrentHashMap;
class TokenBucket {
    int tokens;
    int maxTokens;
    double refillRate;
    long lastRefillTime;
    public TokenBucket(int maxTokens, double refillRate) {
        this.maxTokens = maxTokens;
        this.refillRate = refillRate;
        this.tokens = maxTokens;
        this.lastRefillTime = System.currentTimeMillis();
    }
    synchronized boolean allowRequest() {
        refill();
        if (tokens > 0) {
            tokens--;
            return true;
        }
        return false;
    }
    void refill() {
        long now = System.currentTimeMillis();
        double tokensToAdd = ((now - lastRefillTime) / 1000.0) * refillRate;
        if (tokensToAdd > 0) {
            tokens = Math.min(maxTokens, tokens + (int) tokensToAdd);
            lastRefillTime = now;
        }
    }
}
public class RateLimiter {
    private ConcurrentHashMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();
    public boolean checkRateLimit(String clientId) {
        buckets.putIfAbsent(clientId,
                new TokenBucket(1000, 1000.0 / 3600));
        TokenBucket bucket = buckets.get(clientId);
        return bucket.allowRequest();
    }
    public static void main(String[] args) {
        RateLimiter rl = new RateLimiter();
        System.out.println(rl.checkRateLimit("abc123"));
    }
}
