## es常用操作

* 参考手册
https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html
https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-body.html
### es常用操作
* 新建索引
```
curl -XPUT 'http://localhost:9200/twitter/doc/1?pretty' -H 'Content-Type: application/json' -d '
{
    "user": "kimchy",
    "post_date": "2009-11-15T13:12:00",
    "message": "Trying out Elasticsearch, so far so good?"
}'
```

*　mapping 信息
URL : http://localhost:9200/test_user_operation20180420/test_user_operation_type/_mapping
Operation: PUT
```    
    {
            "test_user_operation_type":{
                "properties":{
                    "userid":{
                        "type":"long"
                    },
                    "sessionid":{
                        "type":"long"
                    },
                    "noderegionid":{
                        "type":"keyword"
                    },
                    "nodeid":{
                        "type":"long"
                    },
                    "targetaddress":{
                        "type":"keyword"
                    },
                    "targetip":{
                        "type":"keyword"
                    },
                    "targetdomain":{
                        "type":"keyword"
                    },
                    "inbytes":{
                        "type":"long"
                    },
                    "outbytes":{
                        "type":"long"
                    },
                    "costtime":{
                        "type":"long"
                    },
                    "clientip":{
                        "type":"ip"
                    },
                    "firstproxyip":{
                        "type":"ip"
                    },
                    "ipcountry":{
                        "type":"keyword"
                    },
                    "ipprovince":{
                        "type":"keyword"
                    },
                    "ipcity":{
                        "type":"keyword"
                    },
                    "channel":{
                        "type":"keyword"
                    },
                    "version":{
                        "type":"keyword"
                    },
                    "topleveldomain":{
                        "type":"keyword"
                    },
                    "time":{
                        "type":"date"
                    }
                }
            }
    }
```

常用属性记录：
```
{
  "test": {
    "properties": {
      "id": {
        "type": "keyword",
        "index": false,
        "store": false
      },
      "userid": {
        "type": "long"
      }
    }
  }
}

```

URL : http://localhost:9200/test_agg_user_operation20180420/test_agg_user_operation_type/_mapping
Operation: PUT  "analyzer": "keyword"
```
{
    "test_agg_user_operation_type":{
        "properties":{
            "userid":{
                "type":"long"
            },
            "targetdomain":{
                "type":"keyword"
            },
            "userbytes":{
                "type":"long"
            },
            "ipprovince":{
                "type":"keyword"
            },
            "ipcity":{
                "type":"keyword"
            },
            "channel":{
                "type":"text",
                "fielddata":true
            },
            "version":{
                "type":"keyword"
            },
            "ptime":{
                "type":"date",
                "format":"YYYY-MM-DD HH:mm:ss"
            }
        }
    }
}



```

* 索引文档
```

```

### es常用查询

* 基本查询

* search on one index and one type
```
curl -XPUT 'http://localhost:9200/kimchy/doc/1?pretty' -H 'Content-Type: application/json' -d '
{
    "user": "kimchy",
    "post_date": "2009-11-15T13:12:00",
    "message": "Trying out Elasticsearch, so far so good?"
}'
```

* search on more than one
```
curl -XGET 'http://localhost:9200/kimchy,another_user/_search?pretty=true' -H 'Content-Type: application/json' -d '
{
    "query" : {
        "match_all" : {}
    }
}'
```

* on all the indices:
```
curl -XGET 'http://localhost:9200/_search?pretty=true' -H 'Content-Type: application/json' -d '
{
    "query" : {
        "match_all" : {}
    }
}'
```

* 聚合查询

求和统计
```
curl -XGET http://192.168.121.41:9200/test_user_operation20180420/_search

{
  "aggs": {
    "allcity": {
      "terms": {
        "field": "targetdomain",
        "size": 500
      },
      "aggs": {
        "inbytes": {
          "sum": {
            "field": "inbytes"
          }
        }
      }
    }
  }
}

```

按照时间聚合统计
```
http://192.168.121.41:9200/test_user_operation20180420/_search

{
  "aggs": {
    "pubTimeCount": {
      "date_histogram": {
        "field": "time",
        "interval": "hour"
      }
    }
  }
}
```


按照订单状态，查询用户数，总支付金额，价格区间支付次数，支付时间区域次数

```json
{
  "size": 0,
  "query": {
    "term": {
      "orderState": {
        "value": 2,
        "boost": 1
      }
    }
  },
  "_source": false,
  "aggregations": {
    "cardinalityUser": {
      "cardinality": {
        "field": "userId"
      }
    },
    "sumPrice": {
      "sum": {
        "field": "orderPrice"
      }
    },
    "timeRanges": {
      "range": {
        "field": "minOfDay",
        "ranges": [
          {
            "from": 0,
            "to": 180
          },
          {
            "from": 180,
            "to": 360
          },
          {
            "from": 360,
            "to": 540
          },
          {
            "from": 540,
            "to": 720
          },
          {
            "from": 720,
            "to": 900
          },
          {
            "from": 900,
            "to": 1080
          },
          {
            "from": 1080,
            "to": 1240
          },
          {
            "from": 1240,
            "to": 1440
          }
        ]
      }
    },
    "priceRanges": {
      "range": {
        "field": "orderPrice",
        "ranges": [
          {
            "to": 50
          },
          {
            "from": 50,
            "to": 100
          },
          {
            "from": 100,
            "to": 200
          },
          {
            "from": 200,
            "to": 300
          },
          {
            "from": 300,
            "to": 500
          },
          {
            "from": 500
          }
        ]
      }
    }
  }
}

```

根据用户分组按照每个小时进行统计条数
```
{
  "aggs": {
    "allcity": {
      "terms": {
        "field": "userid",
        "size": 500
      },
      "aggs": {
        "pubTimeCount": {
          "date_histogram": {
            "field": "time",
            "interval": "hour"
          }
        }
      }
    }
  }
}

```

按照用户，城市进行分组，统计注入字节数量的和与平均值
```
http://192.168.121.41:9200/test_user_operation20180420/_search
```
{
  "aggs": {
    "allcity": {
      "terms": {
        "field": "userid",
        "size": 500
      },
      "aggs": {
        "citys": {
          "terms": {
            "field": "ipcity"
          },
          "aggs": {
            "sumInbytes": {
              "sum": {
                "field": "inbytes"
              }
            },
            "avgInbytes": {
              "avg": {
                "field": "inbytes"
              }
            }
          }
        }
      }
    }
  }
}

```


```
查询网站访问人数最多的top10
http://localhost:9200/01*/

{
  "query": {
    "term": {
      "domain": "redirector.gvt1.com"
    }
  },
  "aggs": {
    "allcity": {
      "terms": {
        "field": "userId",
        "size": 10
      }
    }
  }
}

```

```
查询网站访问最多的10个城市
http://localhost:9200/01*/
{
  "query": {
    "term": {
      "domain": "redirector.gvt1.com"
    }
  },
  "aggs": {
    "allcity": {
      "terms": {
        "field": "ipCity",
        "size": 10
      }
    }
  }
}

```


```
查询网站被访问最多的10个网站
http://localhost:9200/01*/
{
  "aggs": {
    "allcity": {
      "terms": {
        "field": "domain",
        "size": 10
      }
    }
  }
}

```


```
使用最多的20个用户
http://localhost:9200/01*/
{
  "aggs": {
    "allcity": {
      "terms": {
        "field": "userId",
        "size": 20
      }
    }
  }
}

```

```
查询24小时的用户数与网站查看数
http://localhost:9200/01*/
{
  "aggs": {
    "allcity": {
      "terms": {
        "field": "userId",
        "size": 20
      }
    }
  }
}

```


```
查询24小时的用户数与网站查看数
http://localhost:9200/01*/
{
  "size": 0,
  "aggs": {
    "pubTimeCount": {
      "date_histogram": {
        "field": "connectionTime",
        "interval": "hour"
      },
      "aggs": {
        "user_count": {
          "cardinality": {
            "field": "userId"
          }
        }
      }
    }
  }
}
```

*  查询支付区间次数

```
{
  "query": {
    "bool": {
      "must": [
        {
          "term": {
            "isPayUser": true
          }
        }
      ]
    }
  },
  "aggs": {
    "payHourAgg": {
      "nested": {
        "path": "payHourAgg"
      },
      "aggs": {
        "consumerSum": {
          "terms": {
            "field": "payHourAgg.payHourRange",
            "size": 100
          },
          "aggs": {
            "sumCount": {
              "sum": {
                "field": "payHourAgg.count"
              }
            }
          }
        }
      }
    }
  }
}
```


*  查询每天的网站数量

```
{
  "size": 0,
  "query": {
    "range": {
      "connectionTime": {
        "gte": "2018-05-01 00:00:00",
        "format": "yyyy-MM-dd HH:mm:ss"
      }
      }
    }
  },
  "aggs": {
    "pubTimeCount": {
      "date_histogram": {
        "field": "connectionTime",
       }
     }
   }
}       
```


* 查询最大时间，最小时间
```
{
  "size": 1,
  "aggs": {
    "sumViewCount": {
      "sum": {
        "field": "viewCount"
      }
    },
    "maxPtime": {
      "max": {
        "field": "pTime"
      }
    },
    "minPtime": {
      "min": {
        "field": "pTime"
      }
    }
  }
}
```
* 查询时间范围内，按照小时去统计最常访问的网站
http://isec-hdp01:9200/01-lm-user_connection-201806071808*/
```
{
  "query": {
    "range": {
      "connectionTime": {
        "gte": "2018-05-26 00:00:00",
        "lt": "2018-05-27 00:00:00",
        "format": "yyyy-MM-dd HH:mm:ss"
      }
    }
  },
  "aggs": {
    "pubTimeCount": {
      "date_histogram": {
        "field": "connectionTime",
        "interval": "hour"
      },
      "aggs": {
        "domainView": {
          "terms": {
            "size": 4,
            "field": "domain"
          }
        }
      }
    }
  }
}
```

* 根据聚合后做数据过滤
```
{
  "size": 0,
  "query": {
    "bool": {
      "must": [
        {
          "range": {
            "pTime": {
              "from": "2018-05-25 00:00:00",
              "to": "20188-05-25 23:59:59",
              "format": "yyyy-MM-dd HH:mm:ss"
            }
          }
        },
        {
          "term": {
            "tags": {
              "value": "01-5"
            }
          }
        },
        {
          "term": {
            "tags": {
              "value": "02-yl"
            }
          }
        }
      ]
    }
  },
  "aggregations": {
    "siteTerm": {
      "terms": {
        "field": "domain",
        "size": 9999,
        "min_doc_count": 1
      },
      "aggs": {
        "sumPv": {
          "sum": {
            "field": "viewCount"
          }
        },
        "sales_bucket_filter": {
          "bucket_selector": {
            "buckets_path": {
              "totalViewCount": "sumPv"
            },
            "script": "params.totalViewCount> 200"
          }
        }
      }
    }
  }
}
```


```
查询按天统计的用户数与网站查看数
http://localhost:9200/01*/
{
  "size": 0,
  "aggs": {
    "pubTimeCount": {
      "date_histogram": {
        "field": "connectionTime",
        "interval": "day"
      },
      "aggs": {
        "user_count": {
          "cardinality": {
            "field": "userId"
          }
        }
      }
    }
  }
}
```

* 删除一条记录

curl -X DELETE  http://isec-hdp01:9200/ida_index_route/ida_index_route/oUOfL2MBgedC-cZvzjbb

* 查询所有的索引信息
curl http://isec-hdp02:9200/_cat/indices

* 删除索引
curl -XDELETE http://isec-hdp02:9200/xxx

* 高级查询

http://192.168.121.41:9200/01*/

```


{
  "size": 0,
  "query": {
    "range": {
      "connectionTime": {
        "from": "2018-01-08 00:00:00",
        "to": "2018-01-09 00:00:00",
        "include_lower": true,
        "include_upper": true,
        "format": "yyyy-MM-dd HH:mm:ss",
        "boost": 1
      }
    }
  },
  "_source": false,
  "aggregations": {
    "aggDomain": {
      "terms": {
        "field": "domain",
        "size": 5000,
        "min_doc_count": 1,
        "shard_min_doc_count": 0,
        "show_term_doc_count_error": false,
        "order": [
          {
            "_count": "desc"
          },
          {
            "_key": "asc"
          }
        ]
      },
      "aggregations": {
        "cardinalityUser": {
          "cardinality": {
            "field": "userId"
          }
        }
      }
    }
  }
}


```

nestd 查询 

```
{
  "query": {
    "bool": {
      "must": [
        {
          "term": {
            "userId": 208
          }
        },
        {
          "nested": {
            "path": "payHour",
            "query": {
              "bool": {
                "must": [
                  {
                    "match": {
                      "payHour.payHourRange": "0"
                    }
                  },
                  {
                    "range": {
                      "payHour.count": {
                        "from": 0
                      }
                    }
                  }
                ]
              }
            }
          }
        }
      ]
    }
  }
}


* nest agg 查询
```
{
  "query": {
    "bool": {
      "must": [
        {
          "term": {
            "isPayUser": true
          }
        }
      ]
    }
  },
  "aggs": {
    "payHourAgg": {
      "nested": {
        "path": "payHourAgg"
      },
      "aggs": {
        "consumerSum": {
          "terms": {
            "field": "payHourAgg.payHourRange",
            "size": 100
          },
          "aggs": {
            "sumCount": {
              "sum": {
                "field": "payHourAgg.count"
              }
            }
          }
        }
      }
    }
  }
}
```


{
  "query": {
    "bool": {
      "must": [
        {
          "term": {
            "userId": 222
          }
        },
        {
          "nested": {
            "path": "payPrice",
            "query": {
              "bool": {
                "must": [
                  {
                    "match": {
                      "payPrice.payPriceRange": "200-300"
                    }
                  }
                ]
              }
            },
            "inner_hits": {}
          }
        }
      ]
    }
  }
}

```

* 索引复制
```
http://localhost:9200/_reindex

{

  "source": {
    "index": "old_index"
  },
  "dest": {
    "index": "new_index",
    "op_type": "create"
  }
}
```


* mapping back
{
    "session":{
        "properties":{
            "id":{
                "type":"keyword",
                "index": "false"
            },
            "app_code":{
                "type":"keyword"
            },
            "os":{
                "type":"keyword"
            },
            "channel":{
                "type":"keyword"
            },
            "app_version":{
                "type":"keyword"
            },
            "user_id":{
                "type":"keyword"
            },
            "count":{
                "type":"integer"
            },
            "stat_time":{
                "type":"date",
                "format":"YYYY-MM-DD HH:mm:ss"
            }
        }
    }
}