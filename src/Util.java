import java.util.Random;

public class Util {

    public static Random rand = new Random();
    public static String atoz = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static String genString(int length) {
        StringBuilder re = new StringBuilder(length);
        re.append(atoz.charAt(rand.nextInt(52)));
        for (int i = 0; i < length; i++) {
            re.append(atoz.charAt(rand.nextInt(62)));
        }
        return re.toString();
    }

    public static double genDouble() {
        double d1 = 5120 * rand.nextDouble();
        double d2 = 1024000 * rand.nextDouble();
        return d1 + d2;
    }
}