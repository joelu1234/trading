@echo off
pushd..
SET PARENT_DIR=%cd%
popd
SET CLASSPATH=..\lib\*;..\;..\config
java -Xms1024M -cp %CLASSPATH% -Dlog.file=%PARENT_DIR%\logs\trading.log trading.service.TradingServer
@echo on