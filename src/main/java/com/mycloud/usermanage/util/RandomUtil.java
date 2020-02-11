package com.mycloud.usermanage.util;

import org.apache.commons.lang.RandomStringUtils;

import java.util.UUID;

public class RandomUtil {
    public static final Integer MDC_LEN = 18;
    public static final String MDC_KEY = "invokeNo";

    /**
     * 用于标识日志记录
     * @return
     */
    public static String getMDC() {
        return UUID.randomUUID().toString().substring(0, MDC_LEN);
    }

    /**
     * 返回32位唯一标识
     * @return
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成随机字母
     * @param count 随机字符串长度
     * @return
     */
    public static String randomAlphabetic(int count) {
        return RandomStringUtils.randomAlphabetic(count);
    }

    /**
     * 生成随机数字
     * @param count 随机字符串长度
     * @return
     */
    public static String randomNumeric(int count) {
        return RandomStringUtils.randomNumeric(count);
    }

    /**
     * 生成随机字母和数字
     * @param count 随机字符串长度
     * @return
     */
    public static String randomAlphanumeric(int count) {
        return RandomStringUtils.randomAlphanumeric(count);
    }

    /**
     * 根据指定的字符生成随机字符串
     * @param count 随机字符串长度
     * @param chars 指定的字符
     * @return
     */
    public static String random(int count, String chars) {
        return RandomStringUtils.random(count, chars);
    }
    
    public static void main(String[] args) {
        System.out.println(getMDC());
        System.out.println(getUUID());
        System.out.println(randomAlphabetic(12));
        System.out.println(randomNumeric(12));
        System.out.println(randomAlphanumeric(12));
        System.out.println(random(12,"123"));
    }
}