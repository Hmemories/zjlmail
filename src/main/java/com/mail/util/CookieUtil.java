package com.mail.util;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class CookieUtil {
    private final static String COOKIE_DOMAIN ="happymmail.com";
    private final static String COOKIE_NAME="mmail_login_token";


    public static String readLoginToken(HttpServletRequest request){
        Cookie[] cookies  = request.getCookies();
        if (cookies != null){
            for (Cookie cookie :cookies){
                if (StringUtils.equals(cookie.getName(),COOKIE_NAME)) {
                    log.info("read cookieName:{},cookieValue:{}", cookie.getName(), cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        return null;
    }



    public static void writeLoginToken(HttpServletResponse response, String token){
        Cookie cookie = new Cookie(COOKIE_NAME,token);

        cookie.setDomain(COOKIE_DOMAIN);
        cookie.setPath("/"); //表示设置在根目录下
        cookie.setHttpOnly(true);

        //单位秒
        //如果这个maxage不设置，cookie就不会写入硬盘，而是写在内存中，只在当前页面有效 -1代表永久
        cookie.setMaxAge(60*60*24*365);
        log.info("write cookieName:{} ,cookieValue:{}",cookie.getName(),cookie.getValue());
        //写入cookie
        response.addCookie(cookie);
    }


    public static void delLoginToken(HttpServletRequest request,HttpServletResponse response){
        Cookie[] cookies = request.getCookies();
        if (cookies != null){
            for (Cookie cookie: cookies){
                if (StringUtils.equals(cookie.getValue(),COOKIE_NAME)) {
                    cookie.setDomain(COOKIE_DOMAIN);
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    log.info("del cookieName:{} ,cookieValue:{}",cookie.getName(),cookie.getValue());
                    response.addCookie(cookie);
                    return;
                }
            }
        }
    }
}
