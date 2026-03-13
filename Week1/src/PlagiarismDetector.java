import java.util.*;
public class PlagiarismDetector {
    // n-gram size
    private int N = 5;
    // n-gram -> set of documents containing it
    private HashMap<String, Set<String>> ngramIndex;
    // document -> list of n-grams
    private HashMap<String, List<String>> documentNgrams;
    public PlagiarismDetector() {
        ngramIndex = new HashMap<>();
        documentNgrams = new HashMap<>();
    }
    // Extract n-grams from text
    private List<String> generateNgrams(String text) {
        List<String> ngrams = new ArrayList<>();
        String[] words = text.toLowerCase().split("\\s+");
        for (int i = 0; i <= words.length - N; i++) {
            StringBuilder gram = new StringBuilder();
            for (int j = 0; j < N; j++) {
                gram.append(words[i + j]).append(" ");
            }
            ngrams.add(gram.toString().trim());
        }
        return ngrams;
    }
    // Add document to database
    public void addDocument(String documentId, String text) {
        List<String> ngrams = generateNgrams(text);
        documentNgrams.put(documentId, ngrams);
        for (String gram : ngrams) {
            ngramIndex.putIfAbsent(gram, new HashSet<>());
            ngramIndex.get(gram).add(documentId);
        }
        System.out.println("Indexed document: " + documentId +
                " (" + ngrams.size() + " n-grams)");
    }
    // Analyze new document
    public void analyzeDocument(String documentId, String text) {
        List<String> ngrams = generateNgrams(text);
        System.out.println("\nAnalyzing document: " + documentId);
        System.out.println("Extracted " + ngrams.size() + " n-grams");
        HashMap<String, Integer> matchCounts = new HashMap<>();
        for (String gram : ngrams) {
            if (ngramIndex.containsKey(gram)) {
                for (String doc : ngramIndex.get(gram)) {
                    matchCounts.put(doc,
                            matchCounts.getOrDefault(doc, 0) + 1);
                }
            }
        }
        for (Map.Entry<String, Integer> entry : matchCounts.entrySet()) {
            String doc = entry.getKey();
            int matches = entry.getValue();
            double similarity =
                    (double) matches / ngrams.size() * 100;
            System.out.println("Found " + matches +
                    " matching n-grams with \"" + doc + "\"");
            System.out.printf("Similarity: %.2f%%\n", similarity);
            if (similarity > 50) {
                System.out.println("⚠ PLAGIARISM DETECTED");
            } else if (similarity > 10) {
                System.out.println("Suspicious similarity");
            }
            System.out.println();
        }
    }
    public static void main(String[] args) {
        PlagiarismDetector detector = new PlagiarismDetector();
        // Existing documents
        String essay1 =
                "Artificial intelligence is transforming the world of technology " +
                        "and enabling new innovations in machine learning";
        String essay2 =
                "Machine learning and artificial intelligence are rapidly changing " +
                        "modern technology and innovation";
        detector.addDocument("essay_089.txt", essay1);
        detector.addDocument("essay_092.txt", essay2);
        // New submission
        String newEssay =
                "Artificial intelligence is transforming the world of technology " +
                        "and enabling many new innovations in machine learning systems";
        detector.analyzeDocument("essay_123.txt", newEssay);
    }
}