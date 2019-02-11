package com.mail.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mail.common.Const;
import com.mail.common.ServerResponse;
import com.mail.dao.CartMapper;
import com.mail.dao.ProductMapper;
import com.mail.pojo.Cart;
import com.mail.pojo.Product;
import com.mail.service.ICartService;
import com.mail.util.BigDecimalUtil;
import com.mail.util.PropertiesUtil;
import com.mail.vo.CartProductVo;
import com.mail.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service("iCartService")
public class ICartServiceImpl implements ICartService {
    @Autowired
    CartMapper cartMapper;
    @Autowired
    ProductMapper productMapper;


    public ServerResponse<CartVo> list (Integer userId){
        CartVo cartVo = this.getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    public ServerResponse<CartVo> add(Integer userId,Integer productId,Integer count){
       Cart cart = cartMapper.selectCartByUserIdProductId(userId,productId);
       if (cart == null){
           Cart cartItem = new Cart();
           cartItem.setUserId(userId);
           cartItem.setProductId(productId);
           cartItem.setQuantity(count);
           cartItem.setChecked(Const.Cart.CHECKED);
           //如果产品不在购物车，需要新加一条记录
           cartMapper.insert(cartItem);
       }else{
           //代表商品已经加入购物车
           count = cart.getQuantity()+count;
           cart.setQuantity(count);
           cartMapper.updateByPrimaryKeySelective(cart);
       }
        return this.list(userId);
    }
    public ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count){
        if (userId == null || productId == null) {
            return  ServerResponse.createByErrorMsg("参数错误");
        }
        Cart cart = cartMapper.selectCartByUserIdProductId(userId,productId);

        if (cart != null) {
            cart.setQuantity(count);
        }
        cartMapper.updateByPrimaryKeySelective(cart);

        return this.list(userId);
    }


    public ServerResponse<CartVo> deleteProduct (Integer userId, String productIds){
        if (userId == null || productIds == null) {
            return  ServerResponse.createByErrorMsg("参数错误");
        }
        List<String> productIdList = Splitter.on(",").splitToList(productIds);
        if(CollectionUtils.isEmpty(productIdList)){
            return  ServerResponse.createByErrorMsg("参数错误");
        }
        cartMapper.deleteByUserIdProductIds(userId,productIdList);

        return this.list(userId);
    }

    public  ServerResponse<CartVo> selectOrUnSelect(Integer userId,Integer productId,Integer checked){

        cartMapper.checkedOrUncheckedProducts(userId, productId,checked);

        return this.list(userId);
    }

    public  ServerResponse<Integer> getCartProductCount(Integer userId){

        return ServerResponse.createBySuccess(cartMapper.selectCartProductCount(userId));
    }



    private CartVo getCartVoLimit(Integer userId){
        CartVo cartVo = new CartVo();
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);
        List<CartProductVo > cartProductVoList = Lists.newArrayList();
        BigDecimal cartTotalPrice = new BigDecimal("0");
        if (CollectionUtils.isNotEmpty(cartList)){
            for (Cart cartItem : cartList){
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setUserId(cartItem.getUserId());
                cartProductVo.setProductId(cartItem.getProductId());

                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
                if (product!= null){
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStock(product.getStock());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductStatus(product.getStatus());
                    //判断库存
                    int buyLimitCount = 0;
                    if (product.getStock()>=cartItem.getQuantity()){
                        buyLimitCount=cartItem.getQuantity();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    }else {
                        buyLimitCount= product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        //购物车中更新有效库存
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(cartItem.getId());
                        cartForQuantity.setQuantity( buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }

                    cartProductVo.setQuantity(buyLimitCount);
                    //计算总价
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartProductVo.getQuantity()));

                }
                if(cartItem.getChecked() == Const.Cart.CHECKED){
                    //如果已经勾选，则计入总价
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);
            }
        }
        cartVo.setCartTotal(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setAllChecked(getAllCheckedStatus(userId));
        cartVo.setImage(PropertiesUtil.getProperty("http://img.imooc.com/"));
        return cartVo;

    }


    private boolean getAllCheckedStatus(Integer userId){
        if (userId == null){
            return true;
        }
        return  cartMapper.selectCartProductCheckedStatusByUserId(userId) == 0;
    }
}
