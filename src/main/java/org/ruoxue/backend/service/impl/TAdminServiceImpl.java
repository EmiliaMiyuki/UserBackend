package org.ruoxue.backend.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.ruoxue.backend.bean.TAdmin;
import org.ruoxue.backend.bean.TSignin;
import org.ruoxue.backend.mapper.TAdminMapper;
import org.ruoxue.backend.mapper.TLogsMapper;
import org.ruoxue.backend.mapper.TSigninMapper;
import org.ruoxue.backend.service.ITAdminService;
import org.ruoxue.backend.util.Md5Util;
import org.ruoxue.backend.util.ResultUtil;
import org.ruoxue.backend.util.ToolUtil;
import org.ruoxue.backend.util.XunBinKit;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author fengjb
 * @since 2018-08-30
 */
@Service("TAdminServiceImpl")
public class TAdminServiceImpl extends ServiceImpl<TAdminMapper, TAdmin> implements ITAdminService {

    @Resource
    private TAdminMapper adminMapper;

    @Resource
    private TSigninMapper signinMapper;

    @Resource
    private TLogsMapper logsMapper;

    @Override
    public Object basicGet(Integer uid) {
//        获取admin实体
        List<Map<String, Object>> list = adminMapper.getAdminAndRole(uid);

        return ResultUtil.success(list);
    }

    @Override
    public Object basicPost(JSONObject jsonObject) {
//      获取参数
        String name = jsonObject.getString("name");

        if(ToolUtil.isEmpty(name)){
            return ResultUtil.error(-1, "参数错误");
        }

//        获取用户id
        Integer uid = XunBinKit.getUid();
        if(ToolUtil.isEmpty(uid)){
            return ResultUtil.error(-4, "用户id为空");
        }

        TAdmin adm = adminMapper.getTAdminByUid(uid);
        if(ToolUtil.isEmpty(adm)){
            ResultUtil.error(-2, "该用户信息为空");
        }

        Integer len = adminMapper.updateAdmin(name, adm.getId());

        logsMapper.addLog(uid, "修改管理员", 1);
        if(len == 1){
            return ResultUtil.success();
        } else {
            return ResultUtil.error(-3, "修改管理员信息失败");
        }

    }

    @Override
    public Object password(String old_pwd, String new_pwd) {
//        获取用户id
        Integer uid = XunBinKit.getUid();
//        获取管理员bean
        TAdmin admin = adminMapper.getTAdminByUid(uid);
//        获取signbean
        TSignin signin = signinMapper.getSigninByUid(admin.getLid());
//        获取加密后的md5原密码
        String oldMd5Pwd = Md5Util.getMD5(old_pwd);
        System.out.println("--111: " + oldMd5Pwd);
        System.out.println("--111: " + signin.getPassword());
        if(!oldMd5Pwd.equals(signin.getPassword())){
            return ResultUtil.error(-1, "原密码错误");
        }

//            将新密码加密
        String newMd5Pwd = Md5Util.getMD5(new_pwd);
        Integer len = signinMapper.updatePassword(newMd5Pwd, admin.getLid());

        logsMapper.addLog(uid, "修改密码", 1);
        if(len == 1){
            return ResultUtil.success(0, "密码修改成功");
        } else {
            return ResultUtil.error(-2, "密码修改失败");
        }

    }

    @Override
    public Object list(Integer page, Integer size, Integer count) {

        if (ToolUtil.isNotEmpty(count)) {
            return ResultUtil.success(adminMapper.countGetAdminList());
        }

        if(ToolUtil.isEmpty(page)){
            page = 1;
        }
        if(ToolUtil.isEmpty(size)){
            size = 10;
        }
        page = (page - 1) * size;
        List<Map<String, Object>> list = adminMapper.getAdminList(page, size);

        return ResultUtil.success(list);
    }

    @Override
    public Object adminAdd(JSONObject jsonObject) {
//        获取参数
        String name = jsonObject.getString("name");
        Integer roleid = jsonObject.getInteger("roleid");
        Integer gid = jsonObject.getInteger("gid");
        String phone = jsonObject.getString("phone");
        String password = jsonObject.getString("password");

        if(ToolUtil.isEmpty(name) || ToolUtil.isEmpty(roleid) || ToolUtil.isEmpty(gid) || ToolUtil.isEmpty(phone) || ToolUtil.isEmpty(password)){
            return ResultUtil.error(-1, "参数错误");
        }

        Integer len = adminMapper.countAdminByName(name) + adminMapper.countCustomerByName(name);
        if(len != 0){
            return ResultUtil.error(-2, "该用户已注册");
        }

//        先插入sign表
        TSignin signin = insertSign(password);

        TAdmin admin = new TAdmin();
        admin.setWid("");
        admin.setStatus(1);
        admin.setModifieddate(new Date());
        admin.setLid(signin.getId());
        admin.setCreatedate(new Date());
        admin.setName(name);
        admin.setPhone(phone);
        admin.setGid(gid);
        admin.setRoleid(roleid);

        logsMapper.addLog(admin.getId(), "添加管理员", 1);
        boolean b = admin.insert();

        return XunBinKit.returnResult(b, -3, null, "添加管理员成功", "添加管理员失败");
    }

    @Override
    public Object removeAdmin(Integer aid) {
        if(ToolUtil.isEmpty(aid)){
            return ResultUtil.error(-1, "参数错误");
        }

        TAdmin admin = adminMapper.getTAdminByUid(aid);
        if(ToolUtil.isEmpty(admin)){
            return ResultUtil.error(-4, "该用户不存在");
        }

        Integer len2 = signinMapper.deleteSign(admin.getLid());

        Integer len1 = adminMapper.deleteAdmin(aid);

        logsMapper.addLog(aid, "删除管理员", 1);

        return XunBinKit.returnResult(((len1 + len2) == 2), -3, null, "删除成功", "删除失败");
    }

    @Override
    public Object getAdminByAid(Integer aid) {
        if(ToolUtil.isEmpty(aid)){
            return ResultUtil.error(-1, "参数错误");
        }

        TAdmin admin = adminMapper.getAdminByAid(aid);

        return XunBinKit.returnResult(ToolUtil.isNotEmpty(admin), -2, admin,"查询成功", "该管理员不存在");
    }

    @Override
    public Object updateAdmin(Integer aid, JSONObject jsonObject) {

//        获取参数
        String name = jsonObject.getString("name");
        Integer roleid = jsonObject.getInteger("roleid");
        String phone = jsonObject.getString("phone");
        String password = jsonObject.getString("password");

        if(ToolUtil.isEmpty(aid) || ToolUtil.isEmpty(name) || ToolUtil.isEmpty(roleid) || ToolUtil.isEmpty(phone)){
            return ResultUtil.error(-1, "参数错误");
        }

        if (ToolUtil.isNotEmpty(password)) {
            password = Md5Util.getMD5(password);
        }

        TAdmin admin = adminMapper.getAdminByAid(aid);
        if(ToolUtil.isEmpty(admin)){
            return ResultUtil.error(-3, "没有查询到管理员信息");
        }

//        修改密码
        Integer len1 = signinMapper.updatePassword(password, admin.getLid());

        logsMapper.addLog(aid, "修改密码", 1);

//        修改账号信息
        Integer len2 = adminMapper.updateAdminByJson(aid, name, roleid, phone);

        logsMapper.addLog(aid, "修改管理员信息", 1);

        return XunBinKit.returnResult((len1 + len2 == 2), -2, null, "修改成功", "修改失败");
    }

    //    插入sign表
    private TSignin insertSign(String password){
//        将密码加密
        String md5Pwd = Md5Util.getMD5(password);

//        生成一个token
        String token = XunBinKit.generateToken();

        TSignin signin = new TSignin();
        signin.setPassword(md5Pwd);
        signin.setToken(token);
        signin.setSalt("");
        signin.setRole(2);
        signin.setMsgcode("");
        signin.insert();

        logsMapper.addLog(signin.getId(), "添加登录信息", 1);

        return signin;
    }


}
