package com.imooc.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.mall.dao.ProductMapper;
import com.imooc.mall.enums.ProductStatusEnum;
import com.imooc.mall.enums.ResponseEnum;
import com.imooc.mall.pojo.Product;
import com.imooc.mall.service.IProductService;
import com.imooc.mall.vo.ProductDetailVo;
import com.imooc.mall.vo.ProductVo;
import com.imooc.mall.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductService implements IProductService {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductMapper productMapper;

    @Override
    public ResponseVo<PageInfo> list(Integer categoryId, Integer pageNum, Integer pageSize) {
        //首先按照Id查找其所有子类id
        Set<Integer> categoryIdSet=new HashSet<>();
        if (categoryId!=null){
            categoryService.findSubCategoryId(categoryId,categoryIdSet);
            //其次要把该Id加进来
            categoryIdSet.add(categoryId);
        }
        //List<Product> products =  productMapper.selectByCategoryIdSet(categoryIdSet.size()==0?null:categoryIdSet);//第一种方式，另外一种是在mapper.xml里面修改
//        List<Product> products =  productMapper.selectByCategoryIdSet(categoryIdSet);在测试完能否正确输出接口文件的list之后继续但是按照下行形式来进行
        List<Product> productList = productMapper.selectByCategoryIdSet(categoryIdSet);
        List<ProductVo> productVosList = productList.stream().map(e -> {
            ProductVo productVo = new ProductVo();
            BeanUtils.copyProperties(e, productVo);
            return productVo;
        }).collect(Collectors.toList());
        PageHelper.startPage(pageNum,pageSize);//分页 pageNum是页数  pageSize是每一页有多少个
        //log.info("products={}",products);在测试完能否正确输出接口文件的list就可以不要了
        //通过categoryId来查询商品，在mapper创建该查询方法。返回的是包括当前id

        PageInfo pageInfo=new PageInfo<>(productList);
        pageInfo.setList(productVosList);
        return ResponseVo.success(pageInfo);
    }

    @Override
    public ResponseVo<ProductDetailVo> detail(Integer productId) {
        Product product=productMapper.selectByPrimaryKey(productId);
        //不推荐使用商品状态不等于在售。在判断时只对确定性的条件判断。因为以后可能会加其他状态
        if (product.getStatus().equals(ProductStatusEnum.OFF_SALE) || product.getStatus().equals(ProductStatusEnum.DELETE)){
            return ResponseVo.error(ResponseEnum.PRODUCT_OFFSALE_OR_DELETE);
        }
        //库存就是stock是不希望别人看到的。需要设置一下(敏感数据处理)
        product.setStock(product.getStock()>100?100:product.getStock());
        ProductDetailVo productDetailVo=new ProductDetailVo();
        BeanUtils.copyProperties(product,productDetailVo);
        return ResponseVo.success(productDetailVo);
    }
}
