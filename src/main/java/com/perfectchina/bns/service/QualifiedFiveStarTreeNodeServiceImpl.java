package com.perfectchina.bns.service;

import com.perfectchina.bns.common.utils.BigDecimalUtil;
import com.perfectchina.bns.common.utils.SavePinUtils;
import com.perfectchina.bns.model.Account;
import com.perfectchina.bns.model.PassUpGpv;
import com.perfectchina.bns.model.reward.BottomQualifiedFiveStarReward;
import com.perfectchina.bns.model.treenode.OpvNetTreeNode;
import com.perfectchina.bns.model.treenode.PassUpGpvNetTreeNode;
import com.perfectchina.bns.model.treenode.QualifiedFiveStarNetTreeNode;
import com.perfectchina.bns.model.treenode.TreeNode;
import com.perfectchina.bns.model.vo.QualifiedFiveStarVo;
import com.perfectchina.bns.repositories.*;
import com.perfectchina.bns.repositories.reward.BottomQualifiedFiveStarRewardRepository;
import com.perfectchina.bns.service.Enum.Pin;
import com.perfectchina.bns.service.pin.PinPoints;
import com.perfectchina.bns.service.pin.PinPosition;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class QualifiedFiveStarTreeNodeServiceImpl extends TreeNodeServiceImpl implements QualifiedFiveStarTreeNodeService {

	private static final Logger logger = LoggerFactory.getLogger(QualifiedFiveStarTreeNodeServiceImpl.class);

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM", Locale.ENGLISH);

	@Autowired
	private PassUpGpvNetTreeNodeRepository passUpGpvNetTreeNodeRepository;
	@Autowired
	private QualifiedFiveStarNetTreeNodeRepository qualifiedFiveStarNetTreeNodeRepository;
	@Autowired
    private AccountRepository accountRepository;
	@Autowired
    private AccountPinHistoryRepository accountPinHistoryRepository;
    @Autowired
    private OpvNetTreeNodeRepository opvNetTreeNodeRepository;
    @Autowired
    private BottomQualifiedFiveStarRewardRepository bottomQualifiedFiveStarRewardRepository;

	private Date previousDateEndTime; // Parameter to set calculate PPV for
										// which month

	public Date getPreviousDateEndTime() {
		return previousDateEndTime;
	}

	public void setPreviousDateEndTime(Date previousDateEndTime) {
		this.previousDateEndTime = previousDateEndTime;
	}

	// Need to walk through simple net, therefore, return simple net tree node
	// repository
	public TreeNodeRepository<QualifiedFiveStarNetTreeNode> getTreeNodeRepository() {
		return qualifiedFiveStarNetTreeNodeRepository;
	}

	public boolean isReadyToUpdate() {
		// need to check if Simple Net already exist, otherwise, cannot
		// calculate
		boolean isReady = false;
		// // Current month
		String snapshotDate = null;
		try {
			snapshotDate = sdf.format(getPreviousDateEndTime());
			PassUpGpvNetTreeNode rootNode = passUpGpvNetTreeNodeRepository.getRootTreeNodeOfMonth(snapshotDate);

			if (rootNode != null) {
				isReady = true;
			}
		} catch (Exception ex) {
			logger.error("isReadyToUpdate, invalidDate=" + getPreviousDateEndTime());
		}
		return isReady;
	}

	@Override
	public void updateWholeTree(String snapshotDate) {
		TreeNode rootNode = passUpGpvNetTreeNodeRepository.getRootTreeNode(snapshotDate);
		updateChildTreeLevel( 0, rootNode,snapshotDate );
	}

	@Override
	public void updateChildTreeLevel(Integer fromLevelNum, TreeNode fromNode,String snapshotDate) {
		super.updateChildTreeLevel(fromLevelNum, fromNode,snapshotDate);
	}

	public int getMaxTreeLevel(String snapShotDate) {
        Integer maxLevelNum = getTreeNodeRepository().getMaxLevelNum(snapShotDate);
        return maxLevelNum == null ? -1 : maxLevelNum;
	}

	private Map<Long,Long> relation = new HashMap<>();

    @Override
    protected void process(TreeNode node, String snapshotDate) {
        logger.debug("process, update node=" + node.getData().getAccountNum() + "/" + node.getData().getName()
                + ", level [" + node.getLevelNum() + "].");
        // 当前节点
        PassUpGpvNetTreeNode passUpGpvNetTreeNode = (PassUpGpvNetTreeNode) node;
        // 待装载节点
        QualifiedFiveStarNetTreeNode qualifiedFiveStarNetTreeNode = new QualifiedFiveStarNetTreeNode();

        Float passUpGpv = passUpGpvNetTreeNode.getPassUpGpv();
        int qualifiedLine = passUpGpvNetTreeNode.getQualifiedLine();
        long id = passUpGpvNetTreeNode.getId();
        Long mapPassUpGpvUplinkId = relation.get(id);

        long passUpGpvUplinkId = mapPassUpGpvUplinkId != null ? mapPassUpGpvUplinkId : passUpGpvNetTreeNode.getUplinkId();

        if(passUpGpvUplinkId!=0){
            PassUpGpvNetTreeNode passUpGpvUplink = passUpGpvNetTreeNodeRepository.getOne(passUpGpvUplinkId);
            String accountNum = passUpGpvUplink.getData().getAccountNum();
            QualifiedFiveStarNetTreeNode qualifiedFiveStarUplink = getTreeNodeRepository().getAccountByAccountNum(snapshotDate,
                    accountNum);
            long qualifiedFiveStarUplinkId = qualifiedFiveStarUplink.getId();
            String uplinkLevelLine = qualifiedFiveStarUplink.getLevelLine();
            qualifiedFiveStarNetTreeNode.setUplinkId(qualifiedFiveStarUplinkId);
            // 判断是否为合格五星
            if (passUpGpv >= PinPoints.COMMON_QUALIFY_POINTS){
                setuplinkLevelLineAndLevel(uplinkLevelLine,qualifiedFiveStarNetTreeNode,qualifiedFiveStarUplinkId);
                copyNetTree(passUpGpvNetTreeNode,qualifiedFiveStarNetTreeNode,snapshotDate);
            }else{
                // 判断是否为红宝石
                if ((passUpGpv >= PinPoints.RUBY_QUALIFY_POINTS && qualifiedLine >= 1) || (qualifiedLine >= 2)){
                    setuplinkLevelLineAndLevel(uplinkLevelLine,qualifiedFiveStarNetTreeNode,qualifiedFiveStarUplinkId);
                    copyNetTree(passUpGpvNetTreeNode,qualifiedFiveStarNetTreeNode,snapshotDate);
                }else {
                    // 过滤此节点
                    // 获取此节点的直接下级
                    List<PassUpGpvNetTreeNode> childNodes = passUpGpvNetTreeNodeRepository.getChildNodesByUpid(id);
                    // 将直接下级的id和上级的id放入map中
                    if (childNodes.size()>0){
                        for (TreeNode childNode : childNodes){
                            PassUpGpvNetTreeNode passUpGpvNetTreeChildNode = (PassUpGpvNetTreeNode)childNode;
                            relation.put(passUpGpvNetTreeChildNode.getId(), passUpGpvUplinkId);
                        }
                    }
                }
            }
            qualifiedFiveStarNetTreeNodeRepository.save(qualifiedFiveStarUplink);
        }else {
            qualifiedFiveStarNetTreeNode.setLevelLine(String.valueOf(passUpGpvUplinkId));
            qualifiedFiveStarNetTreeNode.setLevelNum(0);
            copyNetTree(passUpGpvNetTreeNode,qualifiedFiveStarNetTreeNode,snapshotDate);
        }
        relation.remove(id);
    }

    private void setuplinkLevelLineAndLevel(String uplinkLevelLine,QualifiedFiveStarNetTreeNode qualifiedFiveStarNetTreeNode,Long qualifiedFiveStarUplinkId){
        String newUplinkLevelLine = String.valueOf(new StringBuilder().append(uplinkLevelLine).append("_").append(qualifiedFiveStarUplinkId));
        String[] newUplinkLevelLines = StringUtils.split(newUplinkLevelLine, "_");
        int level = newUplinkLevelLines.length-1;
        qualifiedFiveStarNetTreeNode.setLevelLine(newUplinkLevelLine);
        qualifiedFiveStarNetTreeNode.setLevelNum(level);
    }

	private void copyNetTree(PassUpGpvNetTreeNode passUpGpvNetTreeNode,QualifiedFiveStarNetTreeNode qualifiedFiveStarNetTreeNode,String snapshotDate){
        String accountNum = passUpGpvNetTreeNode.getData().getAccountNum();
		OpvNetTreeNode opvNetTreeNode = opvNetTreeNodeRepository.findByAccountNum(snapshotDate,accountNum);
        Float asteriskNodePoints = passUpGpvNetTreeNode.getAsteriskNodePoints();
        Boolean hasAsteriskNode = passUpGpvNetTreeNode.getHasAsteriskNode();
        qualifiedFiveStarNetTreeNode.setAsteriskNodePoints(asteriskNodePoints == null ? 0F : asteriskNodePoints);
		qualifiedFiveStarNetTreeNode.setHasAsteriskNode(hasAsteriskNode == null ? false : hasAsteriskNode);
        qualifiedFiveStarNetTreeNode.setPassUpGpv(passUpGpvNetTreeNode.getPassUpGpv());
        qualifiedFiveStarNetTreeNode.setPpv(passUpGpvNetTreeNode.getPpv());
        qualifiedFiveStarNetTreeNode.setGpv(passUpGpvNetTreeNode.getGpv());
		qualifiedFiveStarNetTreeNode.setQualifiedLine(passUpGpvNetTreeNode.getQualifiedLine());
        qualifiedFiveStarNetTreeNode.setGoldDiamondLine(0);
        qualifiedFiveStarNetTreeNode.setEmeraldLine(0);
		qualifiedFiveStarNetTreeNode.setSnapshotDate(passUpGpvNetTreeNode.getSnapshotDate());
        qualifiedFiveStarNetTreeNode.setAboveEmeraldNodeSign(false);
        qualifiedFiveStarNetTreeNode.setOpv(opvNetTreeNode.getOpv());
//        qualifiedFiveStarNetTreeNode.setEndFiveStar(false);
		qualifiedFiveStarNetTreeNode.setData(passUpGpvNetTreeNode.getData());
		qualifiedFiveStarNetTreeNode.setBorrowedPoints(0F);
		qualifiedFiveStarNetTreeNode.setBorrowPoints(0F);
		qualifiedFiveStarNetTreeNodeRepository.save(qualifiedFiveStarNetTreeNode);
	}

    private Map<Long,Integer> goldDiamondLine = new HashMap<>();
    private Map<Long,List<PassUpGpv>> downLines = new HashMap<>();

    @Override
	/**
	 * Update the entire tree's pass-up-gpv
	 */
	public void updateWholeTreeQualifiedFiveStar(String snapShotDate) {
		// Get the highest level of a tree
		int treeLevel = getMaxTreeLevel(snapShotDate);
		if (treeLevel < 0)
			return;
		while (treeLevel > 0) {
			List<QualifiedFiveStarNetTreeNode> thisTreeLevelList = qualifiedFiveStarNetTreeNodeRepository.getTreeNodesByLevelAndSnapshotDate(snapShotDate,treeLevel);
			for (QualifiedFiveStarNetTreeNode qualifiedFiveStarNetTreeNode : thisTreeLevelList) {
                // first
			    long uplinkId = qualifiedFiveStarNetTreeNode.getUplinkId();
                Float passUpGpv = qualifiedFiveStarNetTreeNode.getPassUpGpv();
                int qualifiedLine = qualifiedFiveStarNetTreeNode.getQualifiedLine();
                long id = qualifiedFiveStarNetTreeNode.getId();
                List<QualifiedFiveStarNetTreeNode> childs = qualifiedFiveStarNetTreeNodeRepository.getChildNodesByUpid(id);
                // 翡翠线
                int EmeraldLine = 0;
                if (childs.size()>0){
                    qualifiedFiveStarNetTreeNode.setHasChild(true);
                    for (QualifiedFiveStarNetTreeNode qualifiedFiveChildNode : childs){
                        String pin = qualifiedFiveChildNode.getData().getPin();
                        // 爬树获取下级翡翠线
                        if (Pin.descOf(pin).getCode() >= Pin.descOf(PinPosition.EMERALD).getCode()){
                            EmeraldLine+=1;
                        }
                    }
                }else {
                    qualifiedFiveStarNetTreeNode.setHasChild(false);
                }
                qualifiedFiveStarNetTreeNode.setEmeraldLine(EmeraldLine);

                List<PassUpGpv> downLinePassUpGpvs = downLines.get(uplinkId);
                List<PassUpGpv> needSortDownLinePassUpGpvs = new ArrayList<>();
                PassUpGpv passUpGpvVo = new PassUpGpv();
                passUpGpvVo.setId(id);
                passUpGpvVo.setPassUpGpv(qualifiedFiveStarNetTreeNode.getPassUpGpv());
                if (downLinePassUpGpvs != null){
                    for (PassUpGpv brotherPassUpGpv : downLinePassUpGpvs){
                        needSortDownLinePassUpGpvs.add(brotherPassUpGpv);
                    }
                }
                needSortDownLinePassUpGpvs.add(passUpGpvVo);
                // 对所有直接下级排序
                List<PassUpGpv> sortedDownLinePassUpGpvs = SortFiveStarIntegral(needSortDownLinePassUpGpvs);
                downLines.put(uplinkId,sortedDownLinePassUpGpvs);
                // second
                // 计算五星代积分
                Float fiveStarIntegral = qualifiedFiveStarNetTreeNode.getPassUpGpv();
                // 获取所有直接下级
                List<PassUpGpv> passUpGpvs = downLines.get(id);
                long accountId = qualifiedFiveStarNetTreeNode.getData().getId();
                String pin;
                if (passUpGpvs != null){
                    Iterator<PassUpGpv> passUpGpvIterator = passUpGpvs.iterator();
                    List<QualifiedFiveStarNetTreeNode> nodes = new ArrayList<>();
                    // 如果合格则不需要借分
                    if (fiveStarIntegral >= PinPoints.COMMON_QUALIFY_POINTS){
                        pin = changeAndGetPin(fiveStarIntegral, passUpGpv, qualifiedLine, accountId,childs,qualifiedFiveStarNetTreeNode);
                        getGoldDiamondLine(id,uplinkId,qualifiedFiveStarNetTreeNode,pin);
                        qualifiedFiveStarNetTreeNode.setFiveStarIntegral(fiveStarIntegral);
                        qualifiedFiveStarNetTreeNodeRepository.save(qualifiedFiveStarNetTreeNode);
                    }else {
                        // 开始借分
                        // 获取借分前的等级
                        String beforeBorrowPin = getPin(fiveStarIntegral, passUpGpv, qualifiedLine);
                        while (!(fiveStarIntegral >= PinPoints.COMMON_QUALIFY_POINTS)){
                            if (passUpGpvIterator.hasNext()) {
                                PassUpGpv maxPassUpGpv = passUpGpvIterator.next();
                                // 获取需要借多少分
                                Float needBorrowPoints = PinPoints.COMMON_QUALIFY_POINTS-fiveStarIntegral;
                                // 获取紧缩gpv最高的节点
                                QualifiedFiveStarNetTreeNode downLineNode = qualifiedFiveStarNetTreeNodeRepository.getOne(maxPassUpGpv.id);
                                Float downlineFiveStarIntegral = downLineNode.getFiveStarIntegral();
                                // 设置下级借出给谁
                                downLineNode.setBorrowTo(qualifiedFiveStarNetTreeNode.getId());
                                // 设置下级借出的值
                                downLineNode.setBorrowedPoints(needBorrowPoints);
                                // 判断下级的分数是否大于上级需要借的分值
                                Boolean isAchieve = downlineFiveStarIntegral > needBorrowPoints;
                                Float downLineNodeFiveStarIntegral = isAchieve ? downlineFiveStarIntegral - needBorrowPoints : 0F;
                                downLineNode.setFiveStarIntegral(downLineNodeFiveStarIntegral);
                                fiveStarIntegral+=(isAchieve ? needBorrowPoints : downlineFiveStarIntegral);

                                Float borrowPoints = qualifiedFiveStarNetTreeNode.getBorrowPoints() != null ? qualifiedFiveStarNetTreeNode.getBorrowPoints() : 0F;
                                qualifiedFiveStarNetTreeNode.setBorrowPoints(borrowPoints+(isAchieve ? needBorrowPoints : downlineFiveStarIntegral));
                                qualifiedFiveStarNetTreeNode.setFiveStarIntegral(fiveStarIntegral);
                                if (isAchieve){
                                    if (downLineNodeFiveStarIntegral >= PinPoints.COMMON_QUALIFY_POINTS){
                                        // 借分后合格，计算等级
                                        pin = changeAndGetPin(fiveStarIntegral, passUpGpv, qualifiedLine, accountId,childs,qualifiedFiveStarNetTreeNode);
                                        getGoldDiamondLine(id,uplinkId,qualifiedFiveStarNetTreeNode,pin);
                                    }else {
                                        // 如果借分后仍不合格，则合格线减一
                                        // 红宝石职级不受影响
                                        qualifiedLine = qualifiedLine == 0 ? 0 : qualifiedLine-1;
                                        if (beforeBorrowPin == PinPosition.RUBY){
                                            Account account = accountRepository.getAccountById(accountId);
                                            SavePinUtils.savePinAndHistory(account, PinPosition.RUBY,accountPinHistoryRepository,accountRepository);
                                        }else {
                                            pin = changeAndGetPin(fiveStarIntegral,passUpGpv,qualifiedLine,accountId,childs,qualifiedFiveStarNetTreeNode);
                                            getGoldDiamondLine(id,uplinkId,qualifiedFiveStarNetTreeNode,pin);
                                        }
                                        qualifiedFiveStarNetTreeNode.setQualifiedLine(qualifiedLine);
                                    }
                                }else {
                                    qualifiedLine = qualifiedLine == 0 ? 0 : qualifiedLine-1;
                                    qualifiedFiveStarNetTreeNode.setQualifiedLine(qualifiedLine);
                                    passUpGpvIterator.remove();
                                }
                                nodes.add(downLineNode);
                                nodes.add(qualifiedFiveStarNetTreeNode);
                                qualifiedFiveStarNetTreeNodeRepository.saveAll(nodes);
//                                qualifiedFiveStarNetTreeNodeRepository.flush();
                            }else {
                                break;
                            }
                        }
                    }
                }else {
                    // A node that has no subordinates or does not need to borrow points,Direct calculation of five star generation integral
                    pin = changeAndGetPin(fiveStarIntegral, passUpGpv, qualifiedLine, accountId,childs,qualifiedFiveStarNetTreeNode);
                    getGoldDiamondLine(id,uplinkId,qualifiedFiveStarNetTreeNode,pin);
                    qualifiedFiveStarNetTreeNode.setFiveStarIntegral(fiveStarIntegral);
                    qualifiedFiveStarNetTreeNodeRepository.save(qualifiedFiveStarNetTreeNode);
                }

			} // end for loop
			treeLevel--;
		}
	}

    @Override
    public void calculateBottomQualifiedFiveStarReward(String snapShotDate) {
        int bottomQualifiedFiveStarCount = qualifiedFiveStarNetTreeNodeRepository.countTreeNodesBySnapshotDateAndPin(snapShotDate,PinPosition.BOTTOM_QUALIFIED_5_STAR);
        BigDecimal Reward = BigDecimalUtil.multiply(500D, Double.valueOf(bottomQualifiedFiveStarCount));
        BottomQualifiedFiveStarReward bottomQualifiedFiveStarReward = new BottomQualifiedFiveStarReward();
        bottomQualifiedFiveStarReward.setCreatedBy("TerryTang");
        bottomQualifiedFiveStarReward.setLastUpdatedBy("TerryTang");
        bottomQualifiedFiveStarReward.setBottomQualifiedFiveStarReward(Reward);
        bottomQualifiedFiveStarRewardRepository.save(bottomQualifiedFiveStarReward);
    }

    private void getGoldDiamondLine(long id,long uplinkId,QualifiedFiveStarNetTreeNode qualifiedFiveStarNetTreeNode,String pin){
        Integer gLine = goldDiamondLine.get(id);
        if (pin == PinPosition.GOLD_DIAMOND){
            // 如果当前元素为金钻
            Integer uplinkGLine = goldDiamondLine.get(uplinkId);
            if (uplinkGLine != null){
                goldDiamondLine.put(uplinkId,uplinkGLine+1);
            }else {
                goldDiamondLine.put(uplinkId,1);
            }
            if (gLine != null){
                qualifiedFiveStarNetTreeNode.setGoldDiamondLine(gLine);
            }
        }else {
            // 如果当前元素不为金钻
            if (gLine != null){
                if (Pin.descOf(pin).getCode() >= Pin.descOf(PinPosition.EMERALD).getCode()){
                    // 如果上级是翡翠及以上,则无论下级有多少个金钻都只算一条金钻线
                    goldDiamondLine.put(uplinkId,1);
                }else {
                    // 如果上级是翡翠以下,则下级有多少个金钻就算多少条金钻线
                    goldDiamondLine.put(uplinkId,gLine);
                }
            }
        }
        goldDiamondLine.remove(id);
    }

    private String getPin(Float currentFiveStarIntegral,Float passUpGpv,int qualifiedLine){
	    if (passUpGpv == null){
            logger.error("passUpGpv为空");
            return null;
        }
        if ((qualifiedLine >= 8) || (qualifiedLine >= 7 && currentFiveStarIntegral >= PinPoints.COMMON_QUALIFY_POINTS)){
            return PinPosition.GOLD_DIAMOND;
        }
        if ((qualifiedLine >= 6) || (qualifiedLine >= 5 && currentFiveStarIntegral >= PinPoints.COMMON_QUALIFY_POINTS)){
            return PinPosition.DIAMOND;
        }
        if ((qualifiedLine >= 4) || (qualifiedLine >= 3 && currentFiveStarIntegral >= PinPoints.COMMON_QUALIFY_POINTS)){
            return PinPosition.EMERALD;
        }
        if ((qualifiedLine >= 2) || (qualifiedLine >= 1 && passUpGpv >= PinPoints.RUBY_QUALIFY_POINTS)){
            return PinPosition.RUBY;
        }
        if (passUpGpv >= PinPoints.RUBY_QUALIFY_POINTS){
            return PinPosition.QUALIFIED_FIVE_STAR;
        }
        return null;
    }

	private String changeAndGetPin(Float currentFiveStarIntegral,Float passUpGpv,int qualifiedLine,Long id,List<QualifiedFiveStarNetTreeNode> childs,QualifiedFiveStarNetTreeNode qualifiedFiveStarNetTreeNode){
        if (passUpGpv == null){
            logger.error("passUpGpv为空");
            return null;
        }
        Account account = accountRepository.getAccountById(id);
        if ((qualifiedLine >= 8) || (qualifiedLine >= 7 && currentFiveStarIntegral >= PinPoints.COMMON_QUALIFY_POINTS)){
            SavePinUtils.savePinAndHistory(account, PinPosition.GOLD_DIAMOND,accountPinHistoryRepository,accountRepository);
            return PinPosition.GOLD_DIAMOND;
        }
        if ((qualifiedLine >= 6) || (qualifiedLine >= 5 && currentFiveStarIntegral >= PinPoints.COMMON_QUALIFY_POINTS)){
            SavePinUtils.savePinAndHistory(account, PinPosition.DIAMOND,accountPinHistoryRepository,accountRepository);
            return PinPosition.DIAMOND;
        }
        if ((qualifiedLine >= 4) || (qualifiedLine >= 3 && currentFiveStarIntegral >= PinPoints.COMMON_QUALIFY_POINTS)){
            SavePinUtils.savePinAndHistory(account, PinPosition.EMERALD,accountPinHistoryRepository,accountRepository);
            return PinPosition.EMERALD;
        }
        if ((qualifiedLine >= 2) || (qualifiedLine >= 1 && passUpGpv >= PinPoints.RUBY_QUALIFY_POINTS)){
            SavePinUtils.savePinAndHistory(account, PinPosition.RUBY,accountPinHistoryRepository,accountRepository);
            return PinPosition.RUBY;
        }
        if (passUpGpv >= PinPoints.RUBY_QUALIFY_POINTS){
            // 判断职级是否为合格五星，是：添加标记，可获取尾线五星奖
            if (!(childs.size()>0)){
//                qualifiedFiveStarNetTreeNode.setEndFiveStar(true);
                SavePinUtils.savePinAndHistory(account, PinPosition.BOTTOM_QUALIFIED_5_STAR,accountPinHistoryRepository,accountRepository);
                return PinPosition.BOTTOM_QUALIFIED_5_STAR;
            }
            SavePinUtils.savePinAndHistory(account, PinPosition.QUALIFIED_FIVE_STAR,accountPinHistoryRepository,accountRepository);
            return PinPosition.QUALIFIED_FIVE_STAR;
        }
        return null;
    }

	private List<PassUpGpv> SortFiveStarIntegral(List<PassUpGpv> passUpGpvs){
	    if (passUpGpvs.size() <= 1){
            return passUpGpvs;
        }
        Collections.sort(passUpGpvs);
        return passUpGpvs;
    }

    public TreeNode getRootNode(String snapshotDate) {
        TreeNode rootNode = qualifiedFiveStarNetTreeNodeRepository.getRootTreeNodeOfMonth( snapshotDate );
        return rootNode;
    }

    @Override
    public List<QualifiedFiveStarVo> convertQualifiedFiveStarVo(String snapshotDate) {
        List<QualifiedFiveStarVo> qualifiedFiveStarVos = new ArrayList<>();
        // 获取level为1的数据
        List<QualifiedFiveStarNetTreeNode> qualifiedFiveStarNetTreeNodes = getTreeNodeRepository().getTreeNodesByLevelAndSnapshotDate(snapshotDate,1);
        if (qualifiedFiveStarNetTreeNodes.size() >0 ){
            for (QualifiedFiveStarNetTreeNode qualifiedFiveStarNetTreeNode : qualifiedFiveStarNetTreeNodes){
                QualifiedFiveStarVo qualifiedFiveStarVo = recursion(qualifiedFiveStarNetTreeNode);
                qualifiedFiveStarVos.add(qualifiedFiveStarVo);
            }
        }
        return qualifiedFiveStarVos;
    }

    private QualifiedFiveStarVo recursion(QualifiedFiveStarNetTreeNode qualifiedFiveStarNetTreeNode){
        List<QualifiedFiveStarNetTreeNode> childs = getTreeNodeRepository().findByParentId(qualifiedFiveStarNetTreeNode.getId());
        List<QualifiedFiveStarVo> nodes = new ArrayList<>();
        if (childs != null){
            for (QualifiedFiveStarNetTreeNode child : childs){
                QualifiedFiveStarVo node = recursion(child);
                nodes.add(node);
            }
        }
        return  convertChildFiveStarVo(qualifiedFiveStarNetTreeNode,nodes);
    }

    private QualifiedFiveStarVo convertChildFiveStarVo(QualifiedFiveStarNetTreeNode child, List<QualifiedFiveStarVo> nodes){
        QualifiedFiveStarVo childVo = new QualifiedFiveStarVo();
        childVo.setLevelNum(child.getLevelNum());
        childVo.setName(child.getData().getName());
        childVo.setAccountNum(child.getData().getAccountNum());
        childVo.setPpv(child.getPpv());
        childVo.setGpv(child.getGpv());
        childVo.setOpv(child.getOpv());
        childVo.setPassUpGpv(child.getPassUpGpv());
        childVo.setBorrowPoints(child.getBorrowPoints());
        childVo.setBorrowedPoints(child.getBorrowedPoints());
        childVo.setFiveStarIntegral(child.getFiveStarIntegral());
        int qualifiedLine = child.getQualifiedLine();
        childVo.setQualifiedLine(qualifiedLine < 0 ? 0 : qualifiedLine);
        childVo.setPin(Pin.descOf(child.getData().getPin()).getCode());
        childVo.setMaxPin(Pin.descOf(child.getData().getMaxPin()).getCode());
        childVo.setChildren(nodes);
        return childVo;
    }
}
