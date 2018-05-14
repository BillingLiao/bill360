package com.wx.b360.tool;

import java.math.BigDecimal;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;


import com.alibaba.fastjson.JSON;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * token处理
 * @author 莫小雨
 *
 */
@SuppressWarnings("restriction")
public class TokenTool {
	//加密关键字
	private static final String DESkeyStr = "9Qk7T1mBkTvHewcOkgcEGwFujv8mrqVhLjFgGDW2AZMNfihmO26dVoeiA2ynFLSG9tGHJCLwhXpBZqZ9yKMganqTTlyWOneqO0r4MrcgJZf4hufo6a0dzppRDGTJcFShma7q74NcwOCl4LqylaGyrQWXpfHWKawMMM5QBYfZEjbwBXvdGPca2s4FkC5lYGCE4xAkXnPBRod3b2rNV4ihMMog5x6k8q3H6bfxvIP52iUr49xJRX5WpONDrS05Vgxj";
	private static final byte[] DESIV = {0x12, 0x34, 0x56, 0x78, (byte) 0x90, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF};// 设置向量，略去
	//验证token用的key
	private final static String KEY = "bill360"; 
	private final static String KEY_ADMIN = "bill360";
	private final static long INTERVAL = Long.parseLong("86400000");//有效期（毫秒）-1天

	/**
	 * 生成token
	 * @param userId
	 * @return
	 */
	public static String create(long userId) {
//		String str = "{\"id\":"+userId+",\"time\":"+System.currentTimeMillis()+",\"key\":\""+KEY+"\"}";
		String str = "{\""+getRandomString((int)(Math.random()*10))+"\":\""+getRandomString((int)(Math.random()*10))+"\",\"id\":"+userId+",\"time\":"+System.currentTimeMillis()+",\"key\":\""+KEY+"\"}";
		return encode(str).replaceAll("\r|\n", "");
	}
	
	/**
	 * |生成atoken
	 * @param adminId
	 * @return
	 */
	public static String createA(long adminId) {
		String str = "{\""+getRandomString((int)(Math.random()*10))+"\":\""+getRandomString((int)(Math.random()*10))+"\",\"id\":"+adminId+",\"time\":"+System.currentTimeMillis()+",\"key\":\""+KEY_ADMIN+"\"}";
		return encode(str).replaceAll("\r|\n", "");
	}
	
	public static String createWallet(long userId, int amount) {
		String money = amount + "";//初始化钱
		String str = "{\""+getRandomString((int)(Math.random()*4))+"\":\""+getRandomString((int)(Math.random()*4))+"\",\"id\":\""+userId+"\",\""+getRandomString((int)(Math.random()*4))+"\":\""+getRandomString((int)(Math.random()*4))+"\",\"money\":\""+money+"\",\""+getRandomString((int)(Math.random()*4))+"\":\""+getRandomString((int)(Math.random()*4))+"\"}";
		return encode(str).replaceAll("\r|\n", "");
	}
	
	/**
	 * 验证wallet
	 * @param wallet值
	 * @return 大于零说明无误，返回的是用户的金额
	 * 等于零说明id有误，所属环境不对，等于-1说明key验证错误，应该是token有误
	 */
	public static BigDecimal verifyWallet(String token, int userId) {
		String jsonStr = decode(token);
		com.alibaba.fastjson.JSONObject jsonObject =  JSON.parseObject(jsonStr);
		try {
			int id = jsonObject.getIntValue("id");
			BigDecimal money = jsonObject.getBigDecimal("money");
			if(userId == id) {
				return money;
			} else {
				return null;
			}

		} catch (NullPointerException e) {
			return null;
		}
	}
	
	/**
	 * 增加
	 * @param token充值之前的加密信息
	 * @param money需要充值的内容
	 * @return
	 */
	public static String increase(String token, BigDecimal money, int userId) {
		String jsonStr = decode(token);
		com.alibaba.fastjson.JSONObject jsonObject =  JSON.parseObject(jsonStr);
		try {
			BigDecimal moneyNow = jsonObject.getBigDecimal("money");
			int id = jsonObject.getIntValue("id");
			if(id == userId) {
				moneyNow = moneyNow.add(money);
				String str = "{\""+getRandomString((int)(Math.random()*4))+"\":\""+getRandomString((int)(Math.random()*4))+"\",\"id\":\""+userId+"\",\""+getRandomString((int)(Math.random()*4))+"\":\""+getRandomString((int)(Math.random()*4))+"\",\"money\":\""+moneyNow+"\",\""+getRandomString((int)(Math.random()*4))+"\":\""+getRandomString((int)(Math.random()*4))+"\"}";
				return encode(str).replaceAll("\r|\n", "");
			} else {
				return null;
			}
		} catch (NullPointerException e) {
			return null;
		}
	}
	
	/**
	 * 减少
	 * @param token充值之前的加密信息
	 * @param money需要充值的内容
	 * @return
	 */
	public static String reduce(String token, BigDecimal money, int userId) {
		String jsonStr = decode(token);
		com.alibaba.fastjson.JSONObject jsonObject =  JSON.parseObject(jsonStr);
		try {
			BigDecimal moneyNow = jsonObject.getBigDecimal("money");
			int id = jsonObject.getIntValue("id");
			if(id == userId) {
				moneyNow = moneyNow.subtract(money);
				String str = "{\""+getRandomString((int)(Math.random()*4))+"\":\""+getRandomString((int)(Math.random()*4))+"\",\"id\":\""+userId+"\",\""+getRandomString((int)(Math.random()*4))+"\":\""+getRandomString((int)(Math.random()*4))+"\",\"money\":\""+moneyNow+"\",\""+getRandomString((int)(Math.random()*4))+"\":\""+getRandomString((int)(Math.random()*4))+"\"}";
				return encode(str).replaceAll("\r|\n", "");
			} else {
				return null;
			}
		} catch (NullPointerException e) {
			return null;
		}
	}
	
	/**
	 * 验证atoken
	 * @param token
	 * @return 大于零说明时间和key都是对的，返回的是用户的id
	 * 等于零说明超时，请重新获取token，等于-1说明key验证错误，应该是token有误
	 */
	public static int verifyA(String token) {
		String jsonStr = decode(token);
		com.alibaba.fastjson.JSONObject jsonObject =  JSON.parseObject(jsonStr);
		try {
			int id = jsonObject.getIntValue("id");
			Long time = jsonObject.getLongValue("time");
			String key = jsonObject.getString("key");
			

			//验证key是否正确
			if(key.equals(TokenTool.KEY_ADMIN)) {
				//验证时间是否过期
				if(System.currentTimeMillis() - time < INTERVAL) {
					return id;
				} else {
					//token超时
					return 0;
				}
			} else {
				//key验证错误
				return -1;
			}
		} catch (NullPointerException e) {
			return -1;
		}
	}

	/**
	 * 验证token
	 * @param token
	 * @return 大于零说明时间和key都是对的，返回的是用户的id
	 * 等于零说明超时，请重新获取token，等于-1说明key验证错误，应该是token有误
	 */
	public static int verify(String token) {
		String jsonStr = decode(token);
		try {
			com.alibaba.fastjson.JSONObject jsonObject =  JSON.parseObject(jsonStr);
			int id = jsonObject.getIntValue("id");
			Long time = jsonObject.getLongValue("time");
			String key = jsonObject.getString("key");

			//验证key是否正确
			if(key.equals(TokenTool.KEY)) {
				//验证时间是否过期
				if(System.currentTimeMillis() - time < INTERVAL) {
					return id;
				} else {
					//token超时
					return 0;
				}
			} else {
				//key验证错误
				return -1;
			}
		} catch (Exception e) {
			//json验证错误
			return -1;
		}
	}



	/**
	 * 加密
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static String encode(String data) {
		try {
			final byte[] DESkey = DESkeyStr.getBytes("UTF-8");// 设置密钥，略去
			DESKeySpec keySpec = new DESKeySpec(DESkey);// 设置密钥参数
			AlgorithmParameterSpec iv = new IvParameterSpec(DESIV);;// 加密算法的参数接口，IvParameterSpec是它的一个实现
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");// 获得密钥工厂
			Key key = keyFactory.generateSecret(keySpec);// 得到密钥对象
			Cipher enCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");// 得到加密对象Cipher
			enCipher.init(Cipher.ENCRYPT_MODE, key, iv);// 设置工作模式为加密模式，给出密钥和向量
			byte[] pasByte = enCipher.doFinal(data.getBytes("utf-8"));
			BASE64Encoder base64Encoder = new BASE64Encoder();
			return base64Encoder.encode(pasByte);
		} catch(Exception e) {
			System.out.println("加密失败");
			System.out.println(e);
			return null;
		}
	}

	/**
	 * 解密
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static String decode(String data) {
		try {
			final byte[] DESkey = DESkeyStr.getBytes("UTF-8");// 设置密钥，略去
			DESKeySpec keySpec = new DESKeySpec(DESkey);// 设置密钥参数
			AlgorithmParameterSpec iv = new IvParameterSpec(DESIV);// 设置向量
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");// 获得密钥工厂
			Key key = keyFactory.generateSecret(keySpec);// 得到密钥对象
			Cipher deCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			deCipher.init(Cipher.DECRYPT_MODE, key, iv);
			BASE64Decoder base64Decoder = new BASE64Decoder();

			byte[] pasByte = deCipher.doFinal(base64Decoder.decodeBuffer(data));
			return new String(pasByte, "UTF-8");
		} catch (Exception e) {
			System.out.println("解密错误");
			System.out.println(e.getMessage());
			e.printStackTrace();
			return null;
		}

	}
	
	//获取指定位数的随机字符串(包含小写字母、大写字母、数字,0<length)
	public static String getRandomString(int length) {
	    //随机字符串的随机字符库
	    String KeyString = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	    StringBuffer sb = new StringBuffer();
	    int len = KeyString.length();
	    for (int i = 0; i < length; i++) {
	       sb.append(KeyString.charAt((int) Math.round(Math.random() * (len - 1))));
	    }
	    return sb.toString();
	}
}
