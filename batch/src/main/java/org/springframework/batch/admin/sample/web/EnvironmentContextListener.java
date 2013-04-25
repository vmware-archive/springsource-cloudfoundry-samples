/*
 * Copyright 2006-2010 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.springframework.batch.admin.sample.web;

import org.apache.commons.logging.*;
import org.cloudfoundry.runtime.env.*;
import org.springframework.util.Assert;

import javax.servlet.*;
import java.util.*;

/**
 * @author Dave Syer
 * @author Josh Long
 */
public class EnvironmentContextListener implements ServletContextListener {

    private static Log logger = LogFactory.getLog(EnvironmentContextListener.class);

    public void contextInitialized(ServletContextEvent sce) {
        logger.info("Attempting to detect cloud environment");
        logger.info(initializeCloudEnvironment() ? "Found VCAP environment" : "No cloud environment");
    }

    private boolean initializeCloudEnvironment() {
        CloudEnvironment cloudEnvironment = new CloudEnvironment();
        boolean detected = cloudEnvironment.isCloudFoundry();

        if (detected) {

            Collection<RdbmsServiceInfo> serviceInfos = cloudEnvironment.getServiceInfos(RdbmsServiceInfo.class);
            RdbmsServiceInfo rdbmsServiceInfo = serviceInfos.iterator().next();
            Assert.isTrue(serviceInfos.size() > 0, "there should be at least one MySQL service provisioned!");

            Map<String, String> propsToSet = new HashMap<String, String>();
            propsToSet.put("ENVIRONMENT", "vmc");
            propsToSet.put("DISCOVERY", "dynamic");
            propsToSet.put("TOPOLOGY", "cluster");
            propsToSet.put("MYSQL_PORT", Integer.toString(rdbmsServiceInfo.getPort()));
            propsToSet.put("MYSQL_HOST", rdbmsServiceInfo.getHost());
            propsToSet.put("MYSQL_USER", rdbmsServiceInfo.getUserName());
            propsToSet.put("MYSQL_PASSWORD", rdbmsServiceInfo.getPassword());
            propsToSet.put("MYSQL_DATABASE", rdbmsServiceInfo.getDatabase());

            for (Map.Entry<String, String> entry : propsToSet.entrySet())
                System.setProperty(entry.getKey(), entry.getValue());
        }
        return detected;
    }

    public void contextDestroyed(ServletContextEvent sce) {
    }

}
