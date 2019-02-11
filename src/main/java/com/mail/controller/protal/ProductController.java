package com.mail.controller.protal;

import com.github.pagehelper.PageInfo;
import com.mail.common.ServerResponse;
import com.mail.service.IProductService;
import com.mail.vo.ProductDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/product/")
public class ProductController {
    @Autowired
    IProductService iProductService;

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<ProductDetailVo> detail(Integer productId){
        return iProductService.getProductDetail(productId);
    }
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(@RequestParam (value = "keyword" ,required = false)String keyword,
                                         @RequestParam (value = "categoryId" ,required = false)Integer categoryId,
                                         @RequestParam(value = "pageNow",defaultValue = "1") int pageNow,
                                         @RequestParam(value = "pageSize",defaultValue = "10")int pageSize,
                                         @RequestParam(value = "orderBy") String orderBy){
        return iProductService.getProductBykeywordCategory(keyword,categoryId,pageNow,pageSize,orderBy);
    }
}
