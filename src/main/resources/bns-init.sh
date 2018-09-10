#构建原始网络图
curl -X PUT http://localhost:8080/bns/api/simpleNet/201808

#构建活跃网络图
curl -X PUT http://localhost:8080/bns/api/activeNet/201808

#计算opv
curl -X PUT http://localhost:8080/bns/api/opvNet/201808

#计算gpv
curl -X PUT http://localhost:8080/bns/api/gpvNet/201808

#构建五星网络图
curl -X PUT http://localhost:8080/bns/api/fiveStarNet/201808

#计算passUpGpv
curl -X PUT http://localhost:8080/bns/api/passUpGpvNet/201808

#构建合格五星网络图
curl -X PUT http://localhost:8080/bns/api/qualifiedFiveStar/201808

#计算顾客级差
curl -X GET http://localhost:8080/bns/api/reward/create/rewardnet/201808

#计算直销员奖
curl -X GET http://localhost:8080/bns/api/reward/create/distributor/bonusnet/201808

#计算直销员级差
curl -X GET http://localhost:8080/bns/api/reward/create/distributordifferential/bonusnet/201808

#计算领导奖
curl -X GET http://localhost:8080/bns/api/reward/create/leader/bonus/201808

#计算金钻平级奖
curl -X GET http://localhost:8080/bns/api/reward/calculate/goldendiamondopv/bonus/201808

