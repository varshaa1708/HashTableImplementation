import java.util.*;
class Transaction {
    int id;
    int amount;
    String merchant;
    String account;
    long time;
    Transaction(int id, int amount, String merchant, String account, long time) {
        this.id = id;
        this.amount = amount;
        this.merchant = merchant;
        this.account = account;
        this.time = time;
    }
}
public class TransactionAnalyzer {
    List<Transaction> transactions = new ArrayList<>();
    public void add(Transaction t) {
        transactions.add(t);
    }
    // Classic Two Sum
    public List<int[]> findTwoSum(int target) {
        Map<Integer, Transaction> map = new HashMap<>();
        List<int[]> result = new ArrayList<>();
        for (Transaction t : transactions) {
            int complement = target - t.amount;
            if (map.containsKey(complement)) {
                result.add(new int[]{map.get(complement).id, t.id});
            }
            map.put(t.amount, t);
        }
        return result;
    }
    // Duplicate detection
    public void detectDuplicates() {
        Map<String, List<Transaction>> map = new HashMap<>();
        for (Transaction t : transactions) {
            String key = t.amount + "-" + t.merchant;
            map.putIfAbsent(key, new ArrayList<>());
            map.get(key).add(t);
        }
        for (String key : map.keySet()) {
            if (map.get(key).size() > 1) {
                System.out.println("Duplicate: " + key);
                for (Transaction t : map.get(key)) {
                    System.out.println("Transaction ID: " + t.id + " Account: " + t.account);
                }
            }
        }
    }
    public static void main(String[] args) {
        TransactionAnalyzer analyzer = new TransactionAnalyzer();
        analyzer.add(new Transaction(1,500,"StoreA","acc1",1000));
        analyzer.add(new Transaction(2,300,"StoreB","acc2",1015));
        analyzer.add(new Transaction(3,200,"StoreC","acc3",1030));
        List<int[]> pairs = analyzer.findTwoSum(500);
        for (int[] p : pairs) {
            System.out.println("Pair: " + p[0] + " , " + p[1]);
        }
    }
}