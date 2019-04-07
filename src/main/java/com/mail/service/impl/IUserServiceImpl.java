package com.mail.service.impl;

import com.mail.common.Const;
import com.mail.common.ServerResponse;
import com.mail.dao.UserMapper;
import com.mail.pojo.User;
import com.mail.service.IUserService;
import com.mail.util.MD5Util;
import com.mail.util.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("iUserService")
public class IUserServiceImpl  implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMsg("用户名不存在");
        }
        // todo 密码登录MD5
        String MD5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username, MD5Password);
        if (user == null) {
            return ServerResponse.createByErrorMsg("密码错误");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登陆成功", user);
    }

    @Override
    public ServerResponse<String> regist(User user) {
        ServerResponse validResponse =checkValid(user.getUsername(),Const.USERNAME);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        validResponse = this.checkValid(user.getEmail(),Const.EMAIL);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        user.setRole(Const.Role.Role_Customer);

        //MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        if (userMapper.insert(user)==0){
            return ServerResponse.createByErrorMsg("注册失败");
        }
        return ServerResponse.createBySuccessMsg("注册成功");

    }

    public ServerResponse<String> checkValid(String str,String type ){
        if (StringUtils.isNotBlank(type)){
            if (type.equals(Const.USERNAME)){
                int resultCount = userMapper.checkUsername(str);
                if (resultCount > 0) {
                    return ServerResponse.createByErrorMsg("用户名存在");
                }
            }
            if (type.equals(Const.EMAIL)){
                if(userMapper.checkEmail(str)>0){
                    return ServerResponse.createByErrorMsg("邮箱已存在");
                }
            }
        }else {
            return ServerResponse.createByErrorMsg("输入错误");
        }
        return ServerResponse.createBySuccessMsg("检验成功");
    }

    public ServerResponse<String> selectQuestion(String username){
       ServerResponse validResponse = this.checkValid(username,Const.USERNAME);
       if (validResponse.isSuccess()){
           return ServerResponse.createByErrorMsg("用户不存在");
       }
       String question = userMapper .selectQuestionByUsername(username);
       if (StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccess(question);
       }
       return ServerResponse.createByErrorMsg("找回密码的问题为空");

    }

    public ServerResponse<String> forgetCheckAnswer(String username,String question ,String answer){
        int resutCount = userMapper.checkAnswer(username,question,answer);
        if (resutCount>0){
            //问题及问题答案正确
            String forgetToken = UUID.randomUUID().toString();
            RedisPoolUtil.setEx(Const.TOKEN_PREFIX+username,60*60*24,forgetToken);
//            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);
            return ServerResponse.createBySuccessMsg(forgetToken);

        }
        return ServerResponse.createByErrorMsg("问题答案错误");
    }
    public ServerResponse<String> forgetResetPassword(String username,String passwordNew ,String forgetToken){
        if (StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMsg("需要传递token");
        }
        ServerResponse validResponse = this.checkValid(username,Const.USERNAME);
        if (validResponse.isSuccess()){
            return ServerResponse.createByErrorMsg("用户不存在");
        }
        //获取本地缓存
//        String token= TokenCache.getkey(TokenCache.TOKEN_PREFIX+username);
        String token = RedisPoolUtil.get(Const.TOKEN_PREFIX+username);

        if (StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMsg("token");
        }
        //这个equal可以防止a=null  a.equal("abc")
        if(StringUtils.equals(token,forgetToken)){
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowResult =userMapper.updatePasswordByUsername(username,md5Password);
            if (rowResult>0){
                return ServerResponse.createBySuccessMsg("修改密码成功");
            }
        }else {
            return ServerResponse.createByErrorMsg("token错误");
        }
        return ServerResponse.createByErrorMsg("修改密码失败");
    }

    public ServerResponse<String> resestPassword(User user ,String passwordNew,String passwordOld){
        //为了横向越权的问题，要校验这个用户的旧密码是指向这个用户，因为我们会查询一个count(1)出来，如果不指定id，那一定是true
        int resultCount=userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
        if (resultCount==0){
            return ServerResponse.createByErrorMsg("旧密码错误");
        }
        //选择性更新
        int updateCount= userMapper.updateByPrimaryKeySelective(user);
        if (updateCount>0){
           return ServerResponse.createBySuccessMsg("更新成功");
        }
        return ServerResponse.createByErrorMsg("密码更新失败");
    }
    public ServerResponse<User> updateInformation(User user){
        //username是不能被更新的
        //更新email，需要校验email是否存在
         int resultCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
         if (resultCount>0){
             ServerResponse.createByErrorMsg("该email已经被占用");
         }
         User updateUser = new User();
         updateUser.setId(user.getId());
         updateUser.setAnswer(user.getAnswer());
         updateUser.setEmail(user.getEmail());
         updateUser.setPhone(user.getPhone());
         updateUser.setQuestion(user.getQuestion());
         int updateResult=  userMapper.updateByPrimaryKeySelective(updateUser);
         if (updateResult>0){
            return ServerResponse.createBySuccess("更新成功",updateUser);
         }
         return ServerResponse.createByErrorMsg("更新失败");

    }
    public ServerResponse<User> getInformation(Integer userId){
        User user =userMapper.selectByPrimaryKey(userId);
        if (user == null){
            return ServerResponse.createByErrorMsg("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return  ServerResponse.createBySuccess(user);
    }

    /**
     * 检验是否为管理员
     * @param user
     * @return
     */
    public ServerResponse<String> checkAdminRole(User user){
        if(user!=null && user.getRole().intValue() == Const.Role.Role_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

}
