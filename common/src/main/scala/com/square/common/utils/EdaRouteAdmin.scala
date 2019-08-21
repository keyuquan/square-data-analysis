package com.square.common.utils

import java.util
import java.util.Date

import com.google.gson.Gson
import com.square.common.Constants
import com.square.common.bean.EdaIndexRoute
import org.apache.commons.lang.StringUtils
import org.elasticsearch.action.get.GetResponse
import org.slf4j.LoggerFactory

import scala.collection.immutable

/**
  *
  * @author cxg
  *         2018-04-28 11:41
  * @version 1.0.0
  */
object EdaRouteAdmin {

  val logger = LoggerFactory.getLogger(EdaRouteAdmin.getClass)

  def createEdaRouteIndex(): Unit = {
    try
      createIndex(Constants.ROUTE_INDEX_NAME, Constants.ROUTE_TYPE_NAME, Constants.ROUTE_INDEX_MAPPING)
    catch {
      case e: Exception =>
        logger.info(e.toString)
    }

  }

  /**
    * 根据 系统类型,业务类型来找到当前使用的索引
    *
    * @param appCode
    * @param eventCode
    * @param maping
    * @return
    */
  def getOrCreateIndex(appCode: String,
                       eventCode: String,
                       maping: String): EdaIndexRoute = {
    val key = appCode + "_" + eventCode
    val routesGroup = EdaIndexRouteHelper.getAllRouteIndex()
    val routes = routesGroup.get(key).toList.flatten
    val filterRoutes: immutable.Seq[EdaIndexRoute] = routes.filter(p => {
      StringUtils.isBlank(p.getEndTime)
    })
    if (routes.isEmpty || filterRoutes.isEmpty) {
      val currentDate = new Date();
      var lastYear = DateUtils.addYear(currentDate, -1)
      val endDate = DateUtils.format(new Date(), DateUtils.DATE_TIGHT__FORMAT)
      val indexName = appCode + "_" + eventCode + "_" + endDate;

      //新建新索引
      EsAdminUtil.createIndex(indexName, eventCode, maping)
      var jsonMap = new util.HashMap[String, Object]()
      jsonMap.put(EdaIndexRoute.APP_CODE_FIELD, appCode)
      jsonMap.put(EdaIndexRoute.EVENT_CODE_FIELD, eventCode)
      jsonMap.put(EdaIndexRoute.INDEX_NAME_FIELD, indexName)
      jsonMap.put(EdaIndexRoute.START_TIME_FIELD, DateUtils.format(lastYear,DateUtils.DATE_FULL_FORMAT))
      jsonMap.put(EdaIndexRoute.SIZE_FIELD, String.valueOf(0))
      val json: Gson = new Gson
      val jsonStr: String = json.toJson(jsonMap)
      val id = EsAdminUtil.indexDocument(Constants.ROUTE_INDEX_NAME, Constants.ROUTE_TYPE_NAME, jsonStr);
      val currentRouteResponse = EsAdminUtil.getDocument(Constants.ROUTE_INDEX_NAME, Constants.ROUTE_TYPE_NAME, id)
      parsetIdaRoute(currentRouteResponse)
    } else {
      var currentRoute = filterRoutes(0)
      //如果超过限定大小,则新建索引
      if (currentRoute == null || currentRoute.getSize >= Constants.INDEX_MAX_SIZE) {
        val currentRouteResponse = createIndex(appCode, eventCode, maping)
        currentRoute = parsetIdaRoute(currentRouteResponse)
      }
      currentRoute
    }
  }

  /**
    * 根据 系统类型,业务类型来找到当前使用的索引
    * （只允许有一个索引存在）
    *
    * @param appCode
    * @param eventCode
    * @param maping
    * @return
    */
  def getOrCreateOneIndex(appCode: String,
                          eventCode: String,
                          maping: String): EdaIndexRoute = {
    val key = appCode + "_" + eventCode
    val routesGroup = EdaIndexRouteHelper.getAllRouteIndex()
    val routes = routesGroup.get(key).toList.flatten
    val filterRoutes: immutable.Seq[EdaIndexRoute] = routes.filter(p => {
      StringUtils.isBlank(p.getEndTime)
    })
    if (routes.isEmpty || filterRoutes.isEmpty) {
      val currentDate = new Date();
      var lastYear = DateUtils.addYear(currentDate, -1)
      val endDate = DateUtils.format(new Date(), DateUtils.DATE_TIGHT__FORMAT)
      val indexName = appCode + "_" + eventCode + "_" + endDate;

      //新建新索引
      EsAdminUtil.createIndex(indexName, eventCode, maping)
      var jsonMap = new util.HashMap[String, Object]()
      jsonMap.put(EdaIndexRoute.APP_CODE_FIELD, appCode)
      jsonMap.put(EdaIndexRoute.EVENT_CODE_FIELD, eventCode)
      jsonMap.put(EdaIndexRoute.INDEX_NAME_FIELD, indexName)
      jsonMap.put(EdaIndexRoute.START_TIME_FIELD, DateUtils.format(lastYear,
        DateUtils.DATE_FULL_FORMAT))
      jsonMap.put(EdaIndexRoute.SIZE_FIELD, String.valueOf(0))
      val json: Gson = new Gson
      val jsonStr: String = json.toJson(jsonMap)
      val id = EsAdminUtil.indexDocument(Constants.ROUTE_INDEX_NAME, Constants.ROUTE_TYPE_NAME, jsonStr);
      val currentRouteResponse = EsAdminUtil.getDocument(Constants .ROUTE_INDEX_NAME, Constants.ROUTE_TYPE_NAME, id)
      parsetIdaRoute(currentRouteResponse)
    } else {
      var currentRoute = filterRoutes(0)
      //如果超过限定大小,则新建索引
      if (currentRoute == null || currentRoute.getSize >= Constants.INDEX_MAX_ONE_SIZ) {
        val currentRouteResponse = createIndex(appCode, eventCode, maping)
        currentRoute = parsetIdaRoute(currentRouteResponse)
      }
      currentRoute
    }
  }


  /**
    * 更新索引记录信息,并返回在当前最新索引名
    *
    * @param currentIndex
    * @return
    *
    */
  def updateRouteIndex(appCode: String, eventCode: String, maping: String, currentIndex: EdaIndexRoute, timeField: String):
  String = {
    var currentIndexName = currentIndex.getIndexName
    var currentIndexNameCopy = currentIndexName
    val size = EsAdminUtil.getIndexSize(currentIndexName)
    logger.info("索引:" + currentIndexName + "的索引的size为:" + size)
    if (size != currentIndex.getSize) {
      currentIndex.setSize(size)
      if (size >= Constants.INDEX_MAX_SIZE) {
        //更新索引大小以及开始\结束时间
        val endTime = EsAdminUtil.queryMaxTime(currentIndexName, timeField);
        val startTime = EsAdminUtil.queryMinTime(currentIndexName, timeField);
        currentIndex.setStartTime(startTime)
        currentIndex.setEndTime(endTime)
        val gson = new Gson()
        val jsonStr = gson.toJson(currentIndex.convertToMap())
        EsAdminUtil.upsert(Constants.ROUTE_INDEX_NAME, Constants.ROUTE_TYPE_NAME, currentIndex.getId, jsonStr)

        //新建新索引
        logger.info("创建新的索引:" + appCode + "_" + eventCode)
        val currentRouteResponse = createIndex(appCode, eventCode, maping)
        val currentRoute = parsetIdaRoute(currentRouteResponse)
        currentIndexNameCopy = currentRoute.getIndexName
      } else {
        val gson = new Gson()
        val startTime = EsAdminUtil.queryMinTime(currentIndexName, timeField);

        if (!startTime.startsWith("Infini")) {
          currentIndex.setStartTime(startTime)
          val jsonStr = gson.toJson(currentIndex.convertToMap())
          logger.info("更新索引:" + currentIndexName + " 信息为:" + jsonStr)
          EsAdminUtil.upsert(Constants.ROUTE_INDEX_NAME, Constants.ROUTE_TYPE_NAME, currentIndex.getId, jsonStr)
        }
      }
    }
    currentIndexNameCopy
  }

  def createIndex(appCode: String, eventCode: String, maping: String): GetResponse = {
    val currentDate = new Date();
    var lastYear = DateUtils.addYear(currentDate, -1)
    val endDate = DateUtils.format(new Date(), DateUtils.DATE_TIGHT__FORMAT)
    val indexName = appCode + "_" + eventCode + "_" + endDate
    //新建新索引
    EsAdminUtil.createIndex(indexName, eventCode, maping)
    var jsonMap = new util.HashMap[String, Object]()
    jsonMap.put(EdaIndexRoute.APP_CODE_FIELD, appCode)
    jsonMap.put(EdaIndexRoute.EVENT_CODE_FIELD, eventCode)
    jsonMap.put(EdaIndexRoute.INDEX_NAME_FIELD, indexName)
    jsonMap.put(EdaIndexRoute.START_TIME_FIELD, DateUtils.format(lastYear, DateUtils.DATE_FULL_FORMAT))
    jsonMap.put(EdaIndexRoute.SIZE_FIELD, String.valueOf(0))
    val json: Gson = new Gson
    val jsonStr: String = json.toJson(jsonMap)
    val id = EsAdminUtil.indexDocument(Constants.ROUTE_INDEX_NAME, Constants.ROUTE_TYPE_NAME, jsonStr);
    val currentRouteResponse = EsAdminUtil.getDocument(Constants.ROUTE_INDEX_NAME, Constants.ROUTE_TYPE_NAME, id)
    currentRouteResponse
  }

  /**
    * 解析es获取的路由信息
    *
    * @param response
    * @return
    */
  def parsetIdaRoute(response: GetResponse): EdaIndexRoute = {
    val sourceAsMap: util.Map[String, AnyRef] = response.getSourceAsMap
    val appCode: String = sourceAsMap.get(EdaIndexRoute.APP_CODE_FIELD)
      .asInstanceOf[String]
    val eventCode: String = sourceAsMap.get(EdaIndexRoute.EVENT_CODE_FIELD)
      .asInstanceOf[String]
    val indexName: String = sourceAsMap.get(EdaIndexRoute.INDEX_NAME_FIELD).asInstanceOf[String]
    val startTime: String = sourceAsMap.get(EdaIndexRoute.START_TIME_FIELD).asInstanceOf[String]
    val endTime: String = sourceAsMap.getOrDefault(EdaIndexRoute.END_TIME_FIELD, "").asInstanceOf[String]
    val size = sourceAsMap.get(EdaIndexRoute.SIZE_FIELD, 0l)
      .asInstanceOf[Long]
    val id = response.getId;
    val route: EdaIndexRoute = new EdaIndexRoute
    route.setId(id)
    route.setAppCode(appCode)
    route.setEventCode(eventCode)
    route.setIndexName(indexName)
    route.setStartTime(startTime)
    route.setEndTime(endTime)
    route.setSize(size)
    route
  }


}
