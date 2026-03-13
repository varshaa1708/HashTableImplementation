import java.time.*;
class ParkingSpot {
    enum Status { EMPTY, OCCUPIED, DELETED }
    String licensePlate;
    Status status;
    LocalDateTime entryTime;
    ParkingSpot() {
        status = Status.EMPTY;
    }
}
public class ParkingLot {
    private ParkingSpot[] table;
    private int capacity = 500;
    private int size = 0;
    private int totalProbes = 0;
    private int totalParks = 0;
    public ParkingLot() {
        table = new ParkingSpot[capacity];
        for (int i = 0; i < capacity; i++) {
            table[i] = new ParkingSpot();
        }
    }
    private int hash(String licensePlate) {
        return Math.abs(licensePlate.hashCode()) % capacity;
    }
    public void parkVehicle(String plate) {
        int index = hash(plate);
        int probes = 0;
        while (table[index].status == ParkingSpot.Status.OCCUPIED) {
            index = (index + 1) % capacity;
            probes++;
        }
        table[index].licensePlate = plate;
        table[index].status = ParkingSpot.Status.OCCUPIED;
        table[index].entryTime = LocalDateTime.now();
        size++;
        totalProbes += probes;
        totalParks++;
        System.out.println("Assigned spot #" + index + " (" + probes + " probes)");
    }
    public void exitVehicle(String plate) {
        int index = hash(plate);
        while (table[index].status != ParkingSpot.Status.EMPTY) {
            if (plate.equals(table[index].licensePlate)) {
                LocalDateTime exitTime = LocalDateTime.now();
                Duration duration = Duration.between(table[index].entryTime, exitTime);
                double hours = duration.toMinutes() / 60.0;
                double fee = hours * 5.5;
                table[index].status = ParkingSpot.Status.DELETED;
                table[index].licensePlate = null;
                size--;
                System.out.println("Spot #" + index + " freed");
                System.out.println("Duration: " + duration.toMinutes() + " minutes");
                System.out.println("Fee: $" + String.format("%.2f", fee));
                return;
            }
            index = (index + 1) % capacity;
        }
        System.out.println("Vehicle not found");
    }
    public void getStatistics() {
        double occupancy = (size * 100.0) / capacity;
        double avgProbes = totalParks == 0 ? 0 : (double) totalProbes / totalParks;
        System.out.println("Occupancy: " + String.format("%.2f", occupancy) + "%");
        System.out.println("Average Probes: " + String.format("%.2f", avgProbes));
    }
    public static void main(String[] args) {
        ParkingLot lot = new ParkingLot();
        lot.parkVehicle("ABC-1234");
        lot.parkVehicle("ABC-1235");
        lot.parkVehicle("XYZ-9999");
        lot.exitVehicle("ABC-1234");
        lot.getStatistics();
    }
}