package com.square.streams.utils

import com.alibaba.fastjson.{JSON, JSONException, JSONObject}

object JsonUtils {
  /** json解析 */
  def parseJson(log: String): JSONObject = {
    var ret: JSONObject = null
    try {
      ret = JSON.parseObject(log)
    } catch {
      //异常json数据处理
      case e: JSONException => println(log)
    }
    ret
  }

}
