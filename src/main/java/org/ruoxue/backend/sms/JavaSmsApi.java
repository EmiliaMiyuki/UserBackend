package org.ruoxue.backend.sms;


import com.sun.media.jfxmedia.logging.Logger;
import io.swagger.annotations.ApiOperation;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.ruoxue.backend.common.constant.ConfigNames;
import org.ruoxue.backend.mapper.TConfigMapper;
import org.ruoxue.backend.util.ResultUtil;
import org.ruoxue.backend.util.XunBinKit;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

/**
 *  点对点发短信
 * @author fengjb
 * @date 2018-09-03
 */
@Controller
@RequestMapping("/api")
public class JavaSmsApi {

    private static String API_KEY="";

    //智能匹配模板发送接口的http地址
    private static String URI_SEND_SMS = "https://sms.yunpian.com/v2/sms/single_send.json";

    //模板发送接口的http地址
    private static String URI_TPL_SEND_SMS = "https://sms.yunpian.com/v2/sms/tpl_single_send.json";

    //编码格式。发送编码格式统一用UTF-8
    private static String ENCODING = "UTF-8";

    private static String code = "";

    private static String company = "云片网";

    @Resource
    private TConfigMapper configMapper;

    private static int expireMinutes = -1;

    @RequestMapping("/sendmsg-test")
    public @ResponseBody Object sentTextMessageTest(@RequestParam String phone, @RequestParam String code, HttpSession session) {
        String imgCode = (String)session.getAttribute("code");
        if (imgCode != null) {
            if (!imgCode.toUpperCase().equals(code.toUpperCase())) {
                return ResultUtil.error(3, "图片验证码不正确。");
            }
        }
        else {
            return ResultUtil.error(2, "请先获取图片验证码。");
        }
        String msgcode = XunBinKit.generateSixNum();
        session.setAttribute("msgcode", msgcode);
        return ResultUtil.success(0, "验证码是" + msgcode);
    }

    @ApiOperation("单发短信")
    @RequestMapping("/sendmsg")
    public @ResponseBody Object sendTextMessage(@RequestParam String phone, @RequestParam String code, HttpSession session) {
        // 初始化参数
        try {
            if (!TencentSMS.initialized) {
                TencentSMS.appkey = configMapper.getConfigByName(ConfigNames.appkey);
                TencentSMS.appid = Integer.parseInt(configMapper.getConfigByName(ConfigNames.appid));
                TencentSMS.smsSign = configMapper.getConfigByName(ConfigNames.smsSign);
                TencentSMS.templateId = Integer.parseInt(configMapper.getConfigByName(ConfigNames.templateID));
                TencentSMS.template = configMapper.getConfigByName(ConfigNames.templateContent);
                /*Logger.logMsg(Logger.DEBUG, "" + TencentSMS.appkey + "/" + TencentSMS.appid + "/"
                        + TencentSMS.smsSign + "/" + TencentSMS.templateId);*/
                if (TencentSMS.appkey == null || TencentSMS.smsSign == null || TencentSMS.template == null)
                    throw new Exception("Not all required parameters are provided");
                TencentSMS.initialized = true;
            }

            String imgCode = (String)session.getAttribute("code");
            if (imgCode != null) {
                if (!imgCode.toUpperCase().equals(code.toUpperCase())) {
                    return ResultUtil.error(3, "图片验证码不正确。");
                }
            }
            else {
                return ResultUtil.error(2, "请先获取图片验证码。");
            }

            // 隨機驗證碼，在模板中作為 $code 代替
            String msgcode = XunBinKit.generateSixNum();
            session.setAttribute("msgcode", msgcode);

            // 驗證碼失效時間，在模板中以 $minute 代替，該值從數據庫中的 sms_expire 字段獲取
            if (expireMinutes == -1) {
                try {  expireMinutes = Integer.parseInt(configMapper.getConfigByName(ConfigNames.expireMinutesSMS)); }
                catch (Exception e) { expireMinutes = 15; }
            }
            session.setAttribute("msg_expire", new Date().getTime() + expireMinutes*60*1000);
            session.setAttribute("resend_date", new Date().getTime() + 60*1000);

            // 處理模板
            String template = TencentSMS.template.replaceAll("\\$code", msgcode)
                                                 .replaceAll("\\$minute", ""+expireMinutes);

            TencentSMS.sendTextMessage(phone, template);
            return ResultUtil.success();
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error(1, e.getMessage());
        }
    }

    @ApiOperation("点对点发送短信")
    @RequestMapping(value = "/sendmsg2", method = RequestMethod.GET)
    public @ResponseBody String sendmsg(@RequestParam String phone) {

//        获取数据库中存储的apikey
        List<Map<String, Object>> list = configMapper.getConfig();

//        保存从数据库中读取出来的信息
        Map<String, Object> msg = new HashMap<String, Object>();
        for(Map<String, Object> map : list){
            msg.put((String) map.get("name"), map.get("value"));
        }
        System.out.println("-----msg: " + msg);

        //修改为您的apikey.apikey可在官网（http://www.yunpian.com)登录后获取
        API_KEY = (String) msg.get("sms_appkey");

        //修改为您要发送的手机号
        String mobile = phone;

//        随机生成六位验证码
        code = XunBinKit.generateSixNum();

        String text = "【" + company + "】您的验证码是" + code;
        System.out.println("----------text: " + text + ",mobile: " + mobile);
        // 发短信调用示例
        try {
            return sendSms(text, mobile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

        //设置模板ID，如使用1号模板:【#company#】您的验证码是#code#
//        long tpl_id = 1;
//        //设置对应的模板变量值
//
//        String tpl_value = URLEncoder.encode("#code#",ENCODING) +"="
//                + URLEncoder.encode(code, ENCODING) + "&"
//                + URLEncoder.encode(company,ENCODING) + "="
//                + URLEncoder.encode("云片网",ENCODING);
//        //模板发送的调用示例
//        tplSendSms(apikey, tpl_id, tpl_value, mobile);

//        调用发送短信
//        sendSms(text, mobile);
    }

    /**
     * 智能匹配模板接口发短信
     *
     * @param text   　短信内容
     * @param mobile 　接受的手机号
     * @return json格式字符串
     * @throws IOException
     */

    public static String sendSms(String text, String mobile) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("apikey",API_KEY);
        params.put("text", text);
        params.put("mobile", mobile);
        return post(URI_SEND_SMS, params);
    }

    /**
     * 通过模板发送短信(不推荐)
     *
     * @param apikey    apikey
     * @param tpl_id    　模板id
     * @param tpl_value 　模板变量值
     * @param mobile    　接受的手机号
     * @return json格式字符串
     * @throws IOException
     */

    public static String tplSendSms(String apikey, long tpl_id, String tpl_value, String mobile) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("apikey", apikey);
        params.put("tpl_id", String.valueOf(tpl_id));
        params.put("tpl_value", tpl_value);
        params.put("mobile", mobile);
        return post(URI_TPL_SEND_SMS, params);
    }

    /**
     * 基于HttpClient 4.3的通用POST方法
     *
     * @param url       提交的URL
     * @param paramsMap 提交<参数，值>Map
     * @return 提交响应
     */

    public static String post(String url, Map<String, String> paramsMap) {
        CloseableHttpClient client = HttpClients.createDefault();
        String responseText = "";
        CloseableHttpResponse response = null;
        try {
            HttpPost method = new HttpPost(url);
            if (paramsMap != null) {
                List<NameValuePair> paramList = new ArrayList<NameValuePair>();
                for (Map.Entry<String, String> param : paramsMap.entrySet()) {
                    NameValuePair pair = new BasicNameValuePair(param.getKey(), param.getValue());
                    paramList.add(pair);
                }
                method.setEntity(new UrlEncodedFormEntity(paramList, ENCODING));
            }
            response = client.execute(method);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                responseText = EntityUtils.toString(entity, ENCODING);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return responseText;
    }


}
