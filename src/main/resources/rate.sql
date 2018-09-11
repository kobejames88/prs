
-- ----------------------------
-- Records of customerbonusrate 顾客级差奖计算比率
-- ----------------------------
INSERT INTO `customerbonusrate` VALUES ('1', '500', '0', null, null, '1999-08-02', '2099-08-02', null, null);
INSERT INTO `customerbonusrate` VALUES ('2', '9000', '0.06', null, null, '1999-08-02', '2099-08-02', null, null);
INSERT INTO `customerbonusrate` VALUES ('3', '18000', '0.09', null, null, '1999-08-02', '2099-08-02', null, null);
INSERT INTO `customerbonusrate` VALUES ('4', '99999999999', '0.12', null, null, '1999-08-02', '2099-08-02', null, null);

-- ----------------------------
-- Records of distributorbonusrate 直销员奖计算比率
-- ----------------------------
INSERT INTO `distributorbonusrate` VALUES ('1', '0.18', null, null, '1999-08-13', '2099-08-13', null, null, '8999', '200');
INSERT INTO `distributorbonusrate` VALUES ('2', '0.21', null, null, '1999-08-13', '2099-08-13', null, null, '17999', '9000');
INSERT INTO `distributorbonusrate` VALUES ('3', '0.24', null, null, '1999-08-13', '2099-08-13', null, null, '100000000000', '18000');

-- ----------------------------
-- Records of distributordifferentialbonusrate 直销员级差计算比率
-- ----------------------------
INSERT INTO `distributordifferentialbonusrate` VALUES ('1', '0.18', null, null, '1999-08-13', '2099-08-13', null, null, '8999', '200');
INSERT INTO `distributordifferentialbonusrate` VALUES ('2', '0.21', null, null, '1999-08-13', '2099-08-13', null, null, '17999', '9000');
INSERT INTO `distributordifferentialbonusrate` VALUES ('3', '0.24', null, null, '1999-08-13', '2099-08-13', null, null, '10000000000', '18000');

-- ----------------------------
-- Records of leaderbonusrate 领导奖计算比率
-- ----------------------------
INSERT INTO `leaderbonusrate` VALUES ('1', 'chenhuahai', '2018-08-16', '0.03', '1999-08-16', '2099-01-01', '0.07', '0.01', 'chenhuahai', '2018-08-16', '0.11', '0.02');

-- ----------------------------
-- Records of goldendiamondopvbonusrate 金钻平级奖计算比率
-- ----------------------------
INSERT INTO `goldendiamondopvbonusrate` VALUES ('1', '0.01', '0.005', '0.0025', '0.0025', '0.001', '0.001', '0.001', '0.001', null, null, '1999-08-28', '2099-08-28', null, null);

-- ----------------------------
-- Records of triplegoldendiamondbonusrate 三金钻奖计算比率
-- ----------------------------
INSERT INTO `triplegoldendiamondbonusrate` VALUES ('1', 'lightway', '2018-09-11', '0.0005', '1999-09-11', '2099-01-11', 'chen', '2018-09-11', '0.0005');
