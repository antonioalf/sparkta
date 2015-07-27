/**
 * Copyright (C) 2015 Stratio (http://stratio.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.stratio.sparkta.serving.api

import akka.event.slf4j.SLF4JLogging

import com.stratio.sparkta.serving.api.constants.AppConstant
import com.stratio.sparkta.serving.api.helpers.SparktaHelper

/**
 * Entry point of the application.
 */
object Sparkta extends App with SLF4JLogging {

  val sparktaHome   = SparktaHelper.initSparktaHome()
  val jars          = SparktaHelper.initJars(AppConstant.JarPaths, sparktaHome)
  val configSparkta = SparktaHelper.initConfig(AppConstant.ConfigAppName)
  val configApi     = SparktaHelper.initConfig(AppConstant.ConfigApi, Some(configSparkta))
  val configJobServer = SparktaHelper.initConfig(AppConstant.ConfigJobServer, Some(configSparkta))

  SparktaHelper.initAkkaSystem(configSparkta, configApi, configJobServer, jars, AppConstant.ConfigAppName)
}
