import java.util.*;
class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    Map<String, Integer> queries = new HashMap<>();
}
public class AutocompleteSystem {
    TrieNode root = new TrieNode();
    public void insert(String query, int freq) {
        TrieNode node = root;
        for (char c : query.toCharArray()) {
            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);
            node.queries.put(query,
                    node.queries.getOrDefault(query, 0) + freq);
        }
    }
    public List<String> search(String prefix) {
        TrieNode node = root;
        for (char c : prefix.toCharArray()) {
            if (!node.children.containsKey(c))
                return new ArrayList<>();
            node = node.children.get(c);
        }
        Map<String, Integer> map = node.queries;
        PriorityQueue<String> pq =
                new PriorityQueue<>((a, b) -> map.get(a) - map.get(b));
        for (String s : map.keySet()) {
            pq.offer(s);
            if (pq.size() > 10)
                pq.poll();
        }
        List<String> result = new ArrayList<>();
        while (!pq.isEmpty())
            result.add(pq.poll());
        Collections.reverse(result);
        return result;
    }
    public static void main(String[] args) {
        AutocompleteSystem ac = new AutocompleteSystem();
        ac.insert("java tutorial", 100);
        ac.insert("javascript", 80);
        ac.insert("java download", 50);
        System.out.println(ac.search("jav"));
    }
}
