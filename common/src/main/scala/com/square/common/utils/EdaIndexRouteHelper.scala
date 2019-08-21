package com.square.common.utils

import com.square.common.bean.EdaIndexRoute
import org.apache.commons.collections.CollectionUtils

import scala.collection.JavaConversions._
import scala.collection.mutable


/**
  * 路由索引缓存
  * Created by chenxiaogang on 18/4/25 0025.
  * version: 0.1
  */
object EdaIndexRouteHelper extends Serializable {
	
	//获得所有的ROUTE信息
	def getAllRouteIndex(): Map[String, mutable.Buffer[EdaIndexRoute]] = {
		var allIdaIndexRoutes = EsAdminUtil.queryAllRouteIndex()

		if (CollectionUtils.isNotEmpty(allIdaIndexRoutes)) {
			allIdaIndexRoutes.groupBy(route => {
				route.getAppCode + "_" + route.getEventCode
			})
		} else {
			Map()
		}
	}
	
	
}
