package com.perfectchina.bns.service;

import com.perfectchina.bns.model.Account;
import com.perfectchina.bns.model.PassUpGpv;
import com.perfectchina.bns.model.treenode.FiveStarNetTreeNode;
import com.perfectchina.bns.model.treenode.PassUpGpvNetTreeNode;
import com.perfectchina.bns.model.treenode.QualifiedFiveStarNetTreeNode;
import com.perfectchina.bns.model.treenode.TreeNode;
import com.perfectchina.bns.repositories.*;
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
	public PassUpGpvNetTreeNodeRepository getTreeNodeRepository() {
		return passUpGpvNetTreeNodeRepository;
	}

	public boolean isReadyToUpdate() {
		// need to check if Simple Net already exist, otherwise, cannot
		// calculate
		boolean isReady = false;
		// // Current month
		String snapshotDate = null;
		try {
			snapshotDate = sdf.format(getPreviousDateEndTime());
			PassUpGpvNetTreeNode rootNode = getTreeNodeRepository().getRootTreeNodeOfMonth(snapshotDate);

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
		TreeNode rootNode = getTreeNodeRepository().getRootTreeNode(snapshotDate);
		updateChildTreeLevel( 0, rootNode );
	}

	@Override
	public void updateChildTreeLevel(Integer fromLevelNum, TreeNode fromNode) {
		super.updateChildTreeLevel(fromLevelNum, fromNode);
	}

//	public int getMaxTreeLevel(String snapShotDate) {
//        int maxLevelNum = getTreeNodeRepository().getMaxLevelNum(snapShotDate);
//        return maxLevelNum;
//	}

	/**
	 * param node is SimpleNetTreeNode walking through
	 */
	protected void process(TreeNode node) {
		logger.debug("process, update node=" + node.getData().getAccountNum() + "/" + node.getData().getName()
				+ ", level [" + node.getLevelNum() + "].");
		// Copy the node of the original network map plus the uplinkId to GpvNetTreeNode
		// 当前元素
		PassUpGpvNetTreeNode passUpGpvNetTreeNode = (PassUpGpvNetTreeNode) node;
		// 待装载元素
		QualifiedFiveStarNetTreeNode qualifiedFiveStarNetTreeNode = new QualifiedFiveStarNetTreeNode();
		Float passUpGpv = passUpGpvNetTreeNode.getPassUpGpv();
		int qualifiedLine = passUpGpvNetTreeNode.getQualifiedLine();
		//the uplinkId is SimpleNet
		long uplinkId = passUpGpvNetTreeNode.getUplinkId();
		if(uplinkId!=0){
			PassUpGpvNetTreeNode one = getTreeNodeRepository().getOne(uplinkId);
			String accountNum = one.getData().getAccountNum();
			QualifiedFiveStarNetTreeNode one2 = qualifiedFiveStarNetTreeNodeRepository.getAccountByAccountNum(passUpGpvNetTreeNode.getSnapshotDate(),
					accountNum);
			qualifiedFiveStarNetTreeNode.setUplinkId(one2.getId());
			// 判断是否为合格五星
			if (passUpGpv >= 18000F){
				copyNetTree(passUpGpvNetTreeNode,qualifiedFiveStarNetTreeNode);
			}else{
			    // 判断是否为红宝石
				if ((passUpGpv >= 9000 && qualifiedLine >= 1) || (qualifiedLine >= 2)){
					copyNetTree(passUpGpvNetTreeNode,qualifiedFiveStarNetTreeNode);
				}else {
					// 过滤此元素
                    // 获取此元素的直接下级
					List<TreeNode> childNodes = passUpGpvNetTreeNodeRepository.getChildNodesByUpid(passUpGpvNetTreeNode.getId());
					// 将此元素的合格线给上级
					QualifiedFiveStarNetTreeNode uplink = qualifiedFiveStarNetTreeNodeRepository.getOne(one2.getId());
					uplink.setQualifiedLine(uplink.getQualifiedLine()+passUpGpvNetTreeNode.getQualifiedLine());
                    // 将此元素的直接下级并到上级
					for (TreeNode childNode : childNodes){
						PassUpGpvNetTreeNode passUpGpvNetTreeChildNode = (PassUpGpvNetTreeNode)childNode;
						passUpGpvNetTreeChildNode.setUplinkId(uplinkId);
						passUpGpvNetTreeChildNode.setLevelNum(childNode.getLevelNum()-1);
						passUpGpvNetTreeNodeRepository.saveAndFlush(passUpGpvNetTreeChildNode);
					}
					qualifiedFiveStarNetTreeNodeRepository.saveAndFlush(one2);
				}
			}
		}else {
			copyNetTree(passUpGpvNetTreeNode,qualifiedFiveStarNetTreeNode);
		}
	}

	private void copyNetTree(PassUpGpvNetTreeNode passUpGpvNetTreeNode,QualifiedFiveStarNetTreeNode qualifiedFiveStarNetTreeNode){
		qualifiedFiveStarNetTreeNode.setHasChild(passUpGpvNetTreeNode.getHasChild());
		qualifiedFiveStarNetTreeNode.setLevelNum(passUpGpvNetTreeNode.getLevelNum());
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
		// Get the level of the original tree
		int treeLevel = qualifiedFiveStarNetTreeNodeRepository.getMaxLevelNum(snapShotDate);
		if (treeLevel < 0)
			return;
		Map<Long,List<PassUpGpv>> downLines = new HashMap<>();
		while (treeLevel >= 0) {
			List<QualifiedFiveStarNetTreeNode> thisTreeLevelTreeList = qualifiedFiveStarNetTreeNodeRepository.getTreeNodesByLevel(treeLevel);
			// loop for the children to calculate OPV at the lowest level
			for (QualifiedFiveStarNetTreeNode qualifiedFiveStarNetTreeNode : thisTreeLevelTreeList) {
                // first 判断map里是否有有共同上级的人，没有就新增，有就叠加
			    long uplinkId = qualifiedFiveStarNetTreeNode.getUplinkId();
                Float passUpGpv = qualifiedFiveStarNetTreeNode.getPassUpGpv();
                Float gpv = qualifiedFiveStarNetTreeNode.getGpv();
                int qualifiedLine = qualifiedFiveStarNetTreeNode.getQualifiedLine();

                // 去map里获取所有有共同上级的人
                List<PassUpGpv> downLinePassUpGpvs = downLines.get(uplinkId);
                List<PassUpGpv> needSortDownLinePassUpGpvs = new ArrayList<>();
                PassUpGpv passUpGpvVo = new PassUpGpv();
                passUpGpvVo.setId(qualifiedFiveStarNetTreeNode.getId());
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
                qualifiedFiveStarNetTreeNode.setFiveStarIntegral(qualifiedFiveStarNetTreeNode.getPassUpGpv());
                // 获取当前节点的五星代积分
                Float fiveStarIntegral = qualifiedFiveStarNetTreeNode.getFiveStarIntegral();
                // 获取该节点的所有直接下级
                List<PassUpGpv> passUpGpvs = downLines.get(qualifiedFiveStarNetTreeNode.getId());
                if (passUpGpvs != null){
                    Iterator<PassUpGpv> passUpGpvIterator = passUpGpvs.iterator();
                    List<QualifiedFiveStarNetTreeNode> nodes = new ArrayList<>();
                    int qualifiedFiveStarLine = passUpGpvs.size();
                    // 如果有子节点，但是不需要借分
                    if (fiveStarIntegral >= 18000F){
                        String pin = changePin(fiveStarIntegral, passUpGpv, qualifiedLine, qualifiedFiveStarLine, qualifiedFiveStarNetTreeNode.getData().getId());
                        setAsteriskNode(fiveStarIntegral,gpv,passUpGpv,pin,qualifiedFiveStarNetTreeNode);
                        qualifiedFiveStarNetTreeNodeRepository.saveAndFlush(qualifiedFiveStarNetTreeNode);
                    }else {
                        // 开始借分
                        while (!(fiveStarIntegral >= 18000F)){
                            if (passUpGpvIterator.hasNext()) {
                                PassUpGpv maxPassUpGpv = passUpGpvIterator.next();
                                // 获取该节点需要借多少分
                                Float needBorrowPoints = 18000F-fiveStarIntegral;
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
                                    if (downLineNodeFiveStarIntegral >= 18000F){
                                        // 借分后合格，计算职级
                                        String pin = changePin(fiveStarIntegral, passUpGpv, qualifiedLine, qualifiedFiveStarLine, qualifiedFiveStarNetTreeNode.getData().getId());
                                        setAsteriskNode(fiveStarIntegral,gpv,passUpGpv,pin,qualifiedFiveStarNetTreeNode);
                                    }else {
                                        //借分后不合格，判断职级
                                        qualifiedFiveStarLine-=1;
                                        String pin = changePin(fiveStarIntegral,passUpGpv,qualifiedLine,qualifiedFiveStarLine,qualifiedFiveStarNetTreeNode.getData().getId());
                                        setAsteriskNode(fiveStarIntegral,gpv,passUpGpv,pin,qualifiedFiveStarNetTreeNode);
                                    }
                                }else {
                                    qualifiedFiveStarLine-=1;
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
                    changePin(fiveStarIntegral, passUpGpv, qualifiedLine, 0, qualifiedFiveStarNetTreeNode.getData().getId());
                    qualifiedFiveStarNetTreeNodeRepository.saveAndFlush(qualifiedFiveStarNetTreeNode);
                }
			} // end for loop
			treeLevel--;
		}
	}

	private String changePin(Float currentFiveStarIntegral,Float passUpGpv,int qualifiedLine,int qualifiedFiveStar,Long id){
        Account account = accountRepository.getAccountById(id);
        String pin = null;
        if ((passUpGpv >= 9000F && qualifiedLine >= 1)||(qualifiedLine >= 2)){
            if ((qualifiedFiveStar >= 3 && currentFiveStarIntegral >= 18000F)||(qualifiedFiveStar >= 4)){
                if ((qualifiedFiveStar >= 5 && currentFiveStarIntegral >= 18000F)||(qualifiedFiveStar >= 6)){
                    if ((qualifiedFiveStar >= 7 && currentFiveStarIntegral >= 18000F)||(qualifiedFiveStar >= 8)){
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

    private void setAsteriskNode(Float currentFiveStarIntegral,Float Gpv,Float PassUpGpv,String pin,QualifiedFiveStarNetTreeNode qualifiedFiveStarNetTreeNode){
	    if (!StringUtils.equals(pin,PinPosition.QUALIFIED_FIVE_STAR)){
	        if (((Gpv>=18000F)&&(PassUpGpv-Gpv>0)&&PassUpGpv>=18000F) || ((Gpv<18000F)&&(PassUpGpv-Gpv>0)&&PassUpGpv>=18000F)){
                qualifiedFiveStarNetTreeNode.setHasAsteriskNode(true);
                qualifiedFiveStarNetTreeNode.setAsteriskNodePoints(PassUpGpv-18000F);
                qualifiedFiveStarNetTreeNode.setFiveStarIntegral(currentFiveStarIntegral-(PassUpGpv-18000F));
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


}
