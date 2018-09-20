// Copyright 2018 North Dakota State University
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package edu.ndsu.eci.international_capstone_exchange.services.impl;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

import edu.ndsu.eci.international_capstone_exchange.services.VelocityService;

public class VelocityServiceImpl implements VelocityService {

  /** logger */
  private static final Logger LOGGER = Logger.getLogger(VelocityServiceImpl.class);
  
  /* (non-Javadoc)
   * @see edu.ndsu.eci.tapestry.services.VelocityService#getEngine()
   */
  @Override
  public VelocityEngine getEngine() {
    Properties configuration = new Properties();
    configuration.setProperty(RuntimeConstants.RESOURCE_LOADER, "class");
    configuration.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
    configuration.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem");
    VelocityEngine engine = new VelocityEngine();
    try {
      engine.init(configuration);
    } catch (Exception e) {
      LOGGER.error("Failed to create Velocity engine", e);
    }
    return engine;
  }
}
