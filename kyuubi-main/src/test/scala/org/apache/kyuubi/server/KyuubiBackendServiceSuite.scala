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

package org.apache.kyuubi.server

import org.apache.kyuubi.{KyuubiFunSuite, KyuubiSQLException}
import org.apache.kyuubi.config.KyuubiConf

class KyuubiBackendServiceSuite extends KyuubiFunSuite {
  test("Illegal key should be restricted and throw KyuubiSQLException") {
    val illegalKey = "session.illegal.key"
    val conf = KyuubiConf().set(KyuubiConf.SESSION_CONF_RESTRICT_LIST,
      illegalKey)
    val backendService = new KyuubiBackendService()
    backendService.initialize(conf)
    assertThrows[KyuubiSQLException] {
      backendService.intercept(Map(illegalKey -> "test"))
    }
  }

  test("Optimize the user's session config") {
    val ignoreKey = "session.ignore.key"
    val conf = KyuubiConf().set(KyuubiConf.SESSION_CONF_IGNORE_LIST,
      ignoreKey)
    val backendService = new KyuubiBackendService()
    backendService.initialize(conf)
    val newConf = backendService.optimizeConf(Map("first" -> "Kyuubi",
      ignoreKey -> "shouldIgnore"))
    assert(1 == newConf.size, "Should remove the ignore key.")
    assert("Kyuubi".equals(newConf.get("first").mkString),
      "Removed the wrong item.")
  }
}
