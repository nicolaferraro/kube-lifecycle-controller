# Kubernetes Lifecycle Controller

This is a simple controller for Kubernetes that allows doing resource cleanup when a pod is deleted,
allowing to handle correctly operations like **pod deletion** or **scaling down**.  

When a pod is deleted (that happens also when scaling down), the controller creates a new Kubernetes `Job` to execute resource cleanup. 
The job will use the same pod specification of the terminated pod, but a environment variable `ON_DELETE_JOB=true`.

The application code can intercept this configuration and run the recovery job instead of the main application. 
An example is provided in the `/example` directory.

Note: it supports Openshift *DeploymentConfig* or Kubernetes *Deployment* options.

## Installation

Connect to a Kubernetes/Openshift installation (you may use e.g. ["oc cluster up"](https://github.com/openshift/origin/blob/master/docs/cluster_up_down.md) to create a new cluster).

Run the following script:
```
./deploy-controller.sh
```

## Trying it out

After the installation, deploy the example using the script:
```
./deploy-example.sh
```

This will deploy 2 pods.

If you scale down to 1 pod, you will notice that a `Job` resource will be created. Looking at the logs,
the job will execute a recovery process for 20 seconds, then complete correctly.