package com.mail.controller.backend;

import com.google.common.collect.Maps;
import com.mail.common.Const;
import com.mail.common.ServerResponse;
import com.mail.pojo.Product;
import com.mail.pojo.User;
import com.mail.service.IFileService;
import com.mail.service.IProductService;
import com.mail.service.IUserService;
import com.mail.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("manage/product")
public class ProductManagerController {
    @Autowired
    IUserService iUserService;
    @Autowired
    IProductService iProductService;
    @Autowired
    IFileService iFileService;

    @RequestMapping("/save.do")
    @ResponseBody
    public ServerResponse productSave(HttpSession session, Product product){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMsg("需要登陆");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.saveOrUpdateProduct(product);

        }else {
            return ServerResponse.createBySuccessMsg("不是管理员，没有权限");
        }
    }
    @RequestMapping("/set_sale_status.do")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session, Integer productId,Integer status){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMsg("需要登陆");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.setSaleStatus(productId,status);
        }else {
            return ServerResponse.createBySuccessMsg("不是管理员，没有权限");
        }
    }
    @RequestMapping("/detail.do")
    @ResponseBody
    public ServerResponse getDetail(HttpSession session,Integer productId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMsg("需要登陆");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.manageProductDetail(productId);
        }else {
            return ServerResponse.createBySuccessMsg("不是管理员，没有权限");
        }
    }
    @RequestMapping("/list.do")
    @ResponseBody
    public ServerResponse getList(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10")int pageSize){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMsg("需要登陆");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){

            return iProductService.getProductList(pageNum,pageSize);
        }else {
            return ServerResponse.createBySuccessMsg("不是管理员，没有权限");
        }
    }
    @RequestMapping("/search.do")
    @ResponseBody
    public ServerResponse productSearch(HttpSession session,String productName,Integer productId, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10")int pageSize){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMsg("需要登陆");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.searchProduct(productName,productId,pageNum,pageSize);
        }else {
            return ServerResponse.createBySuccessMsg("不是管理员，没有权限");
        }
    }
    @RequestMapping("/upload.do")
    @ResponseBody
    public ServerResponse upload( HttpSession session, @RequestParam(value = "upload_file" ,required = false) MultipartFile file , HttpServletRequest request){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMsg("需要登陆");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
            //相当于在webapp下面创建了 upload文件夹
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file,path);
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
            Map map = Maps.newHashMap();
            map.put("uri",targetFileName);
            map.put("url",url);
            return ServerResponse.createBySuccess(map);
        }else {
            return ServerResponse.createBySuccessMsg("不是管理员，没有权限");
        }
    }
    @RequestMapping("/rich_text.do")
    @ResponseBody
    public Map upload(HttpSession session, @RequestParam(value = "upload_file" ,required = false) MultipartFile file , HttpServletRequest request, HttpServletResponse response){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        Map resultMap = Maps.newHashMap();
        if (user == null) {
            resultMap.put("success",false);
            resultMap.put("msg","请登录管理员");
            return resultMap;
        }
        //富文本中的返回值有自己的要求，我们使用的是simditor所以按照simditor格式进行返回
//        {
//            "success":true/false,
//            "msg":"error message",
//            "file_path":"[real file path]"
//        }

        if (iUserService.checkAdminRole(user).isSuccess()){
            //相当于在webapp下面创建了 upload文件夹
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file,path);
            if (StringUtils.isBlank(targetFileName)){
                resultMap.put("success",false);
                resultMap.put("msg","上传失败");
                return resultMap;
            }
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix"+targetFileName);

            resultMap.put("success",true);
            resultMap.put("msg","上传成功");
            resultMap.put("file_path",url);
            response.addHeader("Access-Control-Allow-Headers","X-File-Name");

            return resultMap;
        }else {
            resultMap.put("success",false);
            resultMap.put("msg","没有权限");
            return resultMap;
        }
    }
}
