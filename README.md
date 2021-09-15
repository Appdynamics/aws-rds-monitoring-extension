# AWS RDS Monitoring Extension

## Use Case
Captures RDS statistics from Amazon CloudWatch and displays them in the AppDynamics Metric Browser.

## Prerequisite

If you don't want to provide awsAccessKey and awsSecretKey, please run the extension on EC2 instance and configure Instance Profile by granting below permissions

~~~
"cloudwatch:GetMetricData",
"cloudwatch:GetMetricStatistics",
"cloudwatch:ListMetrics"
~~~

Before the extension is installed, the prerequisites mentioned [here](https://community.appdynamics.com/t5/Knowledge-Base/Extensions-Prerequisites-Guide/ta-p/35213) need to be met. Please do not proceed with the extension installation if the specified prerequisites are not met.

The extension needs to be able to connect to AWS in order to collect and send metrics. To do this, you will have to either establish a remote connection in between the extension and the product, or have an agent on the same machine running the product in order for the extension to collect and send the metrics.

** Note : This extension is compatible with Machine Agent version 4.5.13 or later.

* If you are seeing warning messages while starting the Machine Agent, update the http-client and http-core JARs in {MACHINE_AGENT_HOME}/monitorsLibs to httpclient-4.5.9 and httpcore-4.4.12 to make this warning go away.
* To make AWS extensions work on Machine Agent < 4.5.13: The http-client and http-core JARs in {MACHINE_AGENT_HOME}/monitorsLibs has to be manually be updated to httpclient-4.5.9 and httpcore-4.4.12

## Installation

1. Run 'mvn clean install' from aws-rds-monitoring-extension
2. Copy and unzip AWSRDSMonitor-\<version\>.zip from 'target' directory into \<machine_agent_dir\>/monitors/
3. Edit config.yaml file in AWSRDSMonitor/conf and provide the required configuration (see Configuration section)
4. Restart the Machine Agent.

Please place the extension in the **"monitors"** directory of your **Machine Agent** installation directory. Do not place the extension in the **"extensions"** directory of your **Machine Agent** installation directory.

## Configuration

1. Configure the "COMPONENT_ID" under which the metrics need to be reported. This can be done by changing the value of `<COMPONENT_ID>` in
     metricPrefix: "Server|Component:<COMPONENT_ID>|Custom Metrics|Amazon RDS|".

     For example,
     ```
     metricPrefix: "Server|Component:100|Custom Metrics|Amazon RDS|"
     ```
More details around metric prefix can be found [here](https://community.appdynamics.com/t5/Knowledge-Base/How-do-I-troubleshoot-missing-custom-metrics-or-extensions/ta-p/28695).

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
4. If you want to filer metrics based on DB identifier, please configure as below. Configuring .* will fetch metrics for all DBs for configured account and region.

    ```
    dimensions:
       - name: "DBInstanceIdentifier"
         displayName: "DBInstanceIdentifier"
         values: ["blog*", "demodb"]
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
7. Configure the metrics section.

     For configuring the metrics, the following properties can be used:

     |     Property      |   Default value |         Possible values         |                                              Description                                                                                                |
     | :---------------- | :-------------- | :------------------------------ | :------------------------------------------------------------------------------------------------------------- |
     | alias             | metric name     | Any string                      | The substitute name to be used in the metric browser instead of metric name.                                   |
     | statType          | "ave"           | "AVERAGE", "SUM", "MIN", "MAX"  | AWS configured values as returned by API                                                                       |
     | aggregationType   | "AVERAGE"       | "AVERAGE", "SUM", "OBSERVATION" | [Aggregation qualifier](https://docs.appdynamics.com/display/latest/Build+a+Monitoring+Extension+Using+Java)    |
     | timeRollUpType    | "AVERAGE"       | "AVERAGE", "SUM", "CURRENT"     | [Time roll-up qualifier](https://docs.appdynamics.com/display/latest/Build+a+Monitoring+Extension+Using+Java)   |
     | clusterRollUpType | "INDIVIDUAL"    | "INDIVIDUAL", "COLLECTIVE"      | [Cluster roll-up qualifier](https://docs.appdynamics.com/display/latest/Build+a+Monitoring+Extension+Using+Java)|
     | multiplier        | 1               | Any number                      | Value with which the metric needs to be multiplied.                                                            |
     | convert           | null            | Any key value map               | Set of key value pairs that indicates the value to which the metrics need to be transformed. eg: UP:0, DOWN:1  |
     | delta             | false           | true, false                     | If enabled, gives the delta values of metrics instead of actual values.                                        |

     For example,
     ```
            - name: "CPUUtilization"
              alias: "CPUUtilization"
              statType: "ave"
              aggregationType: "OBSERVATION"
              timeRollUpType: "CURRENT"
              clusterRollUpType: "COLLECTIVE"
              delta: false
              multiplier: 1
     ```
     **All these metric properties are optional, and the default value shown in the table is applied to the metric(if a property has not been specified) by default.**


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
3. Ensure that the required permissions have been given to the account being used with the extension.
4. Please follow the steps listed in this [troubleshooting-document](https://community.appdynamics.com/t5/Knowledge-Base/How-to-troubleshoot-missing-custom-metrics-or-extensions-metrics/ta-p/28695) in order to troubleshoot your issue. These are a set of common issues that customers might have faced during the installation of the extension.


## Contributing

Always feel free to fork and contribute any changes directly here on [GitHub](https://github.com/Appdynamics/aws-rds-monitoring-extension).

## Version
|          Name            |  Version   |
|--------------------------|------------|
|Extension Version         |2.0.7       |
|Last Update               |19/05/2021  |
|Changes list              |[ChangeLog](https://github.com/Appdynamics/aws-rds-monitoring-extension/blob/master/CHANGELOG.md)|

**Note**: While extensions are maintained and supported by customers under the open-source licensing model, they interact with agents and Controllers that are subject to [AppDynamics’ maintenance and support policy](https://docs.appdynamics.com/latest/en/product-and-release-announcements/maintenance-support-for-software-versions). Some extensions have been tested with AppDynamics 4.5.13+ artifacts, but you are strongly recommended against using versions that are no longer supported.
