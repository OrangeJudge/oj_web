package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StringHashing {
    public static String sha1(String origin) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
        }
        catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        assert md != null;
        return byteArrayToHexString(md.digest(origin.getBytes()));
    }

    private static String byteArrayToHexString(byte[] b) {
        String result = "";
        for (byte aB : b) {
            result += Integer.toString((aB & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }
}
