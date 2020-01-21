/**   
 * 项目名称：	WebTemplet
 * 类名称：	MD5Util
 * 类描述：	MD5加密
 * 创建人：	Y.P
 * 创建时间：	2014年11月6日 上午10:51:06
 * 修改人：	Y.P
 * 修改时间：	2014年11月6日 上午10:51:06
 * 修改备注：
 * 版本：	v1.0
 */
package com.sa.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;

public class MD5Util {
	public static String MD5(String s) {
        try {
            byte[] btInput = s.getBytes("utf-8");
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            return byteArrayToHex(md);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

	public static String getMd5ByFileProperty(String fileProperty) {
		try {
			// 拿到一个MD5转换器（如果想要SHA1参数换成”SHA1”）
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			// 输入的字符串转换成字节数组
			byte[] inputByteArray = fileProperty.getBytes();
			// inputByteArray是输入字符串转换得到的字节数组
			messageDigest.update(inputByteArray);
			// 转换并返回结果，也是字节数组，包含16个元素
			byte[] resultByteArray = messageDigest.digest();
			// 字符数组转换成字符串返回
			return byteArrayToHex(resultByteArray);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String byteArrayToHex(byte[] byteArray) {
		// 首先初始化一个字符数组，用来存放每个16进制字符
		char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'A', 'B', 'C', 'D', 'E', 'F' };
		// new一个字符数组，这个就是用来组成结果字符串的（解释一下：一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2次方））
		char[] resultCharArray = new char[byteArray.length * 2];
		// 遍历字节数组，通过位运算（位运算效率高），转换成字符放到字符数组中去
		int index = 0;
		for (byte b : byteArray) {
			resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
			resultCharArray[index++] = hexDigits[b & 0xf];
		}
		// 字符数组组合成字符串返回
		return new String(resultCharArray);

	}

	public static String getMd5ByFile(File file) {
		String value = null;
		FileInputStream in = null;
		FileChannel ch = null;
		try {
			in = new FileInputStream(file);
			ch = in.getChannel();
			MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(byteBuffer);
			BigInteger bi = new BigInteger(1, md5.digest());
			value = bi.toString(16);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != ch) {
				try {
					ch.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return value.toUpperCase();
	}

	/**
	 * MD5校验
	 * @param request
	 * @param key
	 * @param strMd5
	 * @return
	 */
	public static boolean check(List<String> listSort, HashMap<String, String> hm, String key, String strMd5, HashMap<String, String> remove, StringBuffer log) {
		String str = "";
		for (String mkey : listSort) {
			if(null != remove.get(mkey) && !"".equals(remove.get(mkey))) continue;
			if (null == hm.get(mkey)) continue;
			str+=hm.get(mkey);
        }
		str+=key;
		String tempMd5 = MD5(str);

		if (tempMd5.equals(strMd5.toUpperCase())) {
			return true;
		}

		log.append("MD5(").append(str).append(") = ").append(tempMd5.toLowerCase()).append("\r\n");
		return false;
	}

//	public static void main(String[] args) {
//		System.out.print(MD5("b7f5c76353f3421fa752cf9888edf8f7123456789047600.0"));
//    }

}
