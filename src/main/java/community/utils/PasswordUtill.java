package community.utils;

import java.util.Random;

public class PasswordUtill {
    public static String generateTempPwd() {
        String source = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int idx = random.nextInt(source.length());
            sb.append(source.charAt(idx));
        }
        return sb.toString();
    }
}