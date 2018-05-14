package com.wx.b360.tool;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.tomcat.util.http.fileupload.disk.DiskFileItem;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import sun.misc.BASE64Decoder;

public class FileTool {
	//上传图片路径
	private final static String PATH = "/upload";
	
	
	/**
	 * 上传文件，spring boot
	 * @param file
	 * @param filePath
	 * @param fileName
	 * @throws Exception
	 */
	public static void uploadFile(byte[] file, String filePath, String fileName) throws Exception { 
        File targetFile = new File(filePath);  
        if(!targetFile.exists()){    
            targetFile.mkdirs();    
        }       
        FileOutputStream out = new FileOutputStream(filePath+PATH + "/"+fileName);
        out.write(file);
        out.flush();
        out.close();
    }
	
	/**
	 * 
	 * @param request
	 * @param fileUrl
	 * @return
	 */
	public static String uploadQianURL(HttpServletRequest request,String fileUrl) {
        //获取文件名，文件名实际上在URL中可以找到
        String fileName = UUID.randomUUID().toString()+System.currentTimeMillis() + ".jpg";
        //这里服务器上要将此图保存的路径
        String savePath = request.getServletContext().getRealPath(PATH);
        System.out.println();
        try {
            URL url = new URL(fileUrl);/*将网络资源地址传给,即赋值给url*/
            /*此为联系获得网络资源的固定格式用法，以便后面的in变量获得url截取网络资源的输入流*/
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            DataInputStream in = new DataInputStream(connection.getInputStream());
            
            File fileLocation = new File(savePath);
            if(!fileLocation.exists()) {
    			boolean isCreated  = fileLocation.mkdir();
    			if(!isCreated) {
    				//目标上传目录创建失败,可做其他处理,例如抛出自定义异常等,一般应该不会出现这种情况。
    				System.out.println("创建目录失败");
    				return null;
    			}
    		}
            
            /*此处也可用BufferedInputStream与BufferedOutputStream*/
            DataOutputStream out = new DataOutputStream(new FileOutputStream(savePath+"\\"+fileName));
            /*将参数savePath，即将截取的图片的存储在本地地址赋值给out输出流所指定的地址*/
            byte[] buffer = new byte[4096];
            int count = 0;
            /*将输入流以字节的形式读取并写入buffer中*/
            while ((count = in.read(buffer)) > 0) {
                out.write(buffer, 0, count);
            }
            out.close();/*后面三行为关闭输入输出流以及网络资源的固定格式*/
            in.close();
            connection.disconnect();
            //返回内容是文件名
            return fileName;/*网络资源截取并存储本地成功返回true*/

        } catch (Exception e) {
            System.out.println(e + fileUrl + savePath);
            return null;
        }
    }
	
	/**
	 * 上传图片
	 * @param base64 图片编码后的字符串
	 * @return 上传成功后的文件名
	 */
	public static String upload(HttpServletRequest request,String base64) {
		String dir = request.getServletContext().getRealPath(PATH);
		File fileLocation = new File(dir);
		if(!fileLocation.exists()) {
			boolean isCreated  = fileLocation.mkdir();
			if(!isCreated) {
				//目标上传目录创建失败,可做其他处理,例如抛出自定义异常等,一般应该不会出现这种情况。
				return null;
			}
		}
		if(base64.indexOf("jpeg") != -1) {
			//存在jpeg文件的情况
			base64 = base64.replaceFirst("jpeg", "jpg");
		}
		String upName = UUID.randomUUID().toString()+System.currentTimeMillis()+"."+base64.substring(11, 14);
		FileOutputStream out;
		String iconBase64 = base64.substring(22);
		try {
			byte[] buffer = new BASE64Decoder().decodeBuffer(iconBase64);
			out = new FileOutputStream(dir+"/"+upName);
			out.write(buffer);
			out.close();
			return upName;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
