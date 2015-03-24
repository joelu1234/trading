@echo off
pushd..
SET PARENT_DIR=%cd%
popd
SET CLASSPATH=%PARENT_DIR%\lib\*;%PARENT_DIR%;%PARENT_DIR%\config
java -Xms1024M -cp %CLASSPATH% -Dlog.file=%PARENT_DIR%\logs\trading.log trading.jetty.TradingServer
@echo on