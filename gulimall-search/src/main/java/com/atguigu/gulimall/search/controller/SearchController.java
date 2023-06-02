package com.atguigu.gulimall.search.controller;

import com.atguigu.gulimall.search.service.MallSearchService;
import com.atguigu.gulimall.search.vo.SearchParam;
import com.atguigu.gulimall.search.vo.SearchResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author lihaohui
 * @date 2023/6/1
 */
@Controller
@RequiredArgsConstructor
public class SearchController {

    private final MallSearchService mallSearchService;

    @GetMapping("/list.html")
    public String list(SearchParam searchParam, ModelMap modelMap, HttpServletRequest request) {
        String queryString = request.getQueryString();
        searchParam.set_queryString(queryString);

        SearchResult search = mallSearchService.search(searchParam);
        modelMap.addAttribute("result", search);
        return "list";
    }
}
