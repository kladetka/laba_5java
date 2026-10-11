import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n===== LAB 5. VARIANT 10 =====");
            System.out.println("1 - Завдання 1: Generic StorageManager");
            System.out.println("2 - Завдання 2: списки слів з файлів");
            System.out.println("3 - Завдання 3: Map довжина слова -> унікальні слова");
            System.out.println("4 - Завдання 4: базові Stream API запити");
            System.out.println("5 - Завдання 5: групування, join та агрегація");
            System.out.println("0 - Вихід");
            System.out.print("Ваш вибір: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": task1(); break;
                case "2": task2(); break;
                case "3": task3(); break;
                case "4": task4(); break;
                case "5": task5(); break;
                case "0": System.out.println("Завершення програми."); return;
                default: System.out.println("Невірний пункт меню.");
            }
        }
    }

    // Сутність для варіанта 10
    static class HotelRoom {
        int roomNumber;
        int hotelId;
        int capacity;
        double pricePerNight;
        boolean hasSeaView;
        boolean isCleaned;
        boolean isAvailable;
        String classType;

        HotelRoom(int roomNumber, int hotelId, int capacity, double pricePerNight,
                  boolean hasSeaView, boolean isCleaned, boolean isAvailable, String classType) {
            this.roomNumber = roomNumber;
            this.hotelId = hotelId;
            this.capacity = capacity;
            this.pricePerNight = pricePerNight;
            this.hasSeaView = hasSeaView;
            this.isCleaned = isCleaned;
            this.isAvailable = isAvailable;
            this.classType = classType;
        }

        @Override
        public String toString() {
            return "Кімната №" + roomNumber + ", готель ID=" + hotelId +
                    ", місткість=" + capacity + ", ціна=" + pricePerNight +
                    ", вид на море=" + hasSeaView + ", прибрана=" + isCleaned +
                    ", вільна=" + isAvailable + ", клас=" + classType;
        }
    }

    // Завдання 1: узагальнене сховище. Фільтрація виконується звичайним циклом.
    static class StorageManager<T> {
        private final List<T> items = new ArrayList<>();

        public void add(T item) { items.add(item); }

        public boolean remove(T item) { return items.remove(item); }

        public T find(Predicate<T> condition) {
            for (T item : items) {
                if (condition.test(item)) return item;
            }
            return null;
        }

        public List<T> filter(Predicate<T> condition) {
            List<T> result = new ArrayList<>();
            for (T item : items) {
                if (condition.test(item)) result.add(item);
            }
            return Collections.unmodifiableList(result);
        }

        public List<T> getAll() {
            return Collections.unmodifiableList(items);
        }
    }

    static List<HotelRoom> sampleRooms() {
        List<HotelRoom> rooms = new ArrayList<>();
        rooms.add(new HotelRoom(101, 1, 2, 2200, true, true, true, "Standard"));
        rooms.add(new HotelRoom(102, 1, 1, 1800, false, true, true, "Standard"));
        rooms.add(new HotelRoom(201, 1, 3, 2800, true, false, false, "Suite"));
        rooms.add(new HotelRoom(202, 1, 4, 3500, true, true, true, "Suite"));
        rooms.add(new HotelRoom(301, 2, 2, 2500, false, true, true, "Deluxe"));
        rooms.add(new HotelRoom(302, 2, 2, 2400, true, true, false, "Deluxe"));
        rooms.add(new HotelRoom(401, 2, 5, 5000, true, false, true, "Suite"));
        rooms.add(new HotelRoom(402, 2, 3, 2900, false, true, true, "Standard"));
        rooms.add(new HotelRoom(501, 3, 2, 2000, true, true, true, "Economy"));
        rooms.add(new HotelRoom(502, 3, 6, 6000, true, true, false, "Suite"));
        rooms.add(new HotelRoom(503, 3, 4, 2600, false, true, true, "Deluxe"));
        rooms.add(new HotelRoom(504, 3, 2, 2300, true, false, true, "Standard"));
        return rooms;
    }

    static void task1() {
        StorageManager<HotelRoom> storage = new StorageManager<>();
        for (HotelRoom room : sampleRooms()) storage.add(room);

        System.out.println("\nЗавдання 1. Усі номери:");
        storage.getAll().forEach(System.out::println);

        System.out.println("\nПошук кімнати за номером 301:");
        HotelRoom found = storage.find(r -> r.roomNumber == 301);
        System.out.println(found == null ? "Номер не знайдено." : found);

        System.out.println("\nФільтр 1: capacity >= 2");
        storage.filter(r -> r.capacity >= 2).forEach(System.out::println);

        System.out.println("\nФільтр 2: pricePerNight <= 3000 AND hasSeaView == true");
        storage.filter(r -> r.pricePerNight <= 3000 && r.hasSeaView).forEach(System.out::println);

        System.out.println("\nФільтр 3: isCleaned == true OR classType == \"Suite\"");
        storage.filter(r -> r.isCleaned || r.classType.equals("Suite")).forEach(System.out::println);
    }

    // Завдання 2: у L1 видаляються всі слова, які є також у L2.
    // Формат input_l1.txt та input_l2.txt: слова, розділені пробілами або новими рядками.
    static void task2() throws IOException {
        List<String> l1 = readWords("input_l1.txt");
        List<String> l2 = readWords("input_l2.txt");
        Set<String> wordsToRemove = new HashSet<>(l2);
        l1.removeIf(wordsToRemove::contains);

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("output_l1.txt"))) {
            for (int i = 0; i < l1.size(); i++) {
                writer.write(l1.get(i));
                if ((i + 1) % 4 == 0 || i == l1.size() - 1) writer.newLine();
                else writer.write(" ");
            }
        }
        System.out.println("Початковий L1: " + readWords("input_l1.txt"));
        System.out.println("L2: " + l2);
        System.out.println("Модифікований L1: " + l1);
        System.out.println("Результат записано у output_l1.txt (по 4 слова в рядку).");
    }

    static List<String> readWords(String fileName) throws IOException {
        String content = Files.readString(Paths.get(fileName));
        if (content.trim().isEmpty()) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(content.trim().split("\\s+")));
    }

    // Завдання 3: ключ — довжина слова, значення — список унікальних слів такої довжини.
    static void task3() throws IOException {
        List<String> words = readWords("input_words.txt");
        Map<Integer, List<String>> grouped = new TreeMap<>();
        for (String word : words) {
            int length = word.length();
            grouped.putIfAbsent(length, new ArrayList<>());
            if (!grouped.get(length).contains(word)) grouped.get(length).add(word);
        }

        System.out.println("\nЗавдання 3. Map<довжина, список унікальних слів>:");
        for (Map.Entry<Integer, List<String>> entry : grouped.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
        System.out.println("TreeMap автоматично сортує ключі (довжини) за зростанням.");
    }

    static void task4() {
        List<HotelRoom> rooms = sampleRooms();

        System.out.println("\nЗавдання 4.1. Місткість >= 2 і ціна <= 2500:");
        rooms.stream()
                .filter(r -> r.capacity >= 2 && r.pricePerNight <= 2500)
                .forEach(System.out::println);

        System.out.println("\nЗавдання 4.2. Сортування за класом зростанням, ціною спаданням; виводимо номер кімнати:");
        rooms.stream()
                .sorted(Comparator.comparing((HotelRoom r) -> r.classType)
                        .thenComparing(Comparator.comparingDouble((HotelRoom r) -> r.pricePerNight).reversed()))
                .map(r -> String.valueOf(r.roomNumber))
                .forEach(System.out::println);

        System.out.println("\nЗавдання 4.3. Пропустити перші 2 номери, взяти наступні 3:");
        rooms.stream().skip(2).limit(3).forEach(System.out::println);

        System.out.println("\nЗавдання 4.4. Перший вільний номер із видом на море:");
        Optional<HotelRoom> first = rooms.stream()
                .filter(r -> r.isAvailable && r.hasSeaView)
                .findFirst();
        System.out.println(first.map(Object::toString).orElse("Відповідного номера не знайдено."));

        System.out.println("\nЗавдання 4.5. Номер із максимальною місткістю:");
        Optional<HotelRoom> largest = rooms.stream()
                .max(Comparator.comparingInt(r -> r.capacity));
        System.out.println(largest.map(Object::toString).orElse("Список порожній."));
    }

    static class Hotel {
        int id;
        String name;
        String city;

        Hotel(int id, String name, String city) {
            this.id = id;
            this.name = name;
            this.city = city;
        }
    }

    static void task5() {
        List<HotelRoom> rooms = sampleRooms();
        List<Hotel> hotels = Arrays.asList(
                new Hotel(1, "Sea Pearl", "Одеса"),
                new Hotel(2, "Comfort", "Київ"),
                new Hotel(3, "Blue Coast", "Затока"),
                new Hotel(4, "Mountain Rest", "Яремче")
        );

        System.out.println("\nЗавдання 5.1. Кількість номерів у кожному готелі:");
        Map<Integer, Long> counts = rooms.stream()
                .collect(Collectors.groupingBy(r -> r.hotelId, TreeMap::new, Collectors.counting()));
        counts.forEach((id, count) -> System.out.println("Готель ID=" + id + ": " + count + " номер(ів)"));

        System.out.println("\nЗавдання 5.2. Середня ціна за ніч у кожному готелі:");
        Map<Integer, Double> averages = rooms.stream()
                .collect(Collectors.groupingBy(r -> r.hotelId, TreeMap::new,
                        Collectors.averagingDouble(r -> r.pricePerNight)));
        averages.forEach((id, avg) -> System.out.printf("Готель ID=%d: %.2f%n", id, avg));

        System.out.println("\nЗавдання 5.3. Inner Join: кімнати разом із назвою готелю:");
        rooms.stream()
                .flatMap(room -> hotels.stream()
                        .filter(hotel -> hotel.id == room.hotelId)
                        .map(hotel -> "Кімната №" + room.roomNumber + ", ціна=" +
                                room.pricePerNight + ", готель=" + hotel.name))
                .forEach(System.out::println);

        System.out.println("\nЗавдання 5.4. Для кожного готелю — список усіх його номерів (у тому числі порожній):");
        hotels.forEach(hotel -> {
            List<Integer> hotelRoomNumbers = rooms.stream()
                    .filter(room -> room.hotelId == hotel.id)
                    .map(room -> room.roomNumber)
                    .collect(Collectors.toList());
            System.out.println(hotel.name + " (" + hotel.city + "): " + hotelRoomNumbers);
        });

        System.out.println("\nЗавдання 5.5. Загальна місткість усіх номерів:");
        int totalCapacity = rooms.stream().mapToInt(room -> room.capacity).sum();
        System.out.println("Загальна кількість місць: " + totalCapacity);
    }
}
