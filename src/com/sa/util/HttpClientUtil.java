/**
 * 项目名称：	WebTemplet
 * 类名称：	CustomizedHttpClient
 * 类描述：
 * 创建人：	Y.P
 * 创建时间：	2015年3月23日 上午9:56:56
 * 修改人：	Y.P
 * 修改时间：	2015年3月23日 上午9:56:56
 * 修改备注：
 * 版本：	v1.0
 */
package com.sa.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class HttpClientUtil {

	/**
     * 处理get请求.
     * @param url  请求路径
     * @return  json
     */
    public static String get(String url){
        //实例化httpclient
        CloseableHttpClient httpclient = HttpClients.createDefault();
        //实例化get方法
        HttpGet httpget = new HttpGet(url);
        //请求结果
        CloseableHttpResponse response = null;
        String content ="";
        try {
            //执行get方法
            response = httpclient.execute(httpget);
//            System.out.println("HTTP return code: "+response.getStatusLine().getStatusCode());
            if(response.getStatusLine().getStatusCode()==200){
                content = EntityUtils.toString(response.getEntity(),"utf-8");
//                System.out.println(content);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }
    /**
     * 处理post请求.
     * @param url  请求路径
     * @param params  参数
     * @return  json
     */
    public static String post(String url,Map<String, String> params) {
        //实例化httpClient
        CloseableHttpClient httpclient = HttpClients.createDefault();
        //实例化post方法
        HttpPost httpPost = new HttpPost(url);
        //处理参数
        List<NameValuePair> nvps = new ArrayList <NameValuePair>();
        Set<String> keySet = params.keySet();
        for(String key : keySet) {
            nvps.add(new BasicNameValuePair(key, params.get(key)));
        }
        //结果
        CloseableHttpResponse response = null;
        String content="";
        try {
            //提交的参数
            UrlEncodedFormEntity uefEntity  = new UrlEncodedFormEntity(nvps, "UTF-8");
            //将参数给post方法
            httpPost.setEntity(uefEntity);
            //执行post方法
            response = httpclient.execute(httpPost);
            if(response.getStatusLine().getStatusCode()==200){
                content = EntityUtils.toString(response.getEntity(),"utf-8");
//                System.out.println(content);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }
    public static String post(String url, String xml) {
    	//实例化httpClient
        CloseableHttpClient httpclient = HttpClients.createDefault();
        //实例化post方法
        HttpPost httpPost = new HttpPost(url);

        //结果
        CloseableHttpResponse response = null;
        String content="";
        try {
        	//处理参数
            StringEntity entity = new StringEntity(xml,"utf-8");//	中文乱码问题
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");
            httpPost.setEntity(entity);

            //执行post方法
            response = httpclient.execute(httpPost);
//            System.out.println("HTTP return code: "+response.getStatusLine().getStatusCode());
//	        if(response.getStatusLine().getStatusCode()==302){
//		        String locationUrl=response.getLastHeader("Location").getValue();
//		        System.out.println("locationUrl : " + locationUrl);
//		        content =get("http://student.valuelearning.net"+locationUrl);//跳转到重定向的url
//		        post(url,xml);
//	        }
            if(response.getStatusLine().getStatusCode()==200){
                content = EntityUtils.toString(response.getEntity(),"utf-8");
//                System.out.println(content);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
	}
//    public static void main(String[] args) {
////    	String url="http://dict.youdao.com/jsonapi?q=apple";
////    	Map<String,String> params=new HashMap<>();
////		params.put("userId", "vs_0000000000008");
////		params.put("userName", "张坤");
////		params.put("roles", "3");
////		params.put("nationalId", "25A958116C052168D8E6A2F0C3488390");
//////		url+="?userId=vs_0000000001203&userName=张坤&roles=3&nationalId=25A958116C052168D8E6A2F0C3488390";
////		System.out.println(url);
////		String contentString=HttpClientUtil.get(url);
////		System.out.println(contentString);
//    	String url="http://192.168.1.115:8080/SAonline/centerCtrl/routeService.do?sn=Exam&mn=saveUserAnswer&token=6DB6BB1C94AA2218CDB8E63828021F0A&userId=4BF29795F486B001&examId=375&testId=85068&questionId=1&dataType=2&businessType=2&userAnswer=%7B%22percentage%22%3A52.97606387434555%7D&score=1&sign=228e0c2b63f2d22185b562b119da6b5f";
//    	for(int i=0;i<3;i++){
//    		String contentString=HttpClientUtil.get(url);
//    		System.out.println(contentString+"   "+i);
//    	}
//
//	}
}