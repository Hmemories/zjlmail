package com.mail.controller.protal;

import com.mail.common.Const;
import com.mail.common.ResponseCode;
import com.mail.common.ServerResponse;
import com.mail.pojo.User;
import com.mail.service.ICartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart/")
public class CartController {
    @Autowired
    ICartService iCartService;

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse list(HttpSession session){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return  ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.list(user.getId());
    }

    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse add(HttpSession session , Integer count, Integer productId){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return  ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.add(user.getId(),productId,count);
    }

    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse update(HttpSession session , Integer count, Integer productId){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return  ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.update(user.getId(),productId,count);
    }

    @RequestMapping("delete_product.do")
    @ResponseBody
    public ServerResponse deleteProduct(HttpSession session , String productIds){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return  ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.deleteProduct(user.getId(),productIds);
    }


    @RequestMapping("select_all.do")
    @ResponseBody
    public ServerResponse selectAll(HttpSession session ){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return  ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(),null,Const.Cart.CHECKED);
    }
    @RequestMapping("un_select_all.do")
    @ResponseBody
    public ServerResponse unSelectAll(HttpSession session ){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return  ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(),null, Const.Cart.UN_CHECKED);
    }

    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse select(HttpSession session,Integer productId  ){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return  ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(),productId,Const.Cart.CHECKED);
    }

    @RequestMapping("un_select.do")
    @ResponseBody
    public ServerResponse unSelect(HttpSession session ,Integer productId ){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return  ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(),productId, Const.Cart.UN_CHECKED);
    }

    @RequestMapping("get_cart_product_count.do")
    @ResponseBody
    public ServerResponse<Integer> getCartProductCount(HttpSession session ,Integer productId ){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return  ServerResponse.createBySuccess(0);
        }
        return iCartService.getCartProductCount(user.getId());
    }
    //全选
    //全反选

    //单独选
    //单独反选

    //查询购物车里面的商品数

}
