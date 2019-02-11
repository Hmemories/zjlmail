package com.mail.service;

import com.github.pagehelper.PageInfo;
import com.mail.common.ServerResponse;
import com.mail.pojo.Product;
import com.mail.vo.ProductDetailVo;

public interface IProductService {
    ServerResponse saveOrUpdateProduct(Product product);
    ServerResponse setSaleStatus(Integer productId ,Integer status);
    ServerResponse manageProductDetail(Integer productId);
    ServerResponse getProductList (int pageNum,int pageSize);
    ServerResponse<PageInfo> searchProduct(String productName, Integer productId, int pageNum, int pageSize);
    ServerResponse<ProductDetailVo> getProductDetail(Integer productId);
    ServerResponse<PageInfo> getProductBykeywordCategory(String keyword,Integer categoryId, int pageNow,int pageSize,String orderBy);
}
