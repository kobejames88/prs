package com.perfectchina.bns.service;

import com.perfectchina.bns.model.Account;
import com.perfectchina.bns.model.PassUpGpv;
import com.perfectchina.bns.model.treenode.FiveStarNetTreeNode;
import com.perfectchina.bns.model.treenode.PassUpGpvNetTreeNode;
import com.perfectchina.bns.model.treenode.QualifiedFiveStarNetTreeNode;
import com.perfectchina.bns.model.treenode.TreeNode;
import com.perfectchina.bns.repositories.*;
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
		updateChildTreeLevel( 0, rootNode );
	}

	@Override
	public void updateChildTreeLevel(Integer fromLevelNum, TreeNode fromNode) {
		super.updateChildTreeLevel(fromLevelNum, fromNode);
	}

	public int getMaxTreeLevel(String snapShotDate) {
        int maxLevelNum = passUpGpvNetTreeNodeRepository.getMaxLevelNum(snapShotDate);
        return maxLevelNum;
	}

	private Map<Long,Long> relation = new HashMap<>();

	/**
	 * param node is SimpleNetTreeNode walking through
	 */
	protected void process(TreeNode node) {
		logger.debug("process, update node=" + node.getData().getAccountNum() + "/" + node.getData().getName()
				+ ", level [" + node.getLevelNum() + "].");
		// 当前元素
		PassUpGpvNetTreeNode passUpGpvNetTreeNode = (PassUpGpvNetTreeNode) node;
		// 待装载元素
		QualifiedFiveStarNetTreeNode qualifiedFiveStarNetTreeNode = new QualifiedFiveStarNetTreeNode();

		Float passUpGpv = passUpGpvNetTreeNode.getPassUpGpv();
		int qualifiedLine = passUpGpvNetTreeNode.getQualifiedLine();
        long id = passUpGpvNetTreeNode.getId();
        Long mapPassUpGpvUplinkId = relation.get(id);

        long passUpGpvUplinkId = mapPassUpGpvUplinkId != null ? mapPassUpGpvUplinkId : passUpGpvNetTreeNode.getUplinkId();

		if(passUpGpvUplinkId!=0){
			PassUpGpvNetTreeNode passUpGpvUplink = passUpGpvNetTreeNodeRepository.getOne(passUpGpvUplinkId);
			String accountNum = passUpGpvUplink.getData().getAccountNum();
			QualifiedFiveStarNetTreeNode qualifiedFiveStarUplink = getTreeNodeRepository().getAccountByAccountNum(passUpGpvNetTreeNode.getSnapshotDate(),
					accountNum);
            long qualifiedFiveStarUplinkId = qualifiedFiveStarUplink.getId();
            String uplinkLevelLine = qualifiedFiveStarUplink.getLevelLine();
            qualifiedFiveStarNetTreeNode.setUplinkId(qualifiedFiveStarUplinkId);
			// 判断是否为合格五星
			if (passUpGpv >= 18000F){
                setuplinkLevelLineAndLevel(uplinkLevelLine,qualifiedFiveStarNetTreeNode,qualifiedFiveStarUplinkId);
				copyNetTree(passUpGpvNetTreeNode,qualifiedFiveStarNetTreeNode);
			}else{
			    // 判断是否为红宝石
				if ((passUpGpv >= 9000 && qualifiedLine >= 1) || (qualifiedLine >= 2)){
                    setuplinkLevelLineAndLevel(uplinkLevelLine,qualifiedFiveStarNetTreeNode,qualifiedFiveStarUplinkId);
                    copyNetTree(passUpGpvNetTreeNode,qualifiedFiveStarNetTreeNode);
				}else {
					// 过滤此元素
                    // 获取此元素的直接下级
					List<TreeNode> childNodes = passUpGpvNetTreeNodeRepository.getChildNodesByUpid(id);
					// 将此元素的合格线给上级
                    int passUpGpvQualifiedLine = passUpGpvNetTreeNode.getQualifiedLine();
                    if (passUpGpvQualifiedLine>0){
                        qualifiedFiveStarUplink.setQualifiedLine(qualifiedFiveStarUplink.getQualifiedLine()+passUpGpvNetTreeNode.getQualifiedLine());
                    }
                    // 将此元素的直接下级id与上级id放入map中
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
		qualifiedFiveStarNetTreeNode.setAsteriskNodePoints(passUpGpvNetTreeNode.getAsteriskNodePoints());
		qualifiedFiveStarNetTreeNode.setHasAsteriskNode(passUpGpvNetTreeNode.getHasAsteriskNode());
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
		// 获取树的最高层级
		int treeLevel = getMaxTreeLevel(snapShotDate);
		if (treeLevel < 0)
			return;

		Map<Long,List<PassUpGpv>> downLines = new HashMap<>();
		while (treeLevel >= 0) {
			List<QualifiedFiveStarNetTreeNode> thisTreeLevelList = qualifiedFiveStarNetTreeNodeRepository.getTreeNodesByLevel(treeLevel);
			// loop for the children to calculate OPV at the lowest level
			for (QualifiedFiveStarNetTreeNode qualifiedFiveStarNetTreeNode : thisTreeLevelList) {
                // first 判断map里是否有有共同上级的人，没有就新增，有就叠加
			    long uplinkId = qualifiedFiveStarNetTreeNode.getUplinkId();
                Float passUpGpv = qualifiedFiveStarNetTreeNode.getPassUpGpv();
                int qualifiedLine = qualifiedFiveStarNetTreeNode.getQualifiedLine();
                long id = qualifiedFiveStarNetTreeNode.getId();
                List<TreeNode> childs = qualifiedFiveStarNetTreeNodeRepository.getChildNodesByUpid(id);
                if (childs.size()>0){
                    qualifiedFiveStarNetTreeNode.setHasChild(true);
                }else {
                    qualifiedFiveStarNetTreeNode.setHasChild(false);
                }

                // 去map里获取所有有共同上级的人
                List<PassUpGpv> downLinePassUpGpvs = downLines.get(uplinkId);
                List<PassUpGpv> needSortDownLinePassUpGpvs = new ArrayList<>();
                PassUpGpv passUpGpvVo = new PassUpGpv();
                passUpGpvVo.setId(id);
                passUpGpvVo.setPassUpGpv(qualifiedFiveStarNetTreeNode.getPassUpGpv());
                if (downLinePassUpGpvs != null){
                    // 循环获取所有下级
                    for (PassUpGpv brotherPassUpGpv : downLinePassUpGpvs){
                        needSortDownLinePassUpGpvs.add(brotherPassUpGpv);
                    }
                }
                needSortDownLinePassUpGpvs.add(passUpGpvVo);
                // 对下级进行排序
                List<PassUpGpv> sortedDownLinePassUpGpvs = SortFiveStarIntegral(needSortDownLinePassUpGpvs);
                // 存放上级id和排序后的所有下级
                downLines.put(uplinkId,sortedDownLinePassUpGpvs);

                // second
                // 当前节点的五星代积分
                Float fiveStarIntegral = qualifiedFiveStarNetTreeNode.getPassUpGpv();
                // 获取该节点的所有直接下级
                List<PassUpGpv> passUpGpvs = downLines.get(id);
                long accountId = qualifiedFiveStarNetTreeNode.getData().getId();
                if (passUpGpvs != null){
                    Iterator<PassUpGpv> passUpGpvIterator = passUpGpvs.iterator();
                    List<QualifiedFiveStarNetTreeNode> nodes = new ArrayList<>();
//                    int qualifiedFiveStarLine = passUpGpvs.size();
                    // 如果有子节点，但是不需要借分
                    if (fiveStarIntegral >= PinPoints.COMMON_QUALIFY_POINTS){
                        String pin = changeAndGetPin(fiveStarIntegral, passUpGpv, qualifiedLine, accountId);
                        qualifiedFiveStarNetTreeNode.setFiveStarIntegral(fiveStarIntegral);
                        qualifiedFiveStarNetTreeNode.setPin(pin);
                        qualifiedFiveStarNetTreeNodeRepository.saveAndFlush(qualifiedFiveStarNetTreeNode);
                    }else {
                        // 开始借分
                        // 借分前先判断职级
                        String beforeBorrowPin = getPin(fiveStarIntegral, passUpGpv, qualifiedLine);
                        while (!(fiveStarIntegral >= PinPoints.COMMON_QUALIFY_POINTS)){
                            if (passUpGpvIterator.hasNext()) {
                                PassUpGpv maxPassUpGpv = passUpGpvIterator.next();
                                // 获取该节点需要借多少分
                                Float needBorrowPoints = PinPoints.COMMON_QUALIFY_POINTS-fiveStarIntegral;
                                // 获取该节点的最高pass-up-gpv子节点
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
                                // 如果未达到则删除一条合格五星线
                                if (isAchieve){
                                    if (downLineNodeFiveStarIntegral >= PinPoints.COMMON_QUALIFY_POINTS){
                                        // 借分后合格，计算职级
                                        String pin = changeAndGetPin(fiveStarIntegral, passUpGpv, qualifiedLine, accountId);
                                        qualifiedFiveStarNetTreeNode.setPin(pin);
                                    }else {
                                        // 借分后不合格，判断职级
                                        // 对红宝石职级没影响
                                        qualifiedLine-=1;
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
                                    qualifiedLine-=1;
                                    qualifiedFiveStarNetTreeNode.setQualifiedLine(qualifiedLine);
                                    passUpGpvIterator.remove();
                                }
                                nodes.add(downLineNode);
                                nodes.add(qualifiedFiveStarNetTreeNode);
                                qualifiedFiveStarNetTreeNodeRepository.save(nodes);
                                qualifiedFiveStarNetTreeNodeRepository.flush();
                            }else {
                                break;
                            }
                        }
                    }
                }else {
                    // 没有下级或者不需要借分的节点，直接计算五星代积分
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
        String pin;
        if ((passUpGpv >= PinPoints.RUBY_QUALIFY_POINTS && qualifiedLine >= 1)||(qualifiedLine >= 2)){
            if ((qualifiedLine >= 3 && currentFiveStarIntegral >= PinPoints.COMMON_QUALIFY_POINTS)||(qualifiedLine >= 4)){
                if ((qualifiedLine >= 5 && currentFiveStarIntegral >= PinPoints.COMMON_QUALIFY_POINTS)||(qualifiedLine >= 6)){
                    if ((qualifiedLine >= 7 && currentFiveStarIntegral >= PinPoints.COMMON_QUALIFY_POINTS)||(qualifiedLine >= 8)){
                        pin = PinPosition.GOLD_DIAMOND;
                    }else {
                        pin = PinPosition.DIAMOND;
                    }
                }else {
                    pin = PinPosition.EMERALD;
                }
            }else {
                pin = PinPosition.RUBY;
            }
        }else {
            pin = PinPosition.QUALIFIED_FIVE_STAR;
        }
        return pin;
    }

	private String changeAndGetPin(Float currentFiveStarIntegral,Float passUpGpv,int qualifiedLine,Long id){
        if (passUpGpv == null){
            logger.error("passUpGpv为空");
            return null;
        }
        Account account = accountRepository.getAccountById(id);
        String pin;
        if ((passUpGpv >= PinPoints.RUBY_QUALIFY_POINTS && qualifiedLine >= 1)||(qualifiedLine >= 2)){
            if ((qualifiedLine >= 3 && currentFiveStarIntegral >= PinPoints.COMMON_QUALIFY_POINTS)||(qualifiedLine >= 4)){
                if ((qualifiedLine >= 5 && currentFiveStarIntegral >= PinPoints.COMMON_QUALIFY_POINTS)||(qualifiedLine >= 6)){
                    if ((qualifiedLine >= 7 && currentFiveStarIntegral >= PinPoints.COMMON_QUALIFY_POINTS)||(qualifiedLine >= 8)){
                        account.setPin(PinPosition.GOLD_DIAMOND);
                        pin = PinPosition.GOLD_DIAMOND;
                    }else {
                        account.setPin(PinPosition.DIAMOND);
                        pin = PinPosition.DIAMOND;
                    }
                }else {
                    account.setPin(PinPosition.EMERALD);
                    pin = PinPosition.EMERALD;
                }
            }else {
                account.setPin(PinPosition.RUBY);
                pin = PinPosition.RUBY;
            }
        }else {
            account.setPin(PinPosition.QUALIFIED_FIVE_STAR);
            pin = PinPosition.QUALIFIED_FIVE_STAR;
        }
        accountRepository.saveAndFlush(account);
        return pin;
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
}
