# replace path in LOAD_TEST_HOME with the correct path
LOAD_TEST_HOME=/home/test/workflow

ME=`basename $0`
WEBDANICASETTINGS=$LOAD_TEST_HOME/conf/webdanica_settings.xml
OPTS1=-Dwebdanica.settings.file=$WEBDANICASETTINGS
OPTS2=-Dlogback.configurationFile=$LOAD_TEST_HOME/conf/silent_logback.xml
OPTS3=-Ddk.netarkivet.settings.file=$LOAD_TEST_HOME/conf/settings_NAS_Webdanica.xml

echo Executing $ME using  webdanica settingsfile \"$WEBDANICASETTINGS\"
java  $OPTS1 $OPTS2 $OPTS3 -cp /usr/hdp/current/phoenix-client/lib/*:lib/* dk.kb.webdanica.core.tools.LoadTest $1 $2 $3