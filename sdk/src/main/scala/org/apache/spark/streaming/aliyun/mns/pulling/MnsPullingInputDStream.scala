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
package org.apache.spark.streaming.aliyun.mns.pulling

import com.aliyun.mns.model.Message
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.aliyun.mns.pulling.MnsPullingReceiver
import org.apache.spark.streaming.dstream.ReceiverInputDStream
import org.apache.spark.streaming.receiver.Receiver

class MnsPullingInputDStream(
    @transient _ssc: StreamingContext,
    queueName: String,
    func: Message => Array[Byte],
    accessKeyId: String,
    accessKeySecret: String,
    endpoint: String,
    storageLevel: StorageLevel)
  extends ReceiverInputDStream[Array[Byte]](_ssc){
  val batchMsgSize = _ssc.sc.getConf.getInt("spark.mns.batchMsg.size", 16)
  val pollingWaitSeconds = _ssc.sc.getConf.getInt("spark.mns.pollingWait.seconds", 30)

  override def getReceiver(): Receiver[Array[Byte]] =
    new MnsPullingReceiver(
      queueName,
      batchMsgSize,
      pollingWaitSeconds,
      func,
      accessKeyId,
      accessKeySecret,
      endpoint,
      storageLevel)
}
