package com.mail.dao;

import com.mail.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUsername(String username);

    int checkEmail(String email);

    int checkPassword(@Param("password") String password,@Param("userId") int userId);

    int checkAnswer(@Param("username")String username,@Param("question")String question,@Param("answer")String answer);

    int checkEmailByUserId(@Param("email") String email,@Param("userId")int userId);

    int updatePasswordByUsername(@Param("username")String username,@Param("passwordNew")String passwordNew);

    String selectQuestionByUsername(String username);

    User selectLogin(@Param("username") String username,@Param("password") String password);
}