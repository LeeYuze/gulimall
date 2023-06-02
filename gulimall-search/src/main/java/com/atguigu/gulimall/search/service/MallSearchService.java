package com.atguigu.gulimall.search.service;

import com.atguigu.gulimall.search.vo.SearchParam;
import com.atguigu.gulimall.search.vo.SearchResult;

/**
 * @author lihaohui
 * @date 2023/6/1
 */
public interface MallSearchService {
    SearchResult search(SearchParam param);
}
