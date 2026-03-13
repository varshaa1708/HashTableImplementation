import java.util.*;

public class UsernameChecker {

    private HashMap<String, Integer> usernameMap;
    private HashMap<String, Integer> attemptFrequency;

    public UsernameChecker() {
        usernameMap = new HashMap<>();
        attemptFrequency = new HashMap<>();
    }

    public void registerUser(String username, int userId) {
        usernameMap.put(username, userId);
    }

    public boolean checkAvailability(String username) {
        attemptFrequency.put(username,
                attemptFrequency.getOrDefault(username, 0) + 1);
        return !usernameMap.containsKey(username);
    }

    public List<String> suggestAlternatives(String username) {

        List<String> suggestions = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            String newName = username + i;

            if (!usernameMap.containsKey(newName)) {
                suggestions.add(newName);
            }
        }

        if (username.contains("_")) {
            String alt = username.replace("_", ".");
            if (!usernameMap.containsKey(alt)) {
                suggestions.add(alt);
            }
        }

        return suggestions;
    }

    public String getMostAttempted() {

        String maxUser = "";
        int maxCount = 0;

        for (String user : attemptFrequency.keySet()) {

            int count = attemptFrequency.get(user);

            if (count > maxCount) {
                maxCount = count;
                maxUser = user;
            }
        }

        return maxUser + " (" + maxCount + " attempts)";
    }

    public static void main(String[] args) {

        UsernameChecker checker = new UsernameChecker();

        checker.registerUser("john_doe", 1);
        checker.registerUser("admin", 2);

        System.out.println("john_doe available? " +
                checker.checkAvailability("john_doe"));

        System.out.println("jane_smith available? " +
                checker.checkAvailability("jane_smith"));

        System.out.println("Suggestions for john_doe: " +
                checker.suggestAlternatives("john_doe"));

        checker.checkAvailability("admin");
        checker.checkAvailability("admin");
        checker.checkAvailability("admin");

        System.out.println("Most attempted: " +
                checker.getMostAttempted());
    }
}