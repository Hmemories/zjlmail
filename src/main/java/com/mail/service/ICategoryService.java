package com.mail.service;

import com.mail.common.ServerResponse;
import com.mail.pojo.Category;

import java.util.List;

public interface ICategoryService {
    ServerResponse addCategory(String categoryName, Integer parentId);
    ServerResponse updateCategoryName(Integer categoryId,String categoryName);
    ServerResponse <List<Category>> getChildrenParallelCategory(int categoryId);
    ServerResponse <List<Integer>>selectCategoryAndChildernById(Integer categoryId);
}
