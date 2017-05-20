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

import java.util.Optional;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.Job;
import io.fabric8.kubernetes.api.model.JobBuilder;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodSpecBuilder;
import io.fabric8.openshift.client.OpenShiftClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author nicola
 * @since 20/05/2017
 */
@Component
public class SimplePodManager implements PodManager {

    private static final String ON_DELETE_JOB_ANNOTATION = "lifecycle-controller-onDeleteJob";
    private static final String ON_DELETE_JOB_ENV_VAR = "ON_DELETE_JOB";

    @Autowired
    private OpenShiftClient openshiftClient;

    @Override
    public boolean isManaged(Pod pod) {
        return onDeleteJob(pod).isPresent() && onDeleteJob(pod).map(d -> !"false".equalsIgnoreCase(d)).orElse(true);
    }

    @Override
    public void onPodDeleted(Pod pod) {
        Job shutdownJob = new JobBuilder()
                .withNewMetadata()
                    .withName(name(pod) + "-shutdown-job")
                .endMetadata()
                .withNewSpec()
                    .withNewTemplate()
                    .withSpec(new PodSpecBuilder(pod.getSpec())
                            .withRestartPolicy("OnFailure")
                            .editFirstContainer()
                                .addNewEnv()
                                    .withName(ON_DELETE_JOB_ENV_VAR)
                                    .withValue("true")
                                .endEnv()
                            .endContainer()
                            .build()
                    )
                    .endTemplate()
                .endSpec()
                .build();


        Container container = shutdownJob.getSpec().getTemplate().getSpec().getContainers().get(0);
        container.setLivenessProbe(null);
        container.setReadinessProbe(null);

        openshiftClient.extensions().jobs().create(shutdownJob);
    }

    private Optional<String> onDeleteJob(Pod pod) {
        return Optional.ofNullable(pod.getMetadata())
                .flatMap(m -> Optional.ofNullable(m.getAnnotations()))
                .flatMap(a -> Optional.ofNullable(a.get(ON_DELETE_JOB_ANNOTATION)));
    }

    private String name(Pod pod) {
        return pod.getMetadata().getName();
    }

}
