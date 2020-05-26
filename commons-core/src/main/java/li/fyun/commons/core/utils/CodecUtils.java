package li.fyun.commons.core.utils;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;

public final class CodecUtils {

    private static String sKey = "1iIHtFoCRUSEYWti";// 加密用的Key 可以用26个字母和数字组成 此处使用AES-128-CBC加密模式，key需要为16位。
    private static String ivParameter = "1045654454730388";//偏移量

    public static String base64Encode(String data) {
        return BaseEncoding.base64().encode(data.getBytes());
    }

    public static String base64Decode(String data) throws UnsupportedEncodingException {
        return new String(BaseEncoding.base64().decode(data), Charsets.UTF_8);
    }

    public static String sha256Hex(String data) {
        return Hashing.sha256().hashString(data, Charsets.UTF_8).toString();
    }

    public static String sha512Hex(String data) {
        return Hashing.sha512().hashString(data, Charsets.UTF_8).toString();
    }

    public static String passwordEncode(String password, String salt, int iteration) {
        String encoded = password;
        for (int i = 0; i < iteration; i++) {
            encoded = sha512Hex(encoded + salt);
        }
        return encoded;
    }

    public static String aesEncode(String sSrc) throws Exception {
        return aesEncode(sSrc, sKey);
    }

    public static String aesDecode(String sSrc) throws Exception {
        return aesDecode(sSrc, sKey);
    }

    public static String aesEncode(String sSrc, String salt) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] raw = salt.getBytes(Charsets.UTF_8);
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes(Charsets.UTF_8));// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes(Charsets.UTF_8));
        return BaseEncoding.base64().encode(encrypted);// 此处使用BASE64做转码。
    }

    public static String aesDecode(String sSrc, String salt) throws Exception {
        byte[] raw = salt.getBytes("ASCII");
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes(Charsets.UTF_8));
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
        byte[] encrypted1 = BaseEncoding.base64().decode(sSrc);// 先用base64解密
        byte[] original = cipher.doFinal(encrypted1);
        return new String(original, Charsets.UTF_8);
    }

}
