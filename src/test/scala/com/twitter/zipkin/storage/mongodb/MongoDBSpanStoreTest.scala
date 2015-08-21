package com.twitter.zipkin.storage.mongodb

import java.nio.ByteBuffer

import com.mongodb.casbah.Imports._
import com.twitter.zipkin.storage.{SpanStoreSpec, IndexedTraceId}
import org.scalatest.FunSuite

class MongoDBSpanStoreTest extends FunSuite {

  test("timestamps in MongoDBSpanStoreUtils") {
    val mockUtils = new MongoDBSpanStoreUtils {
      override def timestampsFromMongoObject(obj: MongoDBObject): Seq[Long] = Seq(0, 1)
    }
    assert(mockUtils.startTimeStampFromMongoObject(null) == 0)
    assert(mockUtils.endTimeStampFromMongoObject(null) == 1)
    val utils = new MongoDBSpanStoreUtils {}
    val actual = Seq[Long](1, 2, 3, 4)
    assert(utils.timestampsFromMongoObject(MongoDBObject(
      "annotations" -> MongoDBList(actual.map(timestamp => MongoDBObject(
        "timestamp" -> timestamp
      )): _*)
    )) == actual)
  }

  test("toByteArray in MongoDBSpanStoreUtils") {
    val arr = Array[Byte](1, 2, 3)
    val bb = ByteBuffer.wrap(arr)
    val bb_str = bb.toString //We'll use this to compare the offset value
    val newArr = new MongoDBSpanStoreUtils {}.toByteArray(bb)
    assert(newArr.length == arr.length)
    (0 to (arr.length - 1)) foreach (i => assert(newArr(i) == arr(i)))
    assert(bb.toString == bb_str) //There should be no side effects
  }

  test("dbObjectToIndexedTraceId in MongoDBSpanStoreUtils") {
    val mockUtils = new MongoDBSpanStoreUtils {
      override def startTimeStampFromMongoObject(obj: MongoDBObject): Long = 666
    }
    assert(mockUtils.dbObjectToIndexedTraceId(MongoDBObject("traceId" -> 123L)) == IndexedTraceId(123, 666))
  }
}
