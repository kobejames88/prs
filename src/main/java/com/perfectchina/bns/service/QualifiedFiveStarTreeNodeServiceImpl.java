package com.perfectchina.bns.service;

import com.perfectchina.bns.model.Account;
import com.perfectchina.bns.model.AccountPinHistory;
import com.perfectchina.bns.model.PassUpGpv;
import com.perfectchina.bns.model.treenode.FiveStarNetTreeNode;
import com.perfectchina.bns.model.treenode.PassUpGpvNetTreeNode;
import com.perfectchina.bns.model.treenode.QualifiedFiveStarNetTreeNode;
import com.perfectchina.bns.model.treenode.TreeNode;
import com.perfectchina.bns.model.vo.QualifiedFiveStarVo;
import com.perfectchina.bns.repositories.*;
import com.perfectchina.bns.service.Enum.Pin;
import com.perfectchina.bns.service.pin.PinPoints;
import com.perfectchina.bns.service.pin.PinPosition;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private FiveStarNetTreeNodeRepository fiveStarNetTreeNodeRepository;
	@Autowired
    private AccountPinHistoryRepository accountPinHistoryRepository;

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
        // Current element
        PassUpGpvNetTreeNode passUpGpvNetTreeNode = (PassUpGpvNetTreeNode) node;
        // Element to be loaded
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
            // To judge if it is a qualified five-star
            if (passUpGpv >= PinPoints.COMMON_QUALIFY_POINTS){
                setuplinkLevelLineAndLevel(uplinkLevelLine,qualifiedFiveStarNetTreeNode,qualifiedFiveStarUplinkId);
                copyNetTree(passUpGpvNetTreeNode,qualifiedFiveStarNetTreeNode);
            }else{
                // To judge if it is a ruby
                if ((passUpGpv >= PinPoints.RUBY_QUALIFY_POINTS && qualifiedLine >= 1) || (qualifiedLine >= 2)){
                    setuplinkLevelLineAndLevel(uplinkLevelLine,qualifiedFiveStarNetTreeNode,qualifiedFiveStarUplinkId);
                    copyNetTree(passUpGpvNetTreeNode,qualifiedFiveStarNetTreeNode);
                }else {
                    // Filter this element
                    // Gets the direct subordinate of this element
                    List<PassUpGpvNetTreeNode> childNodes = passUpGpvNetTreeNodeRepository.getChildNodesByUpid(id);

                    // The direct subordinate ID of this element and the higher level ID are placed in map
                    if (childNodes.size()>0){
                        for (TreeNode childNode : childNodes){
                            PassUpGpvNetTreeNode passUpGpvNetTreeChildNode = (PassUpGpvNetTreeNode)childNode;
                            relation.put(passUpGpvNetTreeChildNode.getId(), passUpGpvUplinkId);
                        }
                    }

                }
            }
            qualifiedFiveStarNetTreeNodeRepository.saveAndFlush(qualifiedFiveStarUplink);
        }else {
            qualifiedFiveStarNetTreeNode.setLevelLine(String.valueOf(passUpGpvUplinkId));
            qualifiedFiveStarNetTreeNode.setLevelNum(0);
            copyNetTree(passUpGpvNetTreeNode,qualifiedFiveStarNetTreeNode);
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

	private void copyNetTree(PassUpGpvNetTreeNode passUpGpvNetTreeNode,QualifiedFiveStarNetTreeNode qualifiedFiveStarNetTreeNode){
        Float asteriskNodePoints = passUpGpvNetTreeNode.getAsteriskNodePoints();
        Boolean hasAsteriskNode = passUpGpvNetTreeNode.getHasAsteriskNode();
        qualifiedFiveStarNetTreeNode.setAsteriskNodePoints(asteriskNodePoints == null ? 0F : asteriskNodePoints);
		qualifiedFiveStarNetTreeNode.setHasAsteriskNode(hasAsteriskNode == null ? false : hasAsteriskNode);
        qualifiedFiveStarNetTreeNode.setPassUpGpv(passUpGpvNetTreeNode.getPassUpGpv());
        qualifiedFiveStarNetTreeNode.setGpv(passUpGpvNetTreeNode.getGpv());
		qualifiedFiveStarNetTreeNode.setQualifiedLine(passUpGpvNetTreeNode.getQualifiedLine());
		qualifiedFiveStarNetTreeNode.setSnapshotDate(passUpGpvNetTreeNode.getSnapshotDate());
		qualifiedFiveStarNetTreeNode.setData(passUpGpvNetTreeNode.getData());
		qualifiedFiveStarNetTreeNodeRepository.saveAndFlush(qualifiedFiveStarNetTreeNode);
	}

	@Override
	/**
	 * Update the entire tree's pass-up-gpv
	 */
	public void updateWholeTreeQualifiedFiveStar(String snapShotDate) {
		// Get the highest level of a tree
		int treeLevel = getMaxTreeLevel(snapShotDate);
		if (treeLevel < 0)
			return;

		Map<Long,List<PassUpGpv>> downLines = new HashMap<>();
		while (treeLevel > 0) {
			List<QualifiedFiveStarNetTreeNode> thisTreeLevelList = qualifiedFiveStarNetTreeNodeRepository.getTreeNodesByLevel(treeLevel);
			// loop for the children to calculate OPV at the lowest level
			for (QualifiedFiveStarNetTreeNode qualifiedFiveStarNetTreeNode : thisTreeLevelList) {
                // first
                // Judge whether there are people in the map who have common superiors or not.
                // yes:add no: superposition
			    long uplinkId = qualifiedFiveStarNetTreeNode.getUplinkId();
                Float passUpGpv = qualifiedFiveStarNetTreeNode.getPassUpGpv();
                int qualifiedLine = qualifiedFiveStarNetTreeNode.getQualifiedLine();
                long id = qualifiedFiveStarNetTreeNode.getId();
                List<QualifiedFiveStarNetTreeNode> childs = qualifiedFiveStarNetTreeNodeRepository.getChildNodesByUpid(id);
                int EmeraldLine = 0;
                int GoldDiamondLine = 0;
                if (childs.size()>0){
                    qualifiedFiveStarNetTreeNode.setHasChild(true);
                    // 记录下级翡翠和金钻的个数
                    for (QualifiedFiveStarNetTreeNode qualifiedFiveChildNode : childs){
                        String pin = qualifiedFiveChildNode.getData().getPin();
                        if (Pin.codeOf(pin).getCode() >= Pin.codeOf(PinPosition.EMERALD).getCode()){
                            EmeraldLine+=1;
                        }
                        if (Pin.codeOf(pin).getCode() >= Pin.codeOf(PinPosition.GOLD_DIAMOND).getCode()){
                            GoldDiamondLine+= 1;
                        }
                    }
                }else {
                    qualifiedFiveStarNetTreeNode.setHasChild(false);
                }
                qualifiedFiveStarNetTreeNode.setEmeraldLine(EmeraldLine);
                qualifiedFiveStarNetTreeNode.setGoldDiamondLine(GoldDiamondLine);

                // Go to map to get all the people with common superiors
                List<PassUpGpv> downLinePassUpGpvs = downLines.get(uplinkId);
                List<PassUpGpv> needSortDownLinePassUpGpvs = new ArrayList<>();
                PassUpGpv passUpGpvVo = new PassUpGpv();
                passUpGpvVo.setId(id);
                passUpGpvVo.setPassUpGpv(qualifiedFiveStarNetTreeNode.getPassUpGpv());
                if (downLinePassUpGpvs != null){
                    // Loop to get all subordinate levels
                    for (PassUpGpv brotherPassUpGpv : downLinePassUpGpvs){
                        needSortDownLinePassUpGpvs.add(brotherPassUpGpv);
                    }
                }
                needSortDownLinePassUpGpvs.add(passUpGpvVo);
                // Sort the subordinates
                List<PassUpGpv> sortedDownLinePassUpGpvs = SortFiveStarIntegral(needSortDownLinePassUpGpvs);
                // Store subordinate ID after sorting and sorting.
                downLines.put(uplinkId,sortedDownLinePassUpGpvs);

                // second
                // The five-star integral of the current node
                Float fiveStarIntegral = qualifiedFiveStarNetTreeNode.getPassUpGpv();
                // Gets all the direct subordinate of the node
                List<PassUpGpv> passUpGpvs = downLines.get(id);
                long accountId = qualifiedFiveStarNetTreeNode.getData().getId();
                if (passUpGpvs != null){
                    Iterator<PassUpGpv> passUpGpvIterator = passUpGpvs.iterator();
                    List<QualifiedFiveStarNetTreeNode> nodes = new ArrayList<>();
                    // 如果合格则不需要借分
                    if (fiveStarIntegral >= PinPoints.COMMON_QUALIFY_POINTS){
                        String pin = changeAndGetPin(fiveStarIntegral, passUpGpv, qualifiedLine, accountId);
                        qualifiedFiveStarNetTreeNode.setFiveStarIntegral(fiveStarIntegral);
                        qualifiedFiveStarNetTreeNode.setPin(pin);
                        qualifiedFiveStarNetTreeNodeRepository.saveAndFlush(qualifiedFiveStarNetTreeNode);
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
                                downLineNode.setBorrowTo(qualifiedFiveStarNetTreeNode.getId());
                                downLineNode.setBorrowedPoints(needBorrowPoints);
                                Boolean isAchieve = downlineFiveStarIntegral > needBorrowPoints;
                                Float downLineNodeFiveStarIntegral = isAchieve ? downlineFiveStarIntegral - needBorrowPoints : 0F;
                                downLineNode.setFiveStarIntegral(downLineNodeFiveStarIntegral);
                                fiveStarIntegral+=(isAchieve ? needBorrowPoints : downlineFiveStarIntegral);
                                Float borrowedPoints = qualifiedFiveStarNetTreeNode.getBorrowedPoints()!= null ? qualifiedFiveStarNetTreeNode.getBorrowedPoints() : 0F;
                                qualifiedFiveStarNetTreeNode.setBorrowPoints(borrowedPoints+(isAchieve ? needBorrowPoints : downlineFiveStarIntegral));
                                qualifiedFiveStarNetTreeNode.setFiveStarIntegral(fiveStarIntegral);

                                if (isAchieve){
                                    if (downLineNodeFiveStarIntegral >= PinPoints.COMMON_QUALIFY_POINTS){
                                        // Qualified after borrowing points,Computing grade
                                        String pin = changeAndGetPin(fiveStarIntegral, passUpGpv, qualifiedLine, accountId);
                                        qualifiedFiveStarNetTreeNode.setPin(pin);
                                    }else {
                                        // 如果借分后仍不合格，则合格线减一
                                        // 红宝石职级不受影响
                                        qualifiedLine = qualifiedLine == 0 ? 0 : qualifiedLine-1;
                                        String pin;
                                        if (beforeBorrowPin == PinPosition.RUBY){
                                            Account account = accountRepository.getAccountById(accountId);
                                            pin = PinPosition.RUBY;
                                            account.setPin(PinPosition.RUBY);
                                            accountRepository.saveAndFlush(account);
                                        }else {
                                            pin = changeAndGetPin(fiveStarIntegral,passUpGpv,qualifiedLine,accountId);
                                        }
                                        qualifiedFiveStarNetTreeNode.setQualifiedLine(qualifiedLine);
                                        qualifiedFiveStarNetTreeNode.setPin(pin);
                                    }
                                }else {
                                    qualifiedLine = qualifiedLine == 0 ? 0 : qualifiedLine-1;
                                    qualifiedFiveStarNetTreeNode.setQualifiedLine(qualifiedLine);
                                    passUpGpvIterator.remove();
                                }
                                nodes.add(downLineNode);
                                nodes.add(qualifiedFiveStarNetTreeNode);
                                qualifiedFiveStarNetTreeNodeRepository.saveAll(nodes);
                                qualifiedFiveStarNetTreeNodeRepository.flush();
                            }else {
                                break;
                            }
                        }
                    }
                }else {
                    // A node that has no subordinates or does not need to borrow points,Direct calculation of five star generation integral
                    String pin = changeAndGetPin(fiveStarIntegral, passUpGpv, qualifiedLine, accountId);
                    qualifiedFiveStarNetTreeNode.setFiveStarIntegral(fiveStarIntegral);
                    qualifiedFiveStarNetTreeNode.setPin(pin);
                    qualifiedFiveStarNetTreeNodeRepository.saveAndFlush(qualifiedFiveStarNetTreeNode);
                }
			} // end for loop
			treeLevel--;
		}
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

	private String changeAndGetPin(Float currentFiveStarIntegral,Float passUpGpv,int qualifiedLine,Long id){
        if (passUpGpv == null){
            logger.error("passUpGpv为空");
            return null;
        }
        Account account = accountRepository.getAccountById(id);
        if ((qualifiedLine >= 8) || (qualifiedLine >= 7 && currentFiveStarIntegral >= PinPoints.COMMON_QUALIFY_POINTS)){
            savePinAndHistory(account,PinPosition.GOLD_DIAMOND);
            return PinPosition.GOLD_DIAMOND;
        }
        if ((qualifiedLine >= 6) || (qualifiedLine >= 5 && currentFiveStarIntegral >= PinPoints.COMMON_QUALIFY_POINTS)){
            savePinAndHistory(account,PinPosition.DIAMOND);
            return PinPosition.DIAMOND;
        }
        if ((qualifiedLine >= 4) || (qualifiedLine >= 3 && currentFiveStarIntegral >= PinPoints.COMMON_QUALIFY_POINTS)){
            savePinAndHistory(account,PinPosition.EMERALD);
            return PinPosition.EMERALD;
        }
        if ((qualifiedLine >= 2) || (qualifiedLine >= 1 && passUpGpv >= PinPoints.RUBY_QUALIFY_POINTS)){
            savePinAndHistory(account,PinPosition.RUBY);
            return PinPosition.RUBY;
        }
        if (passUpGpv >= PinPoints.RUBY_QUALIFY_POINTS){
            savePinAndHistory(account,PinPosition.QUALIFIED_FIVE_STAR);
            return PinPosition.QUALIFIED_FIVE_STAR;
        }
        return null;
    }

    private void savePinAndHistory(Account account,String pin){
        account.setPin(pin);
        String maxPin = account.getMaxPin();
        if (Pin.codeOf(pin).getCode() > Pin.codeOf(maxPin).getCode()){
            AccountPinHistory accountPinHistory = new AccountPinHistory();
            accountPinHistory.setPromotionDate(new Date());
            accountPinHistory.setAccount(account);
            accountPinHistory.setCreatedBy("TerryTang");
            accountPinHistory.setLastUpdatedBy("TerryTang");
            accountPinHistory.setPin(pin);
            accountPinHistoryRepository.save(accountPinHistory);
        }
        accountRepository.save(account);
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
        List<QualifiedFiveStarNetTreeNode> qualifiedFiveStarNetTreeNodes = getTreeNodeRepository().getTreeNodesBySnapshotDate(snapshotDate);
        if (qualifiedFiveStarNetTreeNodes.size() > 0) {
            for (QualifiedFiveStarNetTreeNode qualifiedFiveStarNetTreeNode : qualifiedFiveStarNetTreeNodes) {
                if (qualifiedFiveStarNetTreeNode.getUplinkId() == 0) continue;
                QualifiedFiveStarVo qualifiedFiveStarVo = new QualifiedFiveStarVo();
                FiveStarNetTreeNode fiveStarAccountNum = fiveStarNetTreeNodeRepository.findByAccountNum(snapshotDate, qualifiedFiveStarNetTreeNode.getData().getAccountNum());
                qualifiedFiveStarVo.setLevelNum(qualifiedFiveStarNetTreeNode.getLevelNum());
                qualifiedFiveStarVo.setName(qualifiedFiveStarNetTreeNode.getData().getName());
                qualifiedFiveStarVo.setAccountNum(qualifiedFiveStarNetTreeNode.getData().getAccountNum());
                qualifiedFiveStarVo.setPpv(fiveStarAccountNum.getPpv());
                qualifiedFiveStarVo.setGpv(fiveStarAccountNum.getGpv());
                qualifiedFiveStarVo.setOpv(fiveStarAccountNum.getOpv());
                qualifiedFiveStarVo.setPassUpGpv(qualifiedFiveStarNetTreeNode.getPassUpGpv());
                qualifiedFiveStarVo.setBorrowPoints(qualifiedFiveStarNetTreeNode.getBorrowPoints());
                qualifiedFiveStarVo.setBorrowedPoints(qualifiedFiveStarNetTreeNode.getBorrowedPoints());
                qualifiedFiveStarVo.setFiveStarIntegral(qualifiedFiveStarNetTreeNode.getFiveStarIntegral());
                int qualifiedLine = qualifiedFiveStarNetTreeNode.getQualifiedLine();
                qualifiedFiveStarVo.setQualifiedLine(qualifiedLine < 0 ? 0 : qualifiedLine);
                String pin = qualifiedFiveStarNetTreeNode.getPin();
                qualifiedFiveStarVo.setPin(Pin.codeOf(pin).getCode());
                // todo 获取每人的历史最高PIN
                qualifiedFiveStarVo.setMaxPin(Pin.codeOf(pin).getCode());
                qualifiedFiveStarVos.add(qualifiedFiveStarVo);
            }
        }
        return qualifiedFiveStarVos;
    }
}
