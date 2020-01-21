package com.sa.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Encoder;

public class HMACSHA1Util {
	/**
     * HMAC-SHA1签名
     *
      * @param message
     * @param key
     * @return
     */
     public static String getHmacSHA1(String message, String key) {
         String hmacSha1 = null;
         try {
             // url encode
             message = URLEncoder.encode(message, "UTF-8");
             // hmac-sha1加密
             Mac mac = Mac.getInstance("HmacSHA1");
             SecretKeySpec spec = new SecretKeySpec(key.getBytes(), "HmacSHA1");
             mac.init(spec);
             byte[] byteHMAC = mac.doFinal(message.getBytes());
             // base64 encode
             hmacSha1 = new BASE64Encoder().encode(byteHMAC);
         } catch (NoSuchAlgorithmException e) {
         } catch (InvalidKeyException e) {
         } catch (UnsupportedEncodingException e) {
         }
         return hmacSha1;
     }
}
