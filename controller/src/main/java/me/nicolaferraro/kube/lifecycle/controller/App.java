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
package me.nicolaferraro.kube.lifecycle.controller;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.Watch;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.openshift.client.DefaultOpenShiftClient;
import io.fabric8.openshift.client.OpenShiftClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author nicola
 * @since 20/05/2017
 */
@SpringBootApplication
public class App {

    @Autowired
    private PodManager podManager;

    @Autowired
    private OpenShiftClient openshiftClient;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    public OpenShiftClient openshiftClient() {
        return new DefaultOpenShiftClient();
    }

    @Bean
    public Watch watcher(OpenShiftClient client) {

        return client.pods().watch(new Watcher<Pod>() {
            @Override
            public void eventReceived(Action action, Pod pod) {
                if (podManager.isManaged(pod) && "DELETED".equalsIgnoreCase(action.name())) {
                    podManager.onPodDeleted(pod);
                }
            }

            @Override
            public void onClose(KubernetesClientException e) {
            }
        });
    }

}
