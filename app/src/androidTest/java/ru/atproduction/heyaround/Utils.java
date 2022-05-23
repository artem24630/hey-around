package ru.atproduction.heyaround;

import java.util.Random;

public class Utils {
    private static final Random rnd = new Random();

    static String generateString()
    {
        char[] chars = new char[rnd.nextInt(9)];
        for (int i = 0; i < chars.length; i++)
        {
            chars[i] = (char) (rnd.nextInt('z' - 'a') + 'a');
        }
        return new String(chars);
    }
}
