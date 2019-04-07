package com.mail.controller.backend;

import com.mail.common.Const;
import com.mail.common.ServerResponse;
import com.mail.pojo.User;
import com.mail.service.IUserService;
import com.mail.util.CookieUtil;
import com.mail.util.JsonUtil;
import com.mail.util.RedisPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/user")
public class UserManageController {
    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User>login(String username, String password , HttpSession session, HttpServletResponse httpServletResponse){
        ServerResponse<User> response = iUserService.login(username,password);
        if (response.isSuccess()){
            User user = response.getData();
            if(user.getRole()==Const.Role.Role_ADMIN){
                CookieUtil.writeLoginToken(httpServletResponse,session.getId());
                RedisPoolUtil.setEx(session.getId(),Const.RedisCacheTime.REDIS_SESSION_EXTIME, JsonUtil.obj2String(response.getData()));
                return response;
            }else {
                return  ServerResponse.createByErrorMsg("不是管理员，无法登录");
            }
        }
        return response;
    }
}
