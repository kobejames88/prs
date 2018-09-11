
-- ----------------------------
-- Records of CustomerBonusRate
-- ----------------------------
DELETE FROM `CustomerBonusRate`;
INSERT INTO `CustomerBonusRate` VALUES ('1', '500', '0', null, null, '1999-08-02', '2099-08-02', null, null);
INSERT INTO `CustomerBonusRate` VALUES ('2', '9000', '0.06', null, null, '1999-08-02', '2099-08-02', null, null);
INSERT INTO `CustomerBonusRate` VALUES ('3', '18000', '0.09', null, null, '1999-08-02', '2099-08-02', null, null);
INSERT INTO `CustomerBonusRate` VALUES ('4', '99999999999', '0.12', null, null, '1999-08-02', '2099-08-02', null, null);

-- ----------------------------
-- Records of DistributorBonusRate
-- ----------------------------
DELETE FROM `DistributorBonusRate`;
INSERT INTO `DistributorBonusRate` VALUES ('1', '0.18', null, null, '1999-08-13', '2099-08-13', null, null, '8999', '200');
INSERT INTO `DistributorBonusRate` VALUES ('2', '0.21', null, null, '1999-08-13', '2099-08-13', null, null, '17999', '9000');
INSERT INTO `DistributorBonusRate` VALUES ('3', '0.24', null, null, '1999-08-13', '2099-08-13', null, null, '100000000000', '18000');

-- ----------------------------
-- Records of DistributorDifferentialBonusRate
-- ----------------------------
DELETE FROM `DistributorDifferentialBonusRate`;
INSERT INTO `DistributorDifferentialBonusRate` VALUES ('1', '0.18', null, null, '1999-08-13', '2099-08-13', null, null, '8999', '200');
INSERT INTO `DistributorDifferentialBonusRate` VALUES ('2', '0.21', null, null, '1999-08-13', '2099-08-13', null, null, '17999', '9000');
INSERT INTO `DistributorDifferentialBonusRate` VALUES ('3', '0.24', null, null, '1999-08-13', '2099-08-13', null, null, '10000000000', '18000');

-- ----------------------------
-- Records of LeaderBonusRate
-- ----------------------------
DELETE FROM `LeaderBonusRate`;
INSERT INTO `LeaderBonusRate` VALUES ('1', 'chenhuahai', '2018-08-16', '0.03', '1999-08-16', '2099-01-01', '0.07', '0.01', 'chenhuahai', '2018-08-16', '0.11', '0.02');

-- ----------------------------
-- Records of GoldenDiamondOPVBonusRate
-- ----------------------------
INSERT INTO `GoldenDiamondOPVBonusRate` VALUES ('1', '0.01', '0.005', '0.0025', '0.0025', '0.001', '0.001', '0.001', '0.001', null, null, '1999-08-28', '2099-08-28', null, null);

-- ----------------------------
-- Records of triplegoldendiamondbonusrate
-- ----------------------------
INSERT INTO `TripleGoldenDiamondBonusRate` VALUES ('1', 'lightway', '2018-09-11', '0.0005', '1999-09-11', '2099-01-11', 'chen', '2018-09-11', '0.0005');
