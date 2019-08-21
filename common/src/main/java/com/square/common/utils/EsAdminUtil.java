package com.square.common.utils;


import com.square.common.Config;
import com.square.common.Constants;
import com.square.common.bean.EdaIndexRoute;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.UUIDs;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.metrics.MaxAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.MinAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ParsedMax;
import org.elasticsearch.search.aggregations.metrics.ParsedMin;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.sort.SortOrder;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;


/**
 * chenxiaogang 2018-10-10
 * v:0.1
 * es管理工具类
 */
public class EsAdminUtil {

    private static RestHighLevelClient client;

    private EsAdminUtil() {
    }

    public static synchronized RestHighLevelClient getRestHighLevelClient() {
        if (client == null) {
            String[] hosts = Config.ES_HOSTS.split(",");
            HttpHost[] HttpHosts = new HttpHost[hosts.length];
            for (int i = 0; i < hosts.length; i++) {
                HttpHosts[i] = new HttpHost(hosts[i], Constants.ES_PORT, Constants.ES_SCHEME);
            }
            RestClientBuilder builder = RestClient.builder(HttpHosts);
            client = new RestHighLevelClient(builder);
        }
        return client;
    }

    /**
     * 关闭client
     *
     * @throws IOException
     */
    public static synchronized void closeClient() throws IOException {
        if (client != null) {
            client.close();
        }
    }

    /**
     * 索引文档
     *
     * @param index      索引名
     * @param type       索引类型
     * @param jsonString json格式数据
     * @return
     * @throws IOException
     */
    public synchronized static String indexDocument(String index, String type, String jsonString) throws IOException {
        RestHighLevelClient client = getRestHighLevelClient();
        IndexRequest request = new IndexRequest(index, type, UUIDs.base64UUID());
        request.opType(DocWriteRequest.OpType.CREATE);
        request.opType("create");
        request.source(jsonString, XContentType.JSON);
        IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
        String id = indexResponse.getId();
        return id;
    }

    /**
     * 批量索引操作
     *
     * @param index 索引名
     * @param type  类型
     * @param docs  批量文档
     * @return
     * @throws IOException
     */
    public synchronized static BulkItemResponse[] bulkIndexDocuments(String index, String type, List<String> docs) throws IOException {
        if (CollectionUtils.isNotEmpty(docs)) {
            RestHighLevelClient client = getRestHighLevelClient();
            BulkRequest bulkRequest = new BulkRequest();
            for (String doc : docs) {
                IndexRequest request = new IndexRequest(index, type, UUIDs.base64UUID());
                request.opType(DocWriteRequest.OpType.CREATE);
                request.opType("create");
                request.source(doc, XContentType.JSON);
                bulkRequest.add(request);
            }
            bulkRequest.timeout(TimeValue.timeValueMinutes(5));
            BulkResponse bulk = client.bulk(bulkRequest, RequestOptions.DEFAULT);
            BulkItemResponse[] items = bulk.getItems();
            return items;
        } else {
            return null;
        }

    }


    /**
     * 根据索引,类型,id查询文档
     *
     * @param index
     * @param type
     * @param id
     * @return
     * @throws IOException
     */
    public static GetResponse getDocument(String index, String type, String id) throws
            IOException {
        RestHighLevelClient client = getRestHighLevelClient();
        GetRequest request = new GetRequest(index, type, id);
        String[] excludes = Strings.EMPTY_ARRAY;
        FetchSourceContext fetchSourceContext = new FetchSourceContext(true, Strings.EMPTY_ARRAY, excludes);
        request.fetchSourceContext(fetchSourceContext);
        GetResponse documentFields = client.get(request, RequestOptions.DEFAULT);
        return documentFields;
    }

    /**
     * 获取索引的大小
     *
     * @param indexName
     * @return
     * @throws IOException
     */
    public static Long getIndexSize(String indexName) throws IOException {

        RestHighLevelClient client = getRestHighLevelClient();
        CountRequest countRequest = new CountRequest(indexName);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        countRequest.source(searchSourceBuilder);
        CountResponse countResponse = client.count(countRequest, RequestOptions.DEFAULT);
        return countResponse.getCount();
    }


    /**
     * 更新记录
     *
     * @param index      索引名
     * @param type       类型名
     * @param id         id
     * @param jsonString 数据json结构
     * @throws IOException
     */
    public static void upsert(String index, String type, String id, String
            jsonString) throws IOException {
        RestHighLevelClient client = getRestHighLevelClient();
        UpdateRequest request = new UpdateRequest(index, type, id);
        request.doc(jsonString, XContentType.JSON);
        client.update(request, RequestOptions.DEFAULT);

    }

    /**
     * 根据索引对应的时间字段名,查询最大时间
     *
     * @param indexName 索引名
     * @param timeField 时间字段名
     * @return
     * @throws IOException
     * @throws ParseException
     */
    public static String queryMaxTime(String indexName, String timeField) throws IOException, ParseException {
        RestHighLevelClient client = getRestHighLevelClient();
        SearchRequest searchRequest = new SearchRequest(indexName);
        MaxAggregationBuilder max = AggregationBuilders.max("agg" + timeField);
        max.field(timeField);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //不显示source信息
        searchSourceBuilder.fetchSource(false);
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchSourceBuilder.aggregation(max);
        searchRequest.source(searchSourceBuilder);
        SearchResponse search = client.search(searchRequest, RequestOptions.DEFAULT);
        Aggregations aggregations = search.getAggregations();
        Map<String, Aggregation> asMap = aggregations.getAsMap();
        Aggregation timeAgg = asMap.get("agg" + timeField);
        ParsedMax maxParsed = (ParsedMax) timeAgg;
        String valueAsString = maxParsed.getValueAsString();
        if (valueAsString.contains("Z")) {
            Date date = DateUtils.dateFromStrUTC(valueAsString);
            valueAsString = DateUtils.format(date, DateUtils.DATE_FULL_FORMAT);
        }
        return valueAsString;
    }

    /**
     * 根据索引对应的时间字段名,查询最小时间
     *
     * @param indexName 索引名
     * @param timeField 时间字段名
     * @return
     * @throws IOException
     * @throws ParseException
     */
    public static String queryMinTime(String indexName, String timeField) throws IOException, ParseException {
        RestHighLevelClient client = getRestHighLevelClient();
        SearchRequest searchRequest = new SearchRequest(indexName);
        MinAggregationBuilder min = AggregationBuilders.min("agg" + timeField);
        min.field(timeField);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //不显示source信息
        searchSourceBuilder.fetchSource(false);
        searchSourceBuilder.aggregation(min);
        searchRequest.source(searchSourceBuilder);
        SearchResponse search = client.search(searchRequest, RequestOptions.DEFAULT);
        Aggregations aggregations = search.getAggregations();
        Map<String, Aggregation> asMap = aggregations.getAsMap();
        Aggregation timeAgg = asMap.get("agg" + timeField);
        ParsedMin minParsed = (ParsedMin) timeAgg;
        String valueAsString = minParsed.getValueAsString();
        if (valueAsString.contains("Z")) {
            Date date = DateUtils.dateFromStrUTC(valueAsString);
            valueAsString = DateUtils.format(date, DateUtils.DATE_FULL_FORMAT);
        }
        return valueAsString;
    }

    /**
     * 查询路由表所有记录,并且组装成EdaIndexRoute对象列表
     *
     * @return
     * @throws IOException
     */
    public static List<EdaIndexRoute> queryAllRouteIndex() throws IOException {
        SearchResponse searchResponse = searchAllIndexRecords();
        SearchHits hits = searchResponse.getHits();
        Long totalHits = hits.getTotalHits().value;
        if (totalHits == 0l) {
            return null;
        } else {
            SearchHit[] hitArrays = hits.getHits();
            List<EdaIndexRoute> routes = new ArrayList<>();
            for (SearchHit hit : hitArrays) {
                EdaIndexRoute route = new EdaIndexRoute();
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                String indexName = (String) sourceAsMap.get(EdaIndexRoute.INDEX_NAME_FIELD);
                String startTime = (String) sourceAsMap.get(EdaIndexRoute.START_TIME_FIELD);
                Object endTime = sourceAsMap.get(EdaIndexRoute.END_TIME_FIELD);
                if (endTime != null) {
                    route.setEndTime(endTime.toString());
                }
                long size = Long.valueOf(sourceAsMap.getOrDefault(EdaIndexRoute.SIZE_FIELD, 0).toString());
                String id = hit.getId();
                String appCode = (String) sourceAsMap.get(EdaIndexRoute.APP_CODE_FIELD);
                String eventCode = (String) sourceAsMap.get(EdaIndexRoute.EVENT_CODE_FIELD);
                route.setId(id);
                route.setAppCode(appCode);
                route.setEventCode(eventCode);
                route.setIndexName(indexName);
                route.setStartTime(startTime);
                route.setSize(size);
                routes.add(route);
            }
            return routes;
        }
    }

    /**
     * 查询路由表所有记录
     *
     * @return
     * @throws IOException
     */
    private static SearchResponse searchAllIndexRecords() throws IOException {
        RestHighLevelClient client = getRestHighLevelClient();
        //构建 searchRequest
        SearchRequest searchRequest = new SearchRequest(Constants.ROUTE_INDEX_NAME);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(10000);
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);
        SearchResponse search = client.search(searchRequest, RequestOptions.DEFAULT);
        return search;
    }

    /**
     * 根据索引名,类型名,mapping文件创建索引
     *
     * @param indexName 索引名
     * @param mapping
     * @throws IOException
     */
    public static void createIndex(String indexName,
                                   String type,
                                   String mapping) throws IOException {
        RestHighLevelClient client = getRestHighLevelClient();
        GetIndexRequest requeste = new GetIndexRequest(indexName);
        boolean exists = client.indices().exists(requeste, RequestOptions.DEFAULT);
        //  判断是否存在不存在再创建
        if (!exists) {
            // index settings
            CreateIndexRequest request = new CreateIndexRequest(indexName);
            request.settings(Settings.builder()
                    .put("index.number_of_shards", 5)
                    .put("index.number_of_replicas", 2)
                    .put("index.max_result_window", 1000000)
                    .put("index.blocks.read_only_allow_delete", false));
            request.mapping(type, mapping, XContentType.JSON);
            //同步请求执行
            client.indices().create(request, RequestOptions.DEFAULT);
        }

    }


    public static String queryRouteIndexNames(String appCode,
                                              String eventCode,
                                              String beginTime,
                                              String endTime) throws IOException {

        List<EdaIndexRoute> routes = queryRouteIndex(appCode, eventCode, beginTime, endTime);
        List<String> indexs = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(routes)) {
            for (EdaIndexRoute route : routes) {
                indexs.add(route.getIndexName());
            }
        }
        String joinNames = StringUtils.join(indexs, ",");
        return joinNames;
    }

    public static String queryAllRouteIndexNames(String eventCode,
                                                 String beginTime,
                                                 String endTime) throws IOException {
        List<EdaIndexRoute> routes = queryAllRouteIndex(eventCode, beginTime, endTime);
        List<String> indexs = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(routes)) {
            for (EdaIndexRoute route : routes) {
                indexs.add(route.getIndexName());
            }
        }
        String joinNames = StringUtils.join(indexs, ",");
        return joinNames;
    }

    /**
     * 根据开始时间或者结束时间 查询路由表中的索引
     */
    public static List<EdaIndexRoute> queryRouteIndexs(String beginTime, String endTime) throws IOException {
        RestHighLevelClient restHighLevelClient = getRestHighLevelClient();
        //构建 searchRequest
        SearchRequest searchRequest = new SearchRequest(Constants.ROUTE_INDEX_NAME);
        //构建searchSourceBulider
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //通过bool来进行查询条件组件
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //开始时间,结束时间不为空 则需要查询为: 查询的起始时间在数据的开始时间与结束时间之间
        updateQuery(beginTime, endTime, boolQueryBuilder);
        //按照时间排序
        searchSourceBuilder.sort(EdaIndexRoute.START_TIME_FIELD, SortOrder.DESC).size(10000);
        searchSourceBuilder.query(boolQueryBuilder);
        //完成最终查询条件组件
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = searchResponse.getHits();
        long totalHits = hits.getTotalHits().value;
        if (totalHits == 0l) {
            return null;
        }
        SearchHit[] hitArrays = hits.getHits();
        List<EdaIndexRoute> routes = new ArrayList<>();
        for (SearchHit hit : hitArrays) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String appCode = Objects.toString(sourceAsMap.get(EdaIndexRoute.APP_CODE_FIELD));
            String eventCode = Objects.toString(sourceAsMap.get(EdaIndexRoute.EVENT_CODE_FIELD));
            String indexName = Objects.toString(sourceAsMap.get(EdaIndexRoute.INDEX_NAME_FIELD));
            String begin = Objects.toString(sourceAsMap.get(EdaIndexRoute.START_TIME_FIELD));
            String end = Objects.toString(sourceAsMap.getOrDefault(EdaIndexRoute.END_TIME_FIELD, ""));
            EdaIndexRoute route = new EdaIndexRoute();
            route.setId(hit.getId());
            route.setAppCode(appCode);
            route.setEventCode(eventCode);
            route.setIndexName(indexName);
            route.setStartTime(begin);
            route.setEndTime(end);
            routes.add(route);
        }
        return routes;
    }

    /**
     * 根据appCode、eventCode、时间起\止 查询可用的索引
     *
     * @param appCode
     * @param eventCode
     * @param beginTime
     * @param endTime
     * @return
     * @throws IOException
     */
    public static List<EdaIndexRoute> queryRouteIndex(
            String appCode,
            String eventCode,
            String beginTime,
            String endTime) throws
            IOException {
        RestHighLevelClient restHighLevelClient = getRestHighLevelClient();
        SearchResponse searchResponse = queryIndexRoutesByTime (restHighLevelClient, appCode, eventCode, beginTime, endTime);
        SearchHits hits = searchResponse.getHits();
        long totalHits = hits.getTotalHits().value;
        if (totalHits == 0l) {
            return null;
        } else {
            SearchHit[] hitArrays = hits.getHits();
            List<EdaIndexRoute> routes = new ArrayList<>();
            for (SearchHit hit : hitArrays) {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                String indexName = (String) sourceAsMap.get(EdaIndexRoute.INDEX_NAME_FIELD);
                String begin = (String) sourceAsMap.get(EdaIndexRoute.START_TIME_FIELD);
                String end = (String) sourceAsMap.getOrDefault(EdaIndexRoute.END_TIME_FIELD, "");
                long size = Long.valueOf(sourceAsMap.getOrDefault(EdaIndexRoute .SIZE_FIELD, 0l).toString());
                EdaIndexRoute route = new EdaIndexRoute();
                route.setId(hit.getId());
                route.setAppCode(appCode);
                route.setEventCode(eventCode);
                route.setIndexName(indexName);
                route.setStartTime(begin);
                route.setEndTime(end);
                route.setSize(size);
                routes.add(route);
            }
            return routes;
        }
    }

    /**
     * 根据 eventCode、时间起\止 查询可用的索引
     *
     * @param eventCode
     * @param beginTime
     * @param endTime
     * @return
     * @throws IOException
     */
    public static List<EdaIndexRoute> queryAllRouteIndex(
            String eventCode,
            String beginTime,
            String endTime) throws
            IOException {
        RestHighLevelClient restHighLevelClient = getRestHighLevelClient();
        SearchResponse searchResponse = queryAllIndexRoutesByTime(restHighLevelClient, eventCode, beginTime, endTime);
        SearchHits hits = searchResponse.getHits();
        if (hits == null) {
            return null;
        } else {
            SearchHit[] hitArrays = hits.getHits();
            List<EdaIndexRoute> routes = new ArrayList<>();
            for (SearchHit hit : hitArrays) {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                String indexName = (String) sourceAsMap.get(EdaIndexRoute.INDEX_NAME_FIELD);
                String begin = (String) sourceAsMap.get(EdaIndexRoute.START_TIME_FIELD);
                String end = (String) sourceAsMap.getOrDefault(EdaIndexRoute.END_TIME_FIELD, "");
                long size = Long.valueOf(sourceAsMap.getOrDefault(EdaIndexRoute.SIZE_FIELD, 0l).toString());
                EdaIndexRoute route = new EdaIndexRoute();
                route.setId(hit.getId());
                route.setEventCode(eventCode);
                route.setIndexName(indexName);
                route.setStartTime(begin);
                route.setEndTime(end);
                route.setSize(size);
                routes.add(route);
            }
            return routes;
        }
    }

    /**
     * 根据appCode,事件code,开始时间,结束时间,查找数据
     *
     * @param eventCode
     * @param beginTime
     * @param endTime
     * @return
     */
    private static SearchResponse queryAllIndexRoutesByTime(
            RestHighLevelClient restHighLevelClient,
            String eventCode,
            String beginTime,
            String endTime
    ) throws IOException {
        //构建 searchRequest
        SearchRequest searchRequest = new SearchRequest(Constants.ROUTE_INDEX_NAME);
        //构建searchSourceBulider
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //通过bool来进行查询条件组件
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //构建 appCode,事件code和时间范围的查询条件
        TermQueryBuilder eventCodeQuery = QueryBuilders.termQuery(EdaIndexRoute.EVENT_CODE_FIELD, eventCode);
        boolQueryBuilder.must(eventCodeQuery);
        //开始时间,结束时间不为空 则需要查询为: 查询的起始时间在数据的开始时间与结束时间之间
        updateQuery(beginTime, endTime, boolQueryBuilder);
        //按照时间排序
        searchSourceBuilder.sort(EdaIndexRoute.START_TIME_FIELD, SortOrder.DESC).size(1000);
        searchSourceBuilder.query(boolQueryBuilder);
        //完成最终查询条件组件
        searchRequest.source(searchSourceBuilder);
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        return search;
    }

    /**
     * 根据appCode,事件code,开始时间,结束时间,查找数据
     *
     * @param appCode
     * @param eventCode
     * @param beginTime
     * @param endTime
     * @return
     */
    private static SearchResponse queryIndexRoutesByTime(
            RestHighLevelClient restHighLevelClient,
            String appCode,
            String eventCode,
            String beginTime,
            String endTime
    ) throws IOException {
        //构建 searchRequest
        SearchRequest searchRequest = new SearchRequest(Constants.ROUTE_INDEX_NAME);
        //构建searchSourceBulider
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //通过bool来进行查询条件组件
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //构建 appCode,事件code和时间范围的查询条件
        TermQueryBuilder appCodeQuery = QueryBuilders.termQuery(EdaIndexRoute.APP_CODE_FIELD, appCode);
        TermQueryBuilder eventCodeQuery = QueryBuilders.termQuery(EdaIndexRoute.EVENT_CODE_FIELD, eventCode);
        boolQueryBuilder.must(appCodeQuery).must(eventCodeQuery);
        //开始时间,结束时间不为空 则需要查询为: 查询的起始时间在数据的开始时间与结束时间之间
        updateQuery(beginTime, endTime, boolQueryBuilder);
        //按照时间排序
        searchSourceBuilder.sort(EdaIndexRoute.START_TIME_FIELD, SortOrder.DESC).size(1000);
        searchSourceBuilder.query(boolQueryBuilder);
        //完成最终查询条件组件
        searchRequest.source(searchSourceBuilder);
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        return search;
    }

    private static void updateQuery(String beginTime, String endTime, BoolQueryBuilder boolQueryBuilder) {
        if (StringUtils.isNotBlank(beginTime) && StringUtils.isNotBlank(endTime)) {
            BoolQueryBuilder condition = constructCondition(beginTime, endTime);
            boolQueryBuilder.must(condition);
        } else if (StringUtils.isNotBlank(beginTime) && StringUtils.isBlank(endTime)) {
            //开始时间不为空,结束时间为空
            BoolQueryBuilder condition = constructBeginTimeCondition(beginTime);
            boolQueryBuilder.must(condition);
        } else if (StringUtils.isBlank(beginTime) && StringUtils.isNotBlank(endTime)) {
            //开始时间为空,结束时间不为空, 数据开始时间小于查询结束时间
            RangeQueryBuilder startTimeRange = QueryBuilders.rangeQuery(EdaIndexRoute.START_TIME_FIELD);
            startTimeRange.to(endTime).format(DateUtils.DATE_FULL_FORMAT);
            boolQueryBuilder.must(startTimeRange);
        }
    }

    private static BoolQueryBuilder constructCondition(String beginTime, String endTime) {
        BoolQueryBuilder conditionBoolQueryBuilder = QueryBuilders.boolQuery();
        // 1,数据的开始时间小于查询的结束时间
        RangeQueryBuilder startTimeQuery = QueryBuilders.rangeQuery(EdaIndexRoute.START_TIME_FIELD);
        startTimeQuery.to(endTime).format(DateUtils.DATE_FULL_FORMAT);

        // 2, 如果数据没有结束时间时 数据的结束时间大于查询的开始时间
        BoolQueryBuilder noExistsEndTimeBoolQueryBuilder = QueryBuilders.boolQuery();
        // 3, 如果数据有结束时间时 数据的结束时间大于查询的开始时间
        BoolQueryBuilder existsEndTimeBoolQueryBuilder = QueryBuilders.boolQuery();

        RangeQueryBuilder endTimeQuery = QueryBuilders.rangeQuery(EdaIndexRoute.END_TIME_FIELD);
        endTimeQuery.from(beginTime).format(DateUtils.DATE_FULL_FORMAT);
        ExistsQueryBuilder existsEndTimeQuery = QueryBuilders.existsQuery(EdaIndexRoute.END_TIME_FIELD);

        existsEndTimeBoolQueryBuilder.must(existsEndTimeQuery).must(startTimeQuery).must(endTimeQuery);

        noExistsEndTimeBoolQueryBuilder.mustNot(existsEndTimeQuery).must(startTimeQuery);

        conditionBoolQueryBuilder.should(noExistsEndTimeBoolQueryBuilder).should(existsEndTimeBoolQueryBuilder);
        return conditionBoolQueryBuilder;
    }

    /**
     * 根据开始时间,组装路由查询条件
     *
     * @param beginTime
     * @return
     */
    private static BoolQueryBuilder constructBeginTimeCondition(String beginTime) {
        BoolQueryBuilder conditionBoolQueryBuilder = QueryBuilders.boolQuery();

        // 1,数据的结束时间大于查询的开始时间
        RangeQueryBuilder endTimeQuery = QueryBuilders.rangeQuery(EdaIndexRoute.END_TIME_FIELD);
        endTimeQuery.from(beginTime).format(DateUtils.DATE_FULL_FORMAT);
        // 2, 如果数据的结束时间为空时,需要的条件时,数据的开始时间大于小于查询开始时间
        //并且结束时间为空
        BoolQueryBuilder noExistsEndTimeBoolQueryBuilder = QueryBuilders.boolQuery();

        ExistsQueryBuilder existsEndTimeQuery = QueryBuilders.existsQuery(EdaIndexRoute.END_TIME_FIELD);
        noExistsEndTimeBoolQueryBuilder.mustNot(existsEndTimeQuery);

        conditionBoolQueryBuilder.should(endTimeQuery).should(noExistsEndTimeBoolQueryBuilder);
        return conditionBoolQueryBuilder;
    }

    /**
     * 获取索引的大小
     *
     * @return
     * @throws IOException
     */
    public static Long getUniqueIdSize(String appCode,
                                       String eventCode,
                                       String createTime,
                                       String uniqueId) throws IOException {

        String beginTime =DateUtils.addDay(createTime,-1);
        String endTime=DateUtils.addDay(createTime,1);
        String indexs = queryRouteIndexNames( appCode, eventCode, beginTime, endTime);
        CountRequest countRequest = new CountRequest(indexs);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchSourceBuilder.query(QueryBuilders.termQuery("unique_id",uniqueId));
        countRequest.source(searchSourceBuilder);
        CountResponse countResponse = client.count(countRequest, RequestOptions.DEFAULT);
        return countResponse.getCount();
    }


    public static void main(String[] args) {

        try {

            System.out.println(getUniqueIdSize("redbonus","sk_bill","2019-07-05 01:00:53","fy_pig_test_22253dd"));
            createIndex(Constants.ROUTE_INDEX_NAME, Constants.ROUTE_TYPE_NAME, Constants.ROUTE_INDEX_MAPPING);
            EsAdminUtil.closeClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
