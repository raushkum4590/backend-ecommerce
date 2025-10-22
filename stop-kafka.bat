@echo off
echo Stopping Kafka and Zookeeper...
docker-compose -f docker-compose-kafka.yml down
echo.
echo ✅ Kafka services stopped successfully!
pause

