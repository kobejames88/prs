package com.perfectchina.bns.service;

import com.perfectchina.bns.model.Account;
import com.perfectchina.bns.model.AccountPinHistory;
import com.perfectchina.bns.model.PassUpGpv;
import com.perfectchina.bns.model.treenode.*;
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
    @Autowired
    private OpvNetTreeNodeRepository opvNetTreeNodeRepository;

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
                copyNetTree(passUpGpvNetTreeNode,qualifiedFiveStarNetTreeNode,snapshotDate);
            }else{
                // To judge if it is a ruby
                if ((passUpGpv >= PinPoints.RUBY_QUALIFY_POINTS && qualifiedLine >= 1) || (qualifiedLine >= 2)){
                    setuplinkLevelLineAndLevel(uplinkLevelLine,qualifiedFiveStarNetTreeNode,qualifiedFiveStarUplinkId);
                    copyNetTree(passUpGpvNetTreeNode,qualifiedFiveStarNetTreeNode,snapshotDate);
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
        String accountNum = qualifiedFiveStarNetTreeNode.getData().getAccountNum();
		OpvNetTreeNode opvNetTreeNode = opvNetTreeNodeRepository.findByAccountNum(snapshotDate,accountNum);
        Float asteriskNodePoints = passUpGpvNetTreeNode.getAsteriskNodePoints();
        Boolean hasAsteriskNode = passUpGpvNetTreeNode.getHasAsteriskNode();
        qualifiedFiveStarNetTreeNode.setAsteriskNodePoints(asteriskNodePoints == null ? 0F : asteriskNodePoints);
		qualifiedFiveStarNetTreeNode.setHasAsteriskNode(hasAsteriskNode == null ? false : hasAsteriskNode);
        qualifiedFiveStarNetTreeNode.setPassUpGpv(passUpGpvNetTreeNode.getPassUpGpv());
        qualifiedFiveStarNetTreeNode.setGpv(passUpGpvNetTreeNode.getGpv());
		qualifiedFiveStarNetTreeNode.setQualifiedLine(passUpGpvNetTreeNode.getQualifiedLine());
        qualifiedFiveStarNetTreeNode.setGoldDiamondLine(0);
        qualifiedFiveStarNetTreeNode.setEmeraldLine(0);
		qualifiedFiveStarNetTreeNode.setSnapshotDate(passUpGpvNetTreeNode.getSnapshotDate());
        qualifiedFiveStarNetTreeNode.setAboveEmeraldNodeSign(false);
        qualifiedFiveStarNetTreeNode.setOpv(opvNetTreeNode.getOpv());
		qualifiedFiveStarNetTreeNode.setData(passUpGpvNetTreeNode.getData());
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
//                int GoldDiamondLine = 0;
                if (childs.size()>0){
                    qualifiedFiveStarNetTreeNode.setHasChild(true);
                    for (QualifiedFiveStarNetTreeNode qualifiedFiveChildNode : childs){
                        String pin = qualifiedFiveChildNode.getData().getPin();
                        // 爬树获取下级翡翠线
                        if (Pin.valueOf(pin).getCode() >= Pin.valueOf(PinPosition.EMERALD).getCode()){
                            EmeraldLine+=1;
                        }
                    }
                }else {
                    qualifiedFiveStarNetTreeNode.setHasChild(false);
                }
                qualifiedFiveStarNetTreeNode.setEmeraldLine(EmeraldLine);

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
                String pin;
                if (passUpGpvs != null){
                    Iterator<PassUpGpv> passUpGpvIterator = passUpGpvs.iterator();
                    List<QualifiedFiveStarNetTreeNode> nodes = new ArrayList<>();
                    // 如果合格则不需要借分
                    if (fiveStarIntegral >= PinPoints.COMMON_QUALIFY_POINTS){
                        pin = changeAndGetPin(fiveStarIntegral, passUpGpv, qualifiedLine, accountId);
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
                                        pin = changeAndGetPin(fiveStarIntegral, passUpGpv, qualifiedLine, accountId);
                                        getGoldDiamondLine(id,uplinkId,qualifiedFiveStarNetTreeNode,pin);
                                    }else {
                                        // 如果借分后仍不合格，则合格线减一
                                        // 红宝石职级不受影响
                                        qualifiedLine = qualifiedLine == 0 ? 0 : qualifiedLine-1;
                                        if (beforeBorrowPin == PinPosition.RUBY){
                                            Account account = accountRepository.getAccountById(accountId);
                                            savePinAndHistory(account,PinPosition.RUBY);
                                        }else {
                                            pin = changeAndGetPin(fiveStarIntegral,passUpGpv,qualifiedLine,accountId);
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
                    pin = changeAndGetPin(fiveStarIntegral, passUpGpv, qualifiedLine, accountId);
                    getGoldDiamondLine(id,uplinkId,qualifiedFiveStarNetTreeNode,pin);
                    qualifiedFiveStarNetTreeNode.setFiveStarIntegral(fiveStarIntegral);
                    qualifiedFiveStarNetTreeNodeRepository.save(qualifiedFiveStarNetTreeNode);
                }
			} // end for loop
			treeLevel--;
		}
	}

	private void getGoldDiamondLine(long id,long uplinkId,QualifiedFiveStarNetTreeNode qualifiedFiveStarNetTreeNode,String pin){
        Integer gLine = goldDiamondLine.get(id);
        if (pin == PinPosition.GOLD_DIAMOND){
            // 如果当前元素为金钻
            if (gLine != null){
                Integer uplinkGLine = goldDiamondLine.get(uplinkId);
                if (uplinkGLine != null){
                    goldDiamondLine.put(uplinkId,uplinkGLine+1);
                }else {
                    goldDiamondLine.put(uplinkId,1);
                }
                qualifiedFiveStarNetTreeNode.setGoldDiamondLine(gLine);
            }
        }else {
            // 如果当前元素不为金钻
            if (gLine != null){
                if (Pin.valueOf(pin).getCode() >= Pin.valueOf(PinPosition.EMERALD).getCode()){
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
        accountRepository.save(account);
        String maxPin = account.getMaxPin();
        Integer max = Pin.valueOf(pin).getCode();
        Integer temp = Pin.valueOf(maxPin).getCode();
        if (max > temp){
            while (max > temp){
                temp+=1;
                String temp_pin = Pin.codeOf(temp).getValue();
                AccountPinHistory accountPinHistory = new AccountPinHistory();
                accountPinHistory.setPromotionDate(new Date());
                accountPinHistory.setAccount(account);
                accountPinHistory.setCreatedBy("TerryTang");
                accountPinHistory.setLastUpdatedBy("TerryTang");
                accountPinHistory.setPin(temp_pin);
                accountPinHistoryRepository.save(accountPinHistory);
            }
        }
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
                qualifiedFiveStarVo.setPin(Pin.valueOf(pin).getCode());
                // todo 获取每人的历史最高PIN
                qualifiedFiveStarVo.setMaxPin(Pin.valueOf(pin).getCode());
                qualifiedFiveStarVos.add(qualifiedFiveStarVo);
            }
        }
        return qualifiedFiveStarVos;
    }
}
