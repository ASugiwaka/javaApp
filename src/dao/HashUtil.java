package dao;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/** 
 * 暗号化用クラス
 * @author 杉若
 */
public class HashUtil {
	 /**
	  * SHA-256で引数で渡された文字列をハッシュ化して返す
	  * @param input - 暗号化前文字列
	  * @return hexString - 暗号化後文字列
   	 */
	public static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
	}
}
