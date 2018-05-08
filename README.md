#AWS RDS Monitoring Extension

## Use Case
Captures RDS statistics from Amazon CloudWatch and displays them in the AppDynamics Metric Browser.

**Note : By default, the Machine agent can only send a fixed number of metrics to the controller. This extension potentially reports thousands of metrics, so to change this limit, please follow the instructions mentioned [here](https://docs.appdynamics.com/display/PRO40/Metrics+Limits).** 

## Prerequisite

If you don't want to provide awsAccessKey and awsSecretKey, please run the extension on EC2 instance and configure Instance Profile by granting below permissions

~~~
"cloudwatch:GetMetricData",
"cloudwatch:GetMetricStatistics",
"cloudwatch:ListMetrics"
~~~

In order to use this extension, you do need a [Standalone JAVA Machine Agent](https://docs.appdynamics.com/display/PRO44/Java+Agent) or [SIM Agent](https://docs.appdynamics.com/display/PRO44/Server+Visibility).  For more details on downloading these products, please  visit [here](https://download.appdynamics.com/).

The extension needs to be able to connect to AWS in order to collect and send metrics. To do this, you will have to either establish a remote connection in between the extension and the product, or have an agent on the same machine running the product in order for the extension to collect and send the metrics.

## Installation

1. Run 'mvn clean install' from aws-rds-monitoring-extension
2. Copy and unzip AWSRDSMonitor-\<version\>.zip from 'target' directory into \<machine_agent_dir\>/monitors/
3. Edit config.yaml file in AWSRDSMonitor/conf and provide the required configuration (see Configuration section)
4. Restart the Machine Agent.

Please place the extension in the **"monitors"** directory of your **Machine Agent** installation directory. Do not place the extension in the **"extensions"** directory of your **Machine Agent** installation directory.

## Configuration

1. Configure the "COMPONENT_ID" under which the metrics need to be reported. This can be done by changing the value of `<COMPONENT_ID>` in
     metricPrefix: "Server|Component:<COMPONENT_ID>|Custom Metrics|Amazon EC2|".

     For example,
     ```
     metricPrefix: "Server|Component:100|Custom Metrics|Amazon EC2|"
     ```
2. Configure "awsAccessKey", "awsSecretKey" and "regions"". If you are running this extension inside an EC2 instance which has IAM profile configured then you don't have to configure these values, extension will use IAM profile to authenticate.

    For example
    ```
    #Add you list of AWS accounts here
    accounts:
      - awsAccessKey: "XXXXXXX1"
        awsSecretKey: "XXXXXXX1"
        displayAccountName: "Test1"
        regions: ["us-east-1","us-west-1","us-west-2"]

      - awsAccessKey: "XXXXXXX2"
        awsSecretKey: "XXXXXXX2"
        displayAccountName: "Test2"
        regions: ["eu-central-1","eu-west-1"]
    ```
3. If you want to encrypt the "awsAccessKey" and "awsSecretKey" then follow the "Credentials Encryption" section and provide the encrypted values in "awsAccessKey" and "awsSecretKey". Configure "enableDecryption" of "credentialsDecryptionConfig" to true and provide the encryption key in "encryptionKey"

    For example,
    ```
    #Encryption key for Encrypted password.
    credentialsDecryptionConfig:
        enableDecryption: "true"
        encryptionKey: "XXXXXXXX"
    ```
4. If you want to filer metrics based on DB identifier. Please configure as below:

    ```
    includeDBIdentifiers: ["blog-*", "demodb"]
    ```
5. Configure the numberOfThreads
     ```
     concurrencyConfig:
        noOfAccountThreads: 3
        noOfRegionThreadsPerAccount: 3
        noOfMetricThreadsPerRegion: 3
     ```
6. Configure the monitoring level as shown below. Allowed values are Basic and Detailed. Refer [this](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/using-cloudwatch-new.html) for more information
   Basic will fire CloudWatch API calls every 5 minutes. Detailed will fire CloudWatch API calls every 1 minutes
    ```
    cloudWatchMonitoring: "Basic"
    ```


### config.yaml

**Note: Please avoid using tab (\t) when editing yaml files. You may want to validate the yaml file using a [yaml validator](http://yamllint.com/).**

**Below is an example config for monitoring multiple accounts and regions:**

~~~
accounts:
  - awsAccessKey: "XXXXXXX1"
    awsSecretKey: "XXXXXXX1"
    displayAccountName: "Test1"
    regions: ["us-east-1","us-west-1","us-west-2"]

  - awsAccessKey: "XXXXXXX2"
    awsSecretKey: "XXXXXXX2"
    displayAccountName: "Test2"
    regions: ["eu-central-1","eu-west-1"]


credentialsDecryptionConfig:
    enableDecryption: "false"
    encryptionKey:

proxyConfig:
    host:
    port:
    username:
    password:


#Filters metrics based on DB identifier names provided. Accepts regex patterns
includeDBIdentifiers: ["blog-*", "demodb"]



concurrencyConfig:
  noOfAccountThreads: 3
  noOfRegionThreadsPerAccount: 3
  noOfMetricThreadsPerRegion: 3
  #Thread timeout in seconds
  threadTimeOut: 30

#Allowed values are Basic and Detailed. Refer https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/using-cloudwatch-new.html for more information
# Basic will fire CloudWatch API calls every 5 minutes
# Detailed will fire CloudWatch API calls every 1 minutes
cloudWatchMonitoring: "Basic"

regionEndPoints:
  ap-southeast-1: monitoring.ap-southeast-1.amazonaws.com
  ap-southeast-2: monitoring.ap-southeast-2.amazonaws.com
  ap-northeast-1: monitoring.ap-northeast-1.amazonaws.com
  eu-central-1: monitoring.eu-central-1.amazonaws.com
  eu-west-1: monitoring.eu-west-1.amazonaws.com
  us-east-1: monitoring.us-east-1.amazonaws.com
  us-west-1: monitoring.us-west-1.amazonaws.com
  us-west-2: monitoring.us-west-2.amazonaws.com
  sa-east-1: monitoring.sa-east-1.amazonaws.com

#This will create this metric in all the tiers, under this path. Please make sure to have a trailing |
#metricPrefix: "Custom Metrics|Amazon RDS|"

#This will create it in specific Tier aka Component. Replace <COMPONENT_ID>. Please make sure to have a trailing |.
#To find out the COMPONENT_ID, please see the screen shot here https://docs.appdynamics.com/display/PRO42/Build+a+Monitoring+Extension+Using+Java
metricPrefix: "Server|Component:<TIER_ID>|Custom Metrics|Amazon RDS|"

# Global metrics config for all accounts
metricsConfig:

    # By default, all metrics retrieved from cloudwatch are 'Average' values.
    # This option allows you to override the metric type.
    #
    # Allowed statTypes are: ave, max, min, sum, samplecount
    #
    # Note: Irrespective of the metric type, value will still be reported as
    # Observed value to the Controller
    includeMetrics:
       - name: "BinLogDiskUsage"
         alias: "BinLogDiskUsage"
         statType: "ave"
         aggregationType: "OBSERVATION"
         timeRollUpType: "CURRENT"
         clusterRollUpType: "COLLECTIVE"
         delta: false
         multiplier: 1
       - name: "BurstBalance"
         alias: "BurstBalance"
         statType: "ave"
         aggregationType: "OBSERVATION"
         timeRollUpType: "CURRENT"
         clusterRollUpType: "COLLECTIVE"
         delta: false
         multiplier: 1
       - name: "CPUUtilization"
         alias: "CPUUtilization"
         statType: "ave"
         aggregationType: "OBSERVATION"
         timeRollUpType: "CURRENT"
         clusterRollUpType: "COLLECTIVE"
         delta: false
         multiplier: 1
       - name: "CPUCreditUsage"
         alias: "CPUCreditUsage"
         statType: "ave"
         aggregationType: "OBSERVATION"
         timeRollUpType: "CURRENT"
         clusterRollUpType: "COLLECTIVE"
         delta: false
         multiplier: 1
       - name: "CPUCreditBalance"
         alias: "CPUCreditBalance"
         statType: "ave"
         aggregationType: "OBSERVATION"
         timeRollUpType: "CURRENT"
         clusterRollUpType: "COLLECTIVE"
         delta: false
         multiplier: 1
       - name: "BinLogDiskUsage"
         alias: "BinLogDiskUsage"
         statType: "ave"
         aggregationType: "OBSERVATION"
         timeRollUpType: "CURRENT"
         clusterRollUpType: "COLLECTIVE"
         delta: false
         multiplier: 1
       - name: "BurstBalance"
         alias: "BurstBalance"
         statType: "ave"
         aggregationType: "OBSERVATION"
         timeRollUpType: "CURRENT"
         clusterRollUpType: "COLLECTIVE"
         delta: false
         multiplier: 1
       - name: "CPUSurplusCreditBalance"
         alias: "CPUSurplusCreditBalance"
         statType: "ave"
         aggregationType: "OBSERVATION"
         timeRollUpType: "CURRENT"
         clusterRollUpType: "COLLECTIVE"
         delta: false
         multiplier: 1
       - name: "CPUSurplusCreditsCharged"
         alias: "CPUSurplusCreditsCharged"
         statType: "ave"
         aggregationType: "OBSERVATION"
         timeRollUpType: "CURRENT"
         clusterRollUpType: "COLLECTIVE"
         delta: false
         multiplier: 1
       - name: "DatabaseConnections"
         alias: "DatabaseConnections"
         statType: "ave"
         aggregationType: "OBSERVATION"
         timeRollUpType: "CURRENT"
         clusterRollUpType: "COLLECTIVE"
         delta: false
         multiplier: 1
       - name: "DiskQueueDepth"
         alias: "DiskQueueDepth"
         statType: "ave"
         aggregationType: "OBSERVATION"
         timeRollUpType: "CURRENT"
         clusterRollUpType: "COLLECTIVE"
         delta: false
         multiplier: 1
       - name: "FreeableMemory"
         alias: "FreeableMemory"
         statType: "ave"
         aggregationType: "OBSERVATION"
         timeRollUpType: "CURRENT"
         clusterRollUpType: "COLLECTIVE"
         delta: false
         multiplier: 1
       - name: "FreeStorageSpace"
         alias: "FreeStorageSpace"
         statType: "ave"
         aggregationType: "OBSERVATION"
         timeRollUpType: "CURRENT"
         clusterRollUpType: "COLLECTIVE"
         delta: false
         multiplier: 1
       - name: "MaximumUsedTransactionIDs"
         alias: "MaximumUsedTransactionIDs"
         statType: "ave"
         aggregationType: "OBSERVATION"
         timeRollUpType: "CURRENT"
         clusterRollUpType: "COLLECTIVE"
         delta: false
         multiplier: 1
       - name: "NetworkReceiveThroughput"
         alias: "NetworkReceiveThroughput"
         statType: "ave"
         aggregationType: "OBSERVATION"
         timeRollUpType: "CURRENT"
         clusterRollUpType: "COLLECTIVE"
         delta: false
         multiplier: 1
       - name: "NetworkTransmitThroughput"
         alias: "NetworkTransmitThroughput"
         statType: "ave"
         aggregationType: "OBSERVATION"
         timeRollUpType: "CURRENT"
         clusterRollUpType: "COLLECTIVE"
         delta: false
         multiplier: 1
       - name: "OldestReplicationSlotLag"
         alias: "OldestReplicationSlotLag"
         statType: "ave"
         aggregationType: "OBSERVATION"
         timeRollUpType: "CURRENT"
         clusterRollUpType: "COLLECTIVE"
         delta: false
         multiplier: 1
       - name: "ReadIOPS"
         alias: "ReadIOPS"
         statType: "ave"
         aggregationType: "OBSERVATION"
         timeRollUpType: "CURRENT"
         clusterRollUpType: "COLLECTIVE"
         delta: false
         multiplier: 1
       - name: "ReadLatency"
         alias: "ReadLatency"
         statType: "ave"
         aggregationType: "OBSERVATION"
         timeRollUpType: "CURRENT"
         clusterRollUpType: "COLLECTIVE"
         delta: false
         multiplier: 1
       - name: "ReadThroughput"
         alias: "ReadThroughput"
         statType: "ave"
         aggregationType: "OBSERVATION"
         timeRollUpType: "CURRENT"
         clusterRollUpType: "COLLECTIVE"
         delta: false
         multiplier: 1
       - name: "ReplicaLag"
         alias: "ReplicaLag"
         statType: "ave"
         aggregationType: "OBSERVATION"
         timeRollUpType: "CURRENT"
         clusterRollUpType: "COLLECTIVE"
         delta: false
         multiplier: 1
       - name: "ReplicationSlotDiskUsage"
         alias: "ReplicationSlotDiskUsage"
         statType: "ave"
         aggregationType: "OBSERVATION"
         timeRollUpType: "CURRENT"
         clusterRollUpType: "COLLECTIVE"
         delta: false
         multiplier: 1
       - name: "SwapUsage"
         alias: "SwapUsage"
         statType: "ave"
         aggregationType: "OBSERVATION"
         timeRollUpType: "CURRENT"
         clusterRollUpType: "COLLECTIVE"
         delta: false
         multiplier: 1
       - name: "TransactionLogsDiskUsage"
         alias: "TransactionLogsDiskUsage"
         statType: "ave"
         aggregationType: "OBSERVATION"
         timeRollUpType: "CURRENT"
         clusterRollUpType: "COLLECTIVE"
         delta: false
         multiplier: 1
       - name: "TransactionLogsGeneration"
         alias: "TransactionLogsGeneration"
         statType: "ave"
         aggregationType: "OBSERVATION"
         timeRollUpType: "CURRENT"
         clusterRollUpType: "COLLECTIVE"
         delta: false
         multiplier: 1
       - name: "WriteIOPS"
         alias: "WriteIOPS"
         statType: "ave"
         aggregationType: "OBSERVATION"
         timeRollUpType: "CURRENT"
         clusterRollUpType: "COLLECTIVE"
         delta: false
         multiplier: 1
       - name: "WriteLatency"
         alias: "WriteLatency"
         statType: "ave"
         aggregationType: "OBSERVATION"
         timeRollUpType: "CURRENT"
         clusterRollUpType: "COLLECTIVE"
         delta: false
         multiplier: 1
       - name: "WriteThroughput"
         alias: "WriteThroughput"
         statType: "ave"
         aggregationType: "OBSERVATION"
         timeRollUpType: "CURRENT"
         clusterRollUpType: "COLLECTIVE"
         delta: false
         multiplier: 1

    metricsTimeRange:
      startTimeInMinsBeforeNow: 5
      endTimeInMinsBeforeNow: 0

    # Rate limit ( per second ) for GetMetricStatistics, default value is 400. https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/cloudwatch_limits.html
    getMetricStatisticsRateLimit: 400

    #
    # The max number of retry attempts for failed retryable requests
    # (ex: 5xx error responses from a service) or throttling errors
    #
    maxErrorRetrySize: 0
~~~

## Metrics
Metrics provided by this extension are defined in the link given below:

[RDS Metrics](http://docs.aws.amazon.com/AmazonCloudWatch/latest/DeveloperGuide/rds-metricscollected.html)

## Credentials Encryption

Please visit [this page](https://community.appdynamics.com/t5/Knowledge-Base/How-to-use-Password-Encryption-with-Extensions/ta-p/29397) to get detailed instructions on password encryption. The steps in this document will guide you through the whole process.

## Extensions Workbench
Workbench is an inbuilt feature provided with each extension in order to assist you to fine tune the extension setup before you actually deploy it on the controller. Please review the following document on [How to use the Extensions WorkBench](https://community.appdynamics.com/t5/Knowledge-Base/How-to-use-the-Extensions-WorkBench/ta-p/30130)

## Troubleshooting
1. Please make sure correct accessKey and secretKey are provided in config.yml.
2. Please verify the correct regions have been configured
3. Enssure that the required permissions have been given to the account being used with the extension.
4. Please follow the steps listed in this [troubleshooting-document](https://community.appdynamics.com/t5/Knowledge-Base/How-to-troubleshoot-missing-custom-metrics-or-extensions-metrics/ta-p/28695) in order to troubleshoot your issue. These are a set of common issues that customers might have faced during the installation of the extension. If these don't solve your issue, please follow the last step on the [troubleshooting-document](https://community.appdynamics.com/t5/Knowledge-Base/How-to-troubleshoot-missing-custom-metrics-or-extensions-metrics/ta-p/28695) to contact the support team.

## Support Tickets
If after going through the [Troubleshooting Document](https://community.appdynamics.com/t5/Knowledge-Base/How-to-troubleshoot-missing-custom-metrics-or-extensions-metrics/ta-p/28695) you have not been able to get your extension working, please file a ticket and add the following information.

Please provide the following in order for us to assist you better.

    1. Stop the running machine agent.
    2. Delete all existing logs under <MachineAgent>/logs.
    3. Please enable debug logging by editing the file <MachineAgent>/conf/logging/log4j.xml. Change the level value of the following <logger> elements to debug.
        <logger name="com.singularity">
        <logger name="com.appdynamics">
    4. Start the machine agent and please let it run for 10 mins. Then zip and upload all the logs in the directory <MachineAgent>/logs/*.
    5. Attach the zipped <MachineAgent>/conf/* directory here.
    6. Attach the zipped <MachineAgent>/monitors/ExtensionFolderYouAreHavingIssuesWith directory here.

For any support related questions, you can also contact help@appdynamics.com.


## Contributing

Always feel free to fork and contribute any changes directly here on [GitHub](https://github.com/Appdynamics/aws-rds-monitoring-extension).

## Version
|          Name            |  Version   |
|--------------------------|------------|
|Extension Version         |2.0.0       |
|Controller Compatibility  |3.7 or Later|
|Last Update               |05/08/2018  |
