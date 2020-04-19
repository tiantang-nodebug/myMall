package com.imooc.mall.service.impl;

import com.imooc.mall.controller.consts.MallConst;
import com.imooc.mall.dao.CategoryMapper;
import com.imooc.mall.pojo.Category;
import com.imooc.mall.service.ICategoryService;
import com.imooc.mall.vo.CategoryVo;
import com.imooc.mall.vo.ResponseVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CategoryService implements ICategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 耗时：http请求最耗时
     * @return
     */
    @Override
    public ResponseVo<List<CategoryVo>> selectAll() {
        //List<CategoryVo> categoryVoList=new ArrayList<>();
        List<Category> categories=categoryMapper.selectAll();
        //查出来parent_id=0的记录
        //先用for循环写一遍，之后再用lambda表达式做对照。
//        for (Category category:categories){
//            if (category.getParentId().equals(MallConst.ROOT_PARENT_ID)){
//                CategoryVo categoryVo=new CategoryVo();
//                BeanUtils.copyProperties(category,categoryVo);
//                categoryVoList.add(categoryVo);
//            }
//        }
        //lambda表达式  lambda+stream  当业务很复杂时，该方式是不适用的  查询一级目录
        List<CategoryVo> categoryVoList = categories.stream().filter(e -> e.getParentId().equals(MallConst.ROOT_PARENT_ID))
                .map(this::category2CategoryVo).collect(Collectors.toList());
        //按sort_order降序排列
        categoryVoList.sort(Comparator.comparing(CategoryVo::getSortOrder).reversed());         //查询子目录
        findSubCategory(categoryVoList,categories);
        return ResponseVo.success(categoryVoList);
    }


    //实现category到CategoryVo的copy
    private CategoryVo category2CategoryVo(Category category){
        CategoryVo categoryVo=new CategoryVo();
        BeanUtils.copyProperties(category,categoryVo);
        return categoryVo;
    }

    //查询子目录

    /**
     *
     * @param categoryVoList  父目录
     * @param categories      子目录
     */
    private void findSubCategory(List<CategoryVo> categoryVoList,List<Category> categories){
        for(CategoryVo categoryVo:categoryVoList){
            List<CategoryVo> subCategoryVoList=new ArrayList<>();
            for (Category category:categories){
                //如果查到内容，要设置subCategory，并继续往下查
                if (categoryVo.getId().equals(category.getParentId())){
                    CategoryVo subCategoryVo = category2CategoryVo(category);
                    subCategoryVoList.add(subCategoryVo);
                }
                //按sort_order降序排列
                subCategoryVoList.sort(Comparator.comparing(CategoryVo::getSortOrder).reversed());
                categoryVo.setSubCategories(subCategoryVoList);//到这只能查到二级目录
                //递归查更多子目录
                findSubCategory(subCategoryVoList,categories);
            }
        }


    }

    @Override
    public void findSubCategoryId(Integer id, Set<Integer> resultSet) {
        List<Category> categories=categoryMapper.selectAll();
        for (Category category:categories){
            if (category.getParentId().equals(id)){
                resultSet.add(category.getId());
                findSubCategoryId(category.getId(),resultSet,categories);//这样写，上面调用selectAll()查询很多次数据库。可以写一个单测的方法看一下
                //为了防止重复读取数据库，所以重载该方法
            }
        }
    }
    /*
    重载该方法之后就只会访问一次数据库了
     */
    private void findSubCategoryId(Integer id, Set<Integer> resultSet,List<Category> categories) {
        for (Category category:categories){
            if (category.getParentId().equals(id)){
                resultSet.add(category.getId());
                findSubCategoryId(category.getId(),resultSet,categories);

            }
        }
    }

}
