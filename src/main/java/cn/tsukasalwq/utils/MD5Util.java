package cn.tsukasalwq.utils;

import java.security.MessageDigest;

public class MD5Util {
    private static String byteArrayToHexString(byte[] bytes) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            resultSb.append(byteToHexString(bytes[i]));
        }

        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0) {
            n += 256;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1]+hexDigits[d2];
    }

    /**
     * 返回大写MD5
     * @param origin
     * @param charsetname
     * @return
     */
    private static String MD5Encode(String origin, String charsetname) {
        String resultString = null;
        try {
            resultString = new String(origin);
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (charsetname == null || "".equals(charsetname)) {
                resultString = byteArrayToHexString(resultString.getBytes());
            } else {
                resultString = byteArrayToHexString(md.digest(resultString.getBytes(charsetname)));
            }
        } catch (Exception e) {

        }
        return resultString.toUpperCase();
    }

    public static String MD5EncodeUtf8(String origin) {
        // md5加盐值的应用，提高破解难度
        origin = origin + PropertiesUtil.getProperty("password.salt", "tsukasalwq");
        return MD5Encode(origin, "utf-8");
    }


    private static final String[] hexDigits = {
            "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b",
            "c", "d", "e", "f"
    };
}
