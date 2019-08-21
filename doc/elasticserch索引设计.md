 ## elasticsearch 索引设计
 
 ### 路由表设计
 索引名：ida_index_route
 * agg_type 聚类类型（01 正常,不聚类 02 10分钟聚类等  ）
 * system_type 系统类型（如 app1(lm)、app2(jg)等 以编号代表  ）
 * business__type 业务类型 （访问行为数据(user_vist)，点击行为数据 等）
 * index_name 索引名 （索引名有相应的规则，如 lm_user_vist_20180401008,表示app1的用户访问行为起始时间为2018年4月10日8时开始）
 * start_time 索引数据起始时间， 在新建索引时确定
 * end_time  索引数据起始时间， 在索引数目达到一亿条时，写入些end_time时间
 * size 索引数据大小，每一批次数据写入时更新
 
```json
{
    "ida_index_route":{
        "properties":{
            "agg_type":{
                "type":"keyword"
            },
            "system_type":{
                "type":"keyword"
            },
            "business_type":{
                "type":"keyword"
            },
            "index_name":{
                "type":"keyword"
            },
            "start_time":{
                "type":"date",
                "format":"yyyy-MM-dd HH:mm:ss"
            },
            "end_time":{
                "type":"date",
                "format":"yyyy-MM-dd HH:mm:ss"
            },
            "size":{
                "type":"long"
            }
        }
    }
}
    
```
  
  