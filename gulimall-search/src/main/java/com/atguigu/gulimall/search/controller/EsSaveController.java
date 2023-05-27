package com.atguigu.gulimall.search.controller;

import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.search.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * @author lihaohui
 * @date 2023/5/27
 */
@RestController
@RequestMapping("/search/save")
@RequiredArgsConstructor
public class EsSaveController {

    private final ProductService productService;

    @PostMapping("/product")
    public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels){

        boolean res = productService.up(skuEsModels);
        if (!res) {
            R.error();
        }

        return R.ok();
    }

}
