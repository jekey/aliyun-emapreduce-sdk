/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.spark.streaming.aliyun.mns

import com.aliyun.mns.model.Message
import org.apache.spark.api.java.function.{Function => JFunction}
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.aliyun.mns.pulling.MnsPullingInputDStream
import org.apache.spark.streaming.api.java.{JavaReceiverInputDStream, JavaStreamingContext}
import org.apache.spark.streaming.dstream.ReceiverInputDStream

/**
 * Various utility classes for working with Aliyun MNS.
 */
object MnsUtils {
  def createPullingStreamAsBytes(
      ssc: StreamingContext,
      queueName: String,
      accessKeyId: String,
      accessKeySecret: String,
      endpoint: String,
      storageLevel: StorageLevel): ReceiverInputDStream[Array[Byte]] = {
    ssc.withNamedScope("mns stream as bytes") {
      val func: Message => Array[Byte] = message => message.getMessageBodyAsBytes
      new MnsPullingInputDStream(ssc, queueName, func, accessKeyId, accessKeySecret, endpoint, storageLevel)
    }
  }

  def createPullingStreamAsRawBytes(
      ssc: StreamingContext,
      queueName: String,
      accessKeyId: String,
      accessKeySecret: String,
      endpoint: String,
      storageLevel: StorageLevel): ReceiverInputDStream[Array[Byte]] = {
    ssc.withNamedScope("mns stream as raw bytes") {
      val func: Message => Array[Byte] = message => message.getMessageBodyAsRawBytes
      new MnsPullingInputDStream(ssc, queueName, func, accessKeyId, accessKeySecret, endpoint, storageLevel)
    }
  }

  def createPullingStream(
      ssc: StreamingContext,
      queueName: String,
      func: Message => Array[Byte],
      accessKeyId: String,
      accessKeySecret: String,
      endpoint: String,
      storageLevel: StorageLevel): ReceiverInputDStream[Array[Byte]] = {
    ssc.withNamedScope("mns stream") {
      new MnsPullingInputDStream(ssc, queueName, func, accessKeyId, accessKeySecret, endpoint, storageLevel)
    }
  }

  def createPullingStreamAsBytes(
      jssc: JavaStreamingContext,
      queueName: String,
      accessKeyId: String,
      accessKeySecret: String,
      endpoint: String,
      storageLevel: StorageLevel): JavaReceiverInputDStream[Array[Byte]] = {
    createPullingStreamAsBytes(jssc.ssc, queueName, accessKeyId, accessKeySecret, endpoint, storageLevel)
  }

  def createPullingStreamAsRawBytes(
      jssc: JavaStreamingContext,
      queueName: String,
      accessKeyId: String,
      accessKeySecret: String,
      endpoint: String,
      storageLevel: StorageLevel): JavaReceiverInputDStream[Array[Byte]] = {
    createPullingStreamAsRawBytes(jssc.ssc, queueName, accessKeyId, accessKeySecret, endpoint, storageLevel)
  }

  def createPullingStream(
      jssc: JavaStreamingContext,
      queueName: String,
      func: JFunction[Message, Array[Byte]],
      accessKeyId: String,
      accessKeySecret: String,
      endpoint: String,
      storageLevel: StorageLevel): JavaReceiverInputDStream[Array[Byte]] = {
    createPullingStream(jssc.ssc, queueName, (msg: Message) => func.call(msg), accessKeyId, accessKeySecret, endpoint,
      storageLevel)
  }
}
