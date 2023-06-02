package com.atguigu.gulimall.search.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.*;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.elasticsearch.core.search.SourceConfig;
import co.elastic.clients.json.JsonData;
import com.alibaba.fastjson2.TypeReference;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.search.constant.EsConstant;
import com.atguigu.gulimall.search.feign.ProductFeignService;
import com.atguigu.gulimall.search.service.MallSearchService;
import com.atguigu.gulimall.search.vo.AttrResponseVo;
import com.atguigu.gulimall.search.vo.BrandVo;
import com.atguigu.gulimall.search.vo.SearchParam;
import com.atguigu.gulimall.search.vo.SearchResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lihaohui
 * @date 2023/6/1
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MallSearchServiceImpl implements MallSearchService {

    private final ElasticsearchClient esClient;

    private final ProductFeignService productFeignService;

    @Override
    public SearchResult search(SearchParam param) {

        SearchResult searchResult = null;
        try {
            SearchResponse<SkuEsModel> searchResponse = buildSearchResponse(param);
            searchResult = buildSearchResult(searchResponse, param);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return searchResult;
    }

    private SearchResult buildSearchResult(SearchResponse<SkuEsModel> response, SearchParam param) {
        SearchResult result = new SearchResult();

        HitsMetadata<SkuEsModel> hits = response.hits();
        List<SkuEsModel> skuEsModels = new ArrayList<>();
        for (Hit<SkuEsModel> hit : hits.hits()) {
            SkuEsModel skuEsModel = hit.source();

            if (StringUtils.hasLength(param.getKeyword())) {
                List<String> skuTitle = hit.highlight().get("skuTitle");
                assert skuEsModel != null;
                skuEsModel.setSkuTitle(skuTitle.get(0));
            }

            skuEsModels.add(skuEsModel);
        }
        result.setProducts(skuEsModels);

        List<SearchResult.AttrVo> attrs = new ArrayList<>();
        Aggregate attrAgg = response.aggregations().get("attr_agg");
        Aggregate attrIdAgg = attrAgg.nested().aggregations().get("attr_id_agg");
        for (LongTermsBucket bucket : attrIdAgg.lterms().buckets().array()) {
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
            long attrId = bucket.key();
            attrVo.setAttrId(attrId);

            String attrName = bucket.aggregations().get("attr_name_agg").sterms().buckets().array().get(0).key().stringValue();
            attrVo.setAttrName(attrName);

            List<String> attrValues = bucket.aggregations().get("attr_value_agg").sterms().buckets().array().stream().map(StringTermsBucket::key).map(FieldValue::stringValue).collect(Collectors.toList());
            attrVo.setAttrValue(attrValues);

            attrs.add(attrVo);
        }
        result.setAttrs(attrs);

        List<SearchResult.BrandVo> brandVos = new ArrayList<>();
        Aggregate brandAgg = response.aggregations().get("brand_agg");
        for (LongTermsBucket bucket : brandAgg.lterms().buckets().array()) {
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
            long brandId = bucket.key();
            brandVo.setBrandId(brandId);

            Aggregate brandImgAgg = bucket.aggregations().get("brand_img_agg");
            brandVo.setBrandImg(brandImgAgg.sterms().buckets().array().get(0).key().stringValue());

            Aggregate brandNameAgg = bucket.aggregations().get("brand_name_agg");
            brandVo.setBrandName(brandNameAgg.sterms().buckets().array().get(0).key().stringValue());

            brandVos.add(brandVo);
        }
        result.setBrands(brandVos);

        List<SearchResult.CatalogVo> catalogVos = new ArrayList<>();
        Aggregate catalogAgg = response.aggregations().get("catalog_agg");
        for (LongTermsBucket bucket : catalogAgg.lterms().buckets().array()) {
            SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
            catalogVo.setCatalogId(bucket.key());

            Aggregate catalogNameAgg = bucket.aggregations().get("catalog_name_agg");
            catalogVo.setCatalogName(catalogNameAgg.sterms().buckets().array().get(0).key().stringValue());
            catalogVos.add(catalogVo);
        }
        result.setCatalogs(catalogVos);

        result.setPageNum(param.getPageNum());
        long total = hits.total().value();
        result.setTotal(total);
        int totalPages = (int) total % EsConstant.PRODUCT_PAGESIZE == 0 ? (int) total / EsConstant.PRODUCT_PAGESIZE : (int) (total / EsConstant.PRODUCT_PAGESIZE + 1);
        result.setTotalPages(totalPages);

        //页码导航
        List<Integer> pageNavs = new ArrayList<>();
        for (int i = 1; i <= totalPages; i++) {
            pageNavs.add(i);
        }
        result.setPageNavs(pageNavs);

        if (!Objects.isNull(param.getAttrs()) && param.getAttrs().size() > 0) {
            List<SearchResult.NavVo> navVos = param.getAttrs().stream().map(attr -> {
                SearchResult.NavVo navVo = new SearchResult.NavVo();
                //1 分析每个attrs传过来的查询参数值
                //attrs=2_5寸:6寸
                String[] s = attr.split("_");
                navVo.setNavValue(s[1]);
                Long attrId = Long.parseLong(s[0]);
                R r = productFeignService.attrInfo(attrId);
                if (r.getCode() == 0) {
                    AttrResponseVo data = r.getData("attr", new TypeReference<AttrResponseVo>() {
                    });

                    navVo.setNavName(data.getAttrName());
                } else {
                    navVo.setNavName(s[0]);
                }

                String replace = replaceQueryString(param, attr, "attrs");
                navVo.setLink("http://search.mall.10686.top/list.html?" + replace);

                result.getAttrIds().add(attrId);
                return navVo;
            }).collect(Collectors.toList());
            result.setNavs(navVos);
        }

        if (!Objects.isNull(param.getBrandId()) && param.getBrandId().size() > 0) {
            List<SearchResult.NavVo> navs = result.getNavs();
            SearchResult.NavVo navVo = new SearchResult.NavVo();
            navVo.setNavName("品牌");

            R r = productFeignService.brandsInfo(param.getBrandId());
            if (r.getCode() == 0) {
                List<BrandVo> brands = r.getData("brand", new TypeReference<List<BrandVo>>() {
                });
                StringBuffer buffer = new StringBuffer();
                String replace = "";
                for (BrandVo brandVo : brands) {
                    buffer.append(brandVo.getName() + ";");
                    replace = replaceQueryString(param, brandVo.getBrandId() + "", "brandId");
                }
                navVo.setNavValue(buffer.toString());
                navVo.setLink("http://search.mall.10686.top/list.html?" + replace);
            }
            navs.add(navVo);
        }

        return result;
    }

    private SearchResponse<SkuEsModel> buildSearchResponse(SearchParam param) throws IOException {

        return esClient.search(builder ->
        {
            builder
                    .index(EsConstant.PRODUCT_INDEX)
                    .query(queryBuild -> {
                        queryBuild.bool(boolBuild -> {
                            /**
                             * 模糊匹配 过滤 按照属性 分类 品牌 价格区间 库存
                             */
                            if (StringUtils.hasLength(param.getKeyword())) {
                                boolBuild.must(must -> must.match(match -> match.field("skuTitle").query(param.getKeyword())));
                            }

                            if (!Objects.isNull(param.getCatalog3Id())) {
                                boolBuild.filter(f -> f.term(term -> term.field("catalogId").value(param.getCatalog3Id())));
                            }

                            if (!Objects.isNull(param.getBrandId()) && param.getBrandId().size() > 0) {
                                boolBuild.filter(filterBuilder -> filterBuilder.terms(terms -> terms.field("brandId").terms(v -> v.value(param.getBrandId().stream().map(FieldValue::of).collect(Collectors.toList())))));
                            }

                            if (!Objects.isNull(param.getAttrs()) && param.getAttrs().size() > 0) {
                                boolBuild.filter(filterBuilder -> {
                                    NestedQuery.Builder nestedQueryBuilder = new NestedQuery.Builder();

                                    nestedQueryBuilder.path("attrs");

                                    Query.Builder query = new Query.Builder();

                                    BoolQuery.Builder boolQuery = new BoolQuery.Builder();
                                    boolQuery.must(m -> {
                                        for (String attrStr : param.getAttrs()) {
                                            //attrs=1_5寸:8寸&attrs=2_16G:8G
                                            String[] s = attrStr.split("_");
                                            String attrId = s[0];
                                            String[] attrValues = s[1].split(":");
                                            m.term(t -> t.field("attrs.attrId").value(attrId));
                                            m.terms(t -> t.field("attrs.attrValue").terms(v -> v.value(Arrays.stream(attrValues).map(FieldValue::of).collect(Collectors.toList()))));
                                        }
                                        return m;
                                    });
                                    query.bool(boolQuery.build());

                                    nestedQueryBuilder.query(query.build());
                                    filterBuilder.nested(nestedQueryBuilder.build());
                                    return filterBuilder;
                                });
                            }

                            if (!Objects.isNull(param.getHasStock())) {
                                boolBuild.filter(filterBuilder -> filterBuilder.term(t -> t.field("hasStock").value(param.getHasStock() == 1)));
                            }

                            //1_500   _500  500_
                            if (StringUtils.hasLength(param.getSkuPrice())) {
                                String[] priceSp = param.getSkuPrice().split("_");
                                boolBuild.filter(filterBuilder -> filterBuilder.range(r -> {
                                    r.field("skuPrice");

                                    if (param.getSkuPrice().startsWith("_")) {
                                        r.lte(JsonData.of(priceSp[1]));
                                    } else if (param.getSkuPrice().endsWith("_")) {
                                        r.gte(JsonData.of(priceSp[0]));
                                    } else if (priceSp.length == 2) {
                                        r.gte(JsonData.of(priceSp[0])).lte(JsonData.of(priceSp[1]));
                                    }

                                    return r;
                                }));
                            }

                            return boolBuild;
                        });

                        return queryBuild;
                    })
                    .size(EsConstant.PRODUCT_PAGESIZE)
                    .from((param.getPageNum() - 1) * EsConstant.PRODUCT_PAGESIZE)
                    .highlight(h -> h.fields("skuTitle", v -> v.preTags("<b style='color:red'>").postTags("</b>")))
                    .aggregations("brand_agg", agg -> agg
                            .terms(TermsAggregation.of(t -> t.field("brandId").size(10)))
                            .aggregations("brand_name_agg", subAgg -> subAgg.terms(TermsAggregation.of(t -> t.field("brandName").size(10))))
                            .aggregations("brand_img_agg", subAgg -> subAgg.terms(TermsAggregation.of(t -> t.field("brandImg").size(10))))
                    )
                    .aggregations("catalog_agg", agg -> agg
                            .terms(TermsAggregation.of(t -> t.field("catalogId").size(10)))
                            .aggregations("catalog_name_agg", subAgg -> subAgg.terms(TermsAggregation.of(t -> t.field("catalogName").size(10))))
                    )
                    .aggregations("attr_agg", agg -> agg
                            .nested(n -> n.path("attrs"))
                            .aggregations("attr_id_agg", subAgg -> subAgg.terms(t -> t.field("attrs.attrId").size(10))
                                    .aggregations("attr_name_agg", gsonAgg -> gsonAgg.terms(t -> t.field("attrs.attrName").size(10)))
                                    .aggregations("attr_value_agg", gsonAgg -> gsonAgg.terms(t -> t.field("attrs.attrValue").size(10)))
                            )
                    );
            if (StringUtils.hasLength(param.getSort())) {
                builder.sort(sort -> {
                    String[] s = param.getSort().split("_");
                    SortOrder order = s[1].equalsIgnoreCase("asc") ? SortOrder.Asc : SortOrder.Desc;
                    sort.field(f -> f.field(s[0]).order(order));
                    return sort;
                });
            }
            return builder;
        }, SkuEsModel.class);
    }

    private String replaceQueryString(SearchParam param, String value, String key) {
        String encode = "";
        try {
            encode = URLEncoder.encode(value, "UTF-8");
            //+ 对应浏览器的%20编码
            encode = encode.replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return param.get_queryString().replace("&" + key + "=" + encode, "");
    }
}
