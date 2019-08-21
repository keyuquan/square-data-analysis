package com.square.common.utils

/**
  * Created by admin on 2018/5/8.
  */
object Utils {
  def hash(s:String)={
    val m = java.security.MessageDigest.getInstance("MD5")
    val b = s.getBytes("UTF-8")
    m.update(b,0,b.length)
    new java.math.BigInteger(1,m.digest()).toString(16)
  }
  def main(args: Array[String]): Unit = {
    println(hash("asdadas"))
  }
}
