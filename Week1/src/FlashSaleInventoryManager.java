import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
public class FlashSaleInventoryManager {
    // productId -> stock count
    private ConcurrentHashMap<String, AtomicInteger> stockMap;
    // productId -> waiting list (FIFO)
    private ConcurrentHashMap<String, LinkedHashMap<Integer, Integer>> waitingList;
    public FlashSaleInventoryManager() {
        stockMap = new ConcurrentHashMap<>();
        waitingList = new ConcurrentHashMap<>();
    }
    // Add product with initial stock
    public void addProduct(String productId, int stock) {
        stockMap.put(productId, new AtomicInteger(stock));
        waitingList.put(productId, new LinkedHashMap<>());
    }
    // Check stock availability
    public int checkStock(String productId) {
        AtomicInteger stock = stockMap.get(productId);
        if (stock == null) {
            return 0;
        }
        return stock.get();
    }
    // Purchase item
    public synchronized String purchaseItem(String productId, int userId) {
        AtomicInteger stock = stockMap.get(productId);
        if (stock == null) {
            return "Product not found";
        }
        // If stock available
        if (stock.get() > 0) {
            int remaining = stock.decrementAndGet();
            return "Success: User " + userId +
                    " purchased item. Remaining stock = " + remaining;
        }
        // Stock finished → add to waiting list
        LinkedHashMap<Integer, Integer> queue = waitingList.get(productId);
        int position = queue.size() + 1;
        queue.put(userId, position);
        return "Out of stock. User " + userId +
                " added to waiting list at position #" + position;
    }
    // Show waiting list
    public void showWaitingList(String productId) {
        LinkedHashMap<Integer, Integer> queue = waitingList.get(productId);
        System.out.println("\nWaiting List for " + productId + ":");
        for (Map.Entry<Integer, Integer> entry : queue.entrySet()) {
            System.out.println("User " + entry.getKey() +
                    " -> Position " + entry.getValue());
        }
    }
    // Main method to simulate flash sale
    public static void main(String[] args) {
        FlashSaleInventoryManager manager = new FlashSaleInventoryManager();
        String product = "IPHONE15_256GB";
        // Add product with stock = 5 (small demo)
        manager.addProduct(product, 5);
        System.out.println("Initial Stock: " + manager.checkStock(product));
        // Simulate purchases
        for (int user = 1; user <= 10; user++) {
            String result = manager.purchaseItem(product, user);
            System.out.println(result);
        }
        // Show remaining stock
        System.out.println("\nFinal Stock: " + manager.checkStock(product));
        // Show waiting list
        manager.showWaitingList(product);
    }
}