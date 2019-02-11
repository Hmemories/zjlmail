package com.mail.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mail.common.Const;
import com.mail.common.ResponseCode;
import com.mail.common.ServerResponse;
import com.mail.dao.CategoryMapper;
import com.mail.dao.ProductMapper;
import com.mail.pojo.Category;
import com.mail.pojo.Product;
import com.mail.service.ICategoryService;
import com.mail.service.IProductService;
import com.mail.util.DateTimeUtil;
import com.mail.util.PropertiesUtil;
import com.mail.vo.ProductDetailVo;
import com.mail.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("iProductService")
public class IProductServiceImpl implements IProductService {

    @Autowired
    ProductMapper productMapper;
    @Autowired
    CategoryMapper categoryMapper;
    @Autowired
    ICategoryService iCategoryService;

    public ServerResponse saveOrUpdateProduct(Product product){
        if(product!=null){
            if (StringUtils.isNotBlank(product.getSubImages())){
                String[] subImageArray =product.getSubImages().split(",");
                if (subImageArray.length>0){
                    product.setMainImage(subImageArray[0]);
                }
             if (product.getId()!=null){
                 int rowCount= productMapper.updateByPrimaryKey(product);
                 if(rowCount>0){
                     return ServerResponse.createBySuccess("更新成功");
                 }else {
                    return ServerResponse.createByErrorMsg("更新产品失败");
                 }
             }else{
                int rowCount= productMapper.insert(product);
                 if(rowCount>0){
                     return ServerResponse.createBySuccess("新增成功");
                 }else {
                     return ServerResponse.createByErrorMsg("新增产品失败");
                 }
             }

            }
        }
            return ServerResponse.createByErrorMsg("新增或更新产品不正确");
    }

    public ServerResponse setSaleStatus(Integer productId ,Integer status){
        if (productId== null ||status == null){
            return ServerResponse.createByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product =new Product();
        product.setId(productId);
        product.setStatus(status);
        int rowCount =productMapper.updateByPrimaryKeySelective(product);
        if (rowCount>0){
            return ServerResponse.createBySuccessMsg("更新状态成功");
        }
        return ServerResponse.createByErrorMsg("更新状态失败");
    }

    public ServerResponse manageProductDetail(Integer productId){

        if (productId== null){
            return ServerResponse.createByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product=productMapper.selectByPrimaryKey(productId);
        if (product==null){
            return ServerResponse.createByErrorMsg("产品已下架或删除");
        }
        //VO对象--value object
        //pojo->bo(business object)->vo(view object)
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    private ProductDetailVo assembleProductDetailVo(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());

        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));

        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category == null){
            productDetailVo.setParentCategoryId(0);//默认根节点
        }else{
            productDetailVo.setParentCategoryId(category.getParentId());
        }

        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return productDetailVo;
    }

    public ServerResponse getProductList (int pageNum,int pageSize){
        //startPage -start
        //填充自己的sql查询逻辑
        //pageHelper -收尾
        PageHelper.startPage(pageNum,pageSize);
        List<Product> productList = productMapper.selectList();
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product product :productList){
            ProductListVo productListVo =assembleProductListVo(product);
            productListVoList.add(productListVo);
        }
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productList);
        return ServerResponse.createBySuccess(pageResult);
    }

    private ProductListVo assembleProductListVo(Product product){
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setName(product.getName());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setStatus(product.getStatus());
        return productListVo;
    }

    public ServerResponse<PageInfo> searchProduct(String productName,Integer productId, int pageNum, int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        if (StringUtils.isNotBlank(productName)){
            productName=new StringBuffer().append("%").append(productName).append("%").toString();
        }
        List<Product> productList= productMapper.selectByNameAndProductId(productName,productId);
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product product :productList){
            ProductListVo productListVo =assembleProductListVo(product);
            productListVoList.add(productListVo);
        }
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);
    }
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId){
        if (productId== null){
            return ServerResponse.createByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);

        if (product==null){

            return ServerResponse.createByErrorMsg("产品已下架或删除");
        }
        if (product.getStatus()!= Const.ProductStatusEnum.ON_SALE.getCode()){
            if (product==null){
                return ServerResponse.createByErrorMsg("产品已下架或删除");
            }
        }
        //VO对象--value object
        //pojo->bo(business object)->vo(view object)
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }
    public ServerResponse<PageInfo> getProductBykeywordCategory(String keyword,Integer categoryId, int pageNow,int pageSize,String orderBy){
        if (StringUtils.isBlank(keyword) && categoryId == null){
            return ServerResponse.createByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        List<Integer> categoryIdList = new ArrayList<Integer>();

        if(categoryId!=null){
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if (category == null && StringUtils.isBlank(keyword)){
                PageHelper.startPage(pageNow,pageSize);
                List<ProductListVo> productListVoList = Lists.newArrayList();
                PageInfo pageResult = new PageInfo(productListVoList);
                return  ServerResponse.createBySuccess(pageResult);
            }
            categoryIdList = iCategoryService.selectCategoryAndChildernById(categoryId).getData();
        }
        if (StringUtils.isNotBlank(keyword)){
            keyword = new StringBuffer().append("%").append(keyword).append("%").toString();
        }
        PageHelper.startPage(pageNow,pageSize);
        //排序处理
        if (StringUtils.isNotBlank(orderBy)){
            if (Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
                String[] orderByArray = orderBy.split("_");
                PageHelper.orderBy(orderByArray[0]+" "+orderByArray[1]);
            }
        }
        List<Product> productList = productMapper.selectByNameAndCategoryIds(keyword,categoryIdList);
        List<ProductListVo> productListVoList =Lists.newArrayList();
        for (Product product :productList){
           ProductListVo productListVo= assembleProductListVo(product);
           productListVoList.add(productListVo);
        }
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);

    }

}
