package com.wx.b360.tool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;


public class HttpTool {

	/**
	 * 将对象写入输出流
	 * @param response
	 * @param object
	 */
	public static void jsonMsg(HttpServletResponse response, Object object) {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("application/json; charset=UTF-8");
		PrintWriter out;
		try {
			out = response.getWriter();
			out.write(JSON.toJSONString(object));
			out.flush();
			out.close();
		} catch (IOException e) {
			System.out.println("IO输出异常");
			e.printStackTrace();
		}
	}

	/**
	 * 发送get请求
	 * @param url
	 * @return
	 */
	public static String get(String url) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		HttpEntity<String> entity = new HttpEntity<String>(url, headers);
		ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		return responseEntity.getBody();
	}

	public static String post(String url, Map<String, String> params) {
		StringHttpMessageConverter m = new StringHttpMessageConverter(Charset.forName("UTF-8"));
		RestTemplate restTemplate=new RestTemplateBuilder().additionalMessageConverters(m).build();
		/* 注意：必须 http、https……开头，不然报错，浏览器地址栏不加 http 之类不出错是因为浏览器自动帮你补全了 */
		HttpHeaders headers = new HttpHeaders();
		/* 这个对象有add()方法，可往请求头存入信息 */       
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		/* 解决中文乱码的关键 , 还有更深层次的问题 关系到 StringHttpMessageConverter，先占位，以后补全*/ 
		HttpEntity<String> entity = new HttpEntity<String>(JSON.toJSONString(params), headers);
		/* body是Http消息体例如json串 */ 
		ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
		
		/*上面这句返回的是往 url发送 post请求 请求携带信息为entity时返回的结果信息
		String.class 是可以修改的，例如User.class，这样你就可以有 User resultUser接返回结果了*/
		
		return result.getBody();
	}

	public static String POSTWithMap(String url, Map<String, String> params) {
		URL u = null;
		HttpURLConnection con = null;
		//构建请求参数
		StringBuffer sb = new StringBuffer();
		if(params!=null){
			for (Entry<String, String> e : params.entrySet()) {
				sb.append(e.getKey());
				sb.append("=");
				sb.append(e.getValue());
				sb.append("&");
			}
			sb.substring(0, sb.length() - 1);
		}


		//尝试发送请求
		try {
			u = new URL(url);
			con = (HttpURLConnection) u.openConnection();
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setUseCaches(false);
			//con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			//con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			con.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
			con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

			OutputStreamWriter osw = new OutputStreamWriter(con.getOutputStream(), "UTF-8");
			osw.write(sb.toString());
			osw.flush();
			osw.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}


		//读取返回内容
		StringBuffer buffer = new StringBuffer();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(con
					.getInputStream(), "UTF-8"));
			String temp;
			while ((temp = br.readLine()) != null) {
				buffer.append(temp);
				buffer.append("\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return buffer.toString();
	}

}
