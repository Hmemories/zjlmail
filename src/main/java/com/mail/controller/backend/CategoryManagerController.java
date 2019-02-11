package com.mail.controller.backend;

import com.mail.common.Const;
import com.mail.common.ServerResponse;
import com.mail.pojo.User;
import com.mail.service.ICategoryService;
import com.mail.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/category")
public class CategoryManagerController {

    @Autowired
    IUserService iUserService;
    @Autowired
    ICategoryService iCategoryService;

    @RequestMapping("add_category.do")
    @ResponseBody
    public ServerResponse addCategory(HttpSession session,String categoryName,@RequestParam(value ="parent",defaultValue = "0") int parentId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMsg("需要登陆");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
            //是管理员
            //增加我们的处理分类逻辑
            return iCategoryService.addCategory(categoryName,parentId);
        }else {
            return ServerResponse.createBySuccessMsg("不是管理员，没有权限");
        }
    }

    @RequestMapping("set_category_name.do")
    @ResponseBody
    public ServerResponse setCategoryName(HttpSession session,Integer categoryId,String categoryName){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMsg("需要登陆");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
            return iCategoryService.updateCategoryName(categoryId,categoryName);
        }else {
            return ServerResponse.createBySuccessMsg("不是管理员，没有权限");
        }
    }

    @RequestMapping("get_category.do")
    @ResponseBody
    public  ServerResponse getChildrenParallelCategory(HttpSession session,@RequestParam(value =" categoryId" ,defaultValue = "0") int categoryId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMsg("需要登陆");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
            //查询子节点的category信息，并且不递归，保持平级
            return iCategoryService.getChildrenParallelCategory(categoryId);
        }else {
            return ServerResponse.createBySuccessMsg("不是管理员，没有权限");
        }
    }

    @RequestMapping("get_deep_category.do")
    @ResponseBody
    public  ServerResponse getCategoryAndDeepChildrenCategory(HttpSession session,@RequestParam(value =" categoryId" ,defaultValue = "0") int categoryId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMsg("需要登陆");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
            //当前节点的id和递归子节点的id
            //0->10000->100000
            return iCategoryService.selectCategoryAndChildernById(categoryId);
        }else {
            return ServerResponse.createBySuccessMsg("不是管理员，没有权限");
        }
    }
}
