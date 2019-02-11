package com.mail.service;

import com.mail.common.ServerResponse;
import com.mail.pojo.User;

public interface IUserService {
    ServerResponse<User> login(String username, String password);
    ServerResponse<String> regist(User user);
    ServerResponse<String> checkValid(String str,String type );
    ServerResponse<String> selectQuestion(String username);
    ServerResponse<String> forgetCheckAnswer(String username,String question,String answer);
    ServerResponse<String> forgetResetPassword(String username,String passwordNew ,String forgetToken);
    ServerResponse<String> resestPassword(User user ,String passwordNew,String passwordOld);
    ServerResponse <User>updateInformation(User user);
    ServerResponse<User> getInformation(Integer userId);
    ServerResponse<String> checkAdminRole(User user);
}
