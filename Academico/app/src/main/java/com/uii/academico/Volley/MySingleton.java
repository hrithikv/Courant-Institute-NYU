public class MySingleton {
    private static MySingleton instance;

    public static MySingleton getInstance() {
        if (instance == null) {
            instance = new MySingleton();
        }
        return instance;
    }

    private int x;

    public int incrementX() {
        return ++x;
    }
}
