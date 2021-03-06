# Installation and configuration of the automatic workflow

An automatic workflow takes care of the analysis of the harvested files on the basis of harvestlog written by the webapp to a common
directory (e.g. /home/harvestLogs)

There is two scripts, one that automatically takes the available harvestlogs from the common directory, and processes the harvestlogs one by one, and one takes a harvestlog as argument and then processes the harvestlog:
 * webdanica-analysis-cron.sh
 * webdanica-analysis-manual.sh

These scripts both include a common file setenv.sh which must be configured correctly before enabling the harvesting workflow, and enabling the cronjobs 
``` 
WORKFLOW_USER_HOME=/home/test
WEBDANICA_VERSION=1.0.0
WORKFLOW_HOME=$WORKFLOW_USER_HOME/automatic-workflow
WEBDATADIR=$WORKFLOW_USER_HOME/ARKIV
HADOOP_HOME=$WORKFLOW_USER_HOME/hadoop-1.2.1/
PIG_HOME=$WORKFLOW_USER_HOME/pig-0.16.0/
JAVA_HOME=/usr/java/jdk1.8.0_92_x64
## the below settings should not be altered
BUSYFILE=$WORKFLOW_HOME/.busy
WORKDIR=$WORKFLOW_HOME/working
OLDJOBSDIR=$WORKFLOW_HOME/oldjobs
PATH=$JAVA_HOME/bin:$PATH
FINDLOGS_SCRIPT=${WORKFLOW_HOME}/findharvestlogs.sh
AUTOMATIC_SCRIPT=${WORKFLOW_HOME}/automatic.sh
export WORKFLOW_HOME WEBDATADIR WEBDANICA_VERSION HADOOP_HOME PIG_HOME BUSYFILE WORKDIR OLDJOBSDIR JAVA_HOME PATH FINDLOGS_SCRIPT AUTOMATIC_SCRIPT
```

The important settings to look at is the WEBDATADIR, WEBDANICA_VERSION, and JAVA_HOME
Make sure that the WEBDATADIR points to the same location as defined by Netarchivesuite (default = home/test/ARKIV)

Furthermore hadoop-1.2.1(http://archive.apache.org/dist/hadoop/core/hadoop-1.2.1/hadoop-1.2.1.tar.gz) and pig-0.16.0(http://ftp.download-by.net/apache/pig/pig-0.16.0/pig-0.16.0.tar.gz) must be downloaded and unpacked into the WORKFLOW_USER_HOME.

## The installation of the automatic-workflow 

Fetch the automatic-workflow folder from github using the command extractFromGithub.sh in the tools folder
```
bash extractFromGithub.sh 1.X 
```
This will download a zipfile of the 1.X branch from github and unpack it in the folder 1.X-DD-MM-YYYY/webdanica-1.X
Where DD-MM-YY represents the current date.

Copy the 1.X-DD-MM-YYYY/webdanica-1.X/automatic-workflow folder to its correct location (i.e the WORKFLOW_USER_HOME), and change the owner of the files to the user running the automatic workflow

Correct the automatic-workflow/setenv.sh to match the wanted setup

Add the execution of the workflow to the crontab. In the file automatic-workflow/crontab.test there is a sample crontab looking like this:

```
## Run the webdanica-analysis-program every 30 minutes
*/30 * * * *  bash /home/test/automatic-workflow/webdanica-analysis-cron.sh
0 * * * * bash /home/test/cleanup_oldjobs.sh
```

In staging the webdanica-analysis-cron.sh is currently every hours:
this is done with this cron-statement:
```
0 */2  * * *  bash /home/test/automatic-workflow/webdanica-analysis-cron.sh
```
The cleanup_oldjobs.sh is found in the root of zipball. This is currently only run every 6 hours.
this declared with this cron-statement:
```
0 */6 * * * bash /home/test/cleanup_oldjobs.sh
```

Disabling the automatic workflow is most easily done by running crontab -e
and then writing '#' as the first character and then save the crontab.

Note: the mails of the user running the crontab (e.g. test) should be forwarded to the adminstrators of the webdanica system

This is most easily done by making an alias for the user in /etc/aliases ( remember to renew the aliases.db by running the /usr/bin/newaliases as root )


