package org.utils;

import java.util.Random;

/**
 * 序列生成
 * 
 * @author Administrator
 * 
 */
public final class SeqGenerator {

    /**
     * 随机串的长度
     */
    private final static int RANDOM_LENGTH = 8;

    /**
     * 随机字符串选择范围---数字
     */
    private final static char[] digitArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    /**
     * 随机字符串选择范围---数字+字母
     */
    private final static char[] charArray =
        {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
            'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G',
            'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    /**
     * 生成指定长度的随机数字串
     * 
     * @return
     */
    public final static String randomDigit() {
        return randomDigit(RANDOM_LENGTH);
    }

    /**
     * 生成指定长度的随机字符串
     * 
     * @return
     */
    public final static String randomString() {
        return randomString(RANDOM_LENGTH);
    }

    /**
     * 生成指定长度的随机字符串
     * 
     * @return
     */
    public final static String randomString(int length) {
        char[] c = new char[length];
        Random random = new Random();
        for (int i = 0; i < length; i++)
            c[i] = charArray[random.nextInt(charArray.length)];
        return new String(c);
    }

    /**
     * 生成指定长度的随机数字串
     * 
     * @return
     */
    public final static String randomDigit(int length) {
        char[] c = new char[length];
        Random random = new Random();
        for (int i = 0; i < length; i++)
            c[i] = digitArray[random.nextInt(digitArray.length)];
        return new String(c);
    }

    /**
     * 生成20位数字序列
     * 
     * @return
     */
    public static final synchronized Long createSeq() {
        return new Long(System.currentTimeMillis() + randomDigit(4));
    }

    /**
     * 生成6位手机验证码
     * 
     * @return
     */
    public static final synchronized String createVerifyCode() {
        return SeqGenerator.randomDigit(6);
    }

}
