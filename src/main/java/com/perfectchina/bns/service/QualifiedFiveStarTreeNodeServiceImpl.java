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
        int maxLevelNum = qualifiedFiveStarNetTreeNodeRepository.getMaxLevelNum(snapShotDate);
        return maxLevelNum;
	}

	private Map<Long,Long> relation = new HashMap<>();

	/**
	 * param node is SimpleNetTreeNode walking through
	 */
	protected void process(TreeNode node) {
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
			QualifiedFiveStarNetTreeNode qualifiedFiveStarUplink = getTreeNodeRepository().getAccountByAccountNum(passUpGpvNetTreeNode.getSnapshotDate(),
					accountNum);
            long qualifiedFiveStarUplinkId = qualifiedFiveStarUplink.getId();
            String uplinkLevelLine = qualifiedFiveStarUplink.getLevelLine();
            qualifiedFiveStarNetTreeNode.setUplinkId(qualifiedFiveStarUplinkId);
			// To judge if it is a qualified five-star
			if (passUpGpv >= 18000F){
                setuplinkLevelLineAndLevel(uplinkLevelLine,qualifiedFiveStarNetTreeNode,qualifiedFiveStarUplinkId);
				copyNetTree(passUpGpvNetTreeNode,qualifiedFiveStarNetTreeNode);
			}else{
			    // To judge if it is a ruby
				if ((passUpGpv >= 9000 && qualifiedLine >= 1) || (qualifiedLine >= 2)){
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
		// Get the highest level of a tree
		int treeLevel = getMaxTreeLevel(snapShotDate);
		if (treeLevel < 0)
			return;

		Map<Long,List<PassUpGpv>> downLines = new HashMap<>();
		while (treeLevel >= 0) {
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
                if (childs.size()>0){
                    qualifiedFiveStarNetTreeNode.setHasChild(true);
                }else {
                    qualifiedFiveStarNetTreeNode.setHasChild(false);
                }

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
                    // If there are child nodes, there is no need to borrow them
                    if (fiveStarIntegral >= PinPoints.COMMON_QUALIFY_POINTS){
                        String pin = changeAndGetPin(fiveStarIntegral, passUpGpv, qualifiedLine, accountId);
                        qualifiedFiveStarNetTreeNode.setFiveStarIntegral(fiveStarIntegral);
                        qualifiedFiveStarNetTreeNode.setPin(pin);
                        qualifiedFiveStarNetTreeNodeRepository.saveAndFlush(qualifiedFiveStarNetTreeNode);
                    }else {
                        // Start the loan
                        // Judging rank before borrowing points
                        String beforeBorrowPin = getPin(fiveStarIntegral, passUpGpv, qualifiedLine);
                        while (!(fiveStarIntegral >= PinPoints.COMMON_QUALIFY_POINTS)){
                            if (passUpGpvIterator.hasNext()) {
                                PassUpGpv maxPassUpGpv = passUpGpvIterator.next();
                                // How many points do you need to get the node
                                Float needBorrowPoints = PinPoints.COMMON_QUALIFY_POINTS-fiveStarIntegral;
                                // Gets the highest pass-up-gpv child node of the node
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
                                // If not, a qualified five-star line will be deleted
                                if (isAchieve){
                                    if (downLineNodeFiveStarIntegral >= PinPoints.COMMON_QUALIFY_POINTS){
                                        // Qualified after borrowing points,Computing grade
                                        String pin = changeAndGetPin(fiveStarIntegral, passUpGpv, qualifiedLine, accountId);
                                        qualifiedFiveStarNetTreeNode.setPin(pin);
                                    }else {
                                        // Unqualified after borrowing points,Computing grade
                                        // No influence on Ruby rank
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
