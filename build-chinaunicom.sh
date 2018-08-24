#  this file will be executed by jenkins #
docker build -t 10.172.49.246/wanmei_pcu_test/perfect-bns .
docker login -u wmpcu -p Wmpcu123 10.172.49.246
docker push 10.172.49.246/wanmei_pcu_test/perfect-bns
