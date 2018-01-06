package cn.tsukasalwq.service;


import cn.tsukasalwq.common.ServerResponse;
import cn.tsukasalwq.pojo.Category;

import java.util.List;

public interface ICategoryService {

    ServerResponse addCategory(String categoryName, Integer parentId);

    ServerResponse updateCategory(Integer categoryId, String categoryName);

    ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);

    ServerResponse<List<Integer>> getCategoryAndChildrenById(Integer categoryId);
}
