package com.mail.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mail.common.ServerResponse;
import com.mail.dao.CategoryMapper;
import com.mail.pojo.Category;
import com.mail.service.ICategoryService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;


@Service("iCategoryService")
public class ICategoryServiceImpl implements ICategoryService {

    private Logger logger= LoggerFactory.getLogger(ICategoryServiceImpl.class);

    @Autowired
    private CategoryMapper categoryMapper;

    public ServerResponse addCategory(String categoryName,Integer parentId){
        if(parentId==null|| StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMsg("参数错误");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);  //表示这个分类可用

        int rowCount = categoryMapper.insert(category);
        if (rowCount>0){
            return ServerResponse.createBySuccessMsg("添加成功");
        }
        return ServerResponse.createByErrorMsg("添加失败");
    }
    public ServerResponse updateCategoryName(Integer categoryId,String categoryName){
        if(categoryId==null|| StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMsg("参数错误");
        }
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);

        int  rowCount= categoryMapper.updateByPrimaryKeySelective(category);
        if (rowCount>0){
            return ServerResponse.createBySuccessMsg("更新品类名字成功");
        }
        return ServerResponse.createByErrorMsg("更新品类名字失败");
    }

    public ServerResponse<List<Category>> getChildrenParallelCategory(int categoryId){
        List<Category> categories = categoryMapper.selectChildrenCategory(categoryId);
       if(CollectionUtils.isEmpty(categories)){
            logger.info("未找到当前分类的子分类");
       }
       return ServerResponse.createBySuccess(categories);
    }

    /**
     * 递归查询本节点的id
     * @param categoryId
     * @return
     */
    public ServerResponse<List<Integer>> selectCategoryAndChildernById(Integer categoryId){
        Set<Category> categorySet = Sets.newHashSet();
        findChildCategory(categorySet,categoryId);
        List<Integer> list = Lists.newArrayList();
        if (categoryId!=null){
            for (Category category: categorySet){
                list.add(category.getId());
            }
        }
        return ServerResponse.createBySuccess(list);
    }
    //递归算出子节点
    public Set<Category> findChildCategory(Set<Category> categorySet,int categoryId){
        Category category =categoryMapper.selectByPrimaryKey(categoryId);
        if (category!= null){
            categorySet.add(category);
        }
        List<Category> categoryList=categoryMapper.selectChildrenCategory(categoryId);
       for (Category categoryItem: categoryList){
           findChildCategory(categorySet,categoryItem.getId());
       }
       return categorySet;
    }
}
