package org.ruoxue.backend.util;


import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 *  志勋 + 佳斌 工具类
 */
public class XunBinKit {

    /**
     *  获取用户id
     */
    public static Integer getUid(){
        HttpSession session = HttpKit.getRequest().getSession();
        Integer uid = (Integer) session.getAttribute("uid");
        return uid;
    }

    /**
     *  获取response对象
     */
    public static HttpServletResponse getResponse(){
        HttpServletResponse response = HttpKit.getResponse();
        return response;
    }

    /**
     *  生成一个md加密后的64为token
     */
    public static String generateToken(){
        String sixNum = generateSixNum();
        String md5Token = null;
        try {
            md5Token = Md5SaltTool.getEncryptedPwd(sixNum);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return md5Token;
    }

    /**
     *  随机生成六位数字
     */
    public static String generateSixNum(){
        StringBuilder sb = new StringBuilder();
        Random rand = new Random();
        for(int i = 0; i < 6; i++){
            int num = rand.nextInt(10);
            sb.append(num + "");
        }
        return sb.toString();
    }

}
