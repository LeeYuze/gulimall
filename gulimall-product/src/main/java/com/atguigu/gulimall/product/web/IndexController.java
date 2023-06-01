package com.atguigu.gulimall.product.web;

import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.Catelog2Vo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @author lihaohui
 * @date 2023/5/28
 */
@Controller
@RequiredArgsConstructor
public class IndexController {

    private final CategoryService categoryService;

    @GetMapping({"/","/index.html"})
    public String indexPage(ModelMap modelMap) {
        List<CategoryEntity> categoryEntities = categoryService.listOfLevel1();
        modelMap.addAttribute("categories", categoryEntities);
        return "index";
    }

    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<Long, List<Catelog2Vo>> getCatelogJson() {
        return categoryService.getCatalogJson();
    }

    @ResponseBody
    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }
}
