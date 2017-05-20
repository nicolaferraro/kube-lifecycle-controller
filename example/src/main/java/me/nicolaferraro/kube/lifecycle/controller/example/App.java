/*
 * Copyright 2016 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version
 * 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package me.nicolaferraro.kube.lifecycle.controller.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author nicola
 * @since 20/05/2017
 */
@SpringBootApplication
public class App {

    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws InterruptedException {
        boolean onDeleteMode = "true".equals(System.getenv("ON_DELETE_JOB"));

        if (onDeleteMode) {
            LOG.info("Starting delete job");

            for (int i = 1; i <= 20; i++) {
                Thread.sleep(1000);
                LOG.info("Done delete step {}", i);
            }

            LOG.info("Deletion completed");
        } else {
            LOG.info("Starting application in normal mode");
            SpringApplication.run(App.class, args);
        }
    }

}
