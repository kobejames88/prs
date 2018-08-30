package com.perfectchina.bns.service;

import com.perfectchina.bns.model.Account;
import com.perfectchina.bns.model.AccountPinHistory;
import com.perfectchina.bns.model.Rank;
import com.perfectchina.bns.model.treenode.*;
import com.perfectchina.bns.model.vo.GoldDiamonndVo;
import com.perfectchina.bns.model.vo.QualifiedFiveStarVo;
import com.perfectchina.bns.repositories.*;
import com.perfectchina.bns.service.Enum.Pin;
import com.perfectchina.bns.service.pin.PinPosition;
import com.perfectchina.bns.service.reward.RewardPosition;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class GoldDiamondTreeNodeServiceImpl extends TreeNodeServiceImpl implements GoldDiamondTreeNodeService {

	private static final Logger logger = LoggerFactory.getLogger(GoldDiamondTreeNodeServiceImpl.class);

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM", Locale.ENGLISH);

	@Autowired
	private GoldDiamondNetTreeNodeRepository goldDiamondNetTreeNodeRepository;

	@Autowired
	private QualifiedFiveStarNetTreeNodeRepository qualifiedFiveStarNetTreeNodeRepository;

    @Autowired
    private FiveStarNetTreeNodeRepository fiveStarNetTreeNodeRepository;

    @Autowired
    private AccountRepository accountRepository;

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
	public TreeNodeRepository<GoldDiamondNetTreeNode> getTreeNodeRepository() {
		return goldDiamondNetTreeNodeRepository;
	}

	public boolean isReadyToUpdate() {
		// need to check if Simple Net already exist, otherwise, cannot
		// calculate
		boolean isReady = false;
		// // Current month
		String snapshotDate = null;
		try {
			snapshotDate = sdf.format(getPreviousDateEndTime());
			QualifiedFiveStarNetTreeNode rootNode = qualifiedFiveStarNetTreeNodeRepository.getRootTreeNodeOfMonth(snapshotDate);

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
		TreeNode rootNode = qualifiedFiveStarNetTreeNodeRepository.getRootTreeNode(snapshotDate);
		updateChildTreeLevel( 0, rootNode,snapshotDate );
	}

	@Override
	public void updateChildTreeLevel(Integer fromLevelNum, TreeNode fromNode,String snapshotDate) {
		super.updateChildTreeLevel(fromLevelNum, fromNode,snapshotDate);
	}

    public int getMaxTreeLevel(String snapShotDate) {
        Integer maxLevelNum = getTreeNodeRepository().getMaxLevelNum(snapShotDate);
        return maxLevelNum == null ? 0 : maxLevelNum;
    }

	private Map<Long,Long> relation = new HashMap<>();
    private Map<Long,List<Rank>> goldRelationLine = new HashMap<>();
    private Map<Long,List<Float>> nodeOpv = new HashMap<>();

	@Override
	protected void process(TreeNode node, String snapshotDate) {
		logger.debug("process, update node=" + node.getData().getAccountNum() + "/" + node.getData().getName()
				+ ", level [" + node.getLevelNum() + "].");
		// 当前元素
		QualifiedFiveStarNetTreeNode qualifiedFiveStarNetTreeNode = (QualifiedFiveStarNetTreeNode) node;
		// 待装载元素
		GoldDiamondNetTreeNode goldDiamondNetTreeNode = new GoldDiamondNetTreeNode();
		String pin = qualifiedFiveStarNetTreeNode.getPin();
		Long id = qualifiedFiveStarNetTreeNode.getId();
		Long map_uplinkId = relation.get(id);

		if (map_uplinkId != null){
			// 如果map中有上级id
            // 获取上级信息
			QualifiedFiveStarNetTreeNode qualifiedFiveStarUplink = qualifiedFiveStarNetTreeNodeRepository.getOne(map_uplinkId);
			String uplinkAccountNum = qualifiedFiveStarUplink.getData().getAccountNum();
			GoldDiamondNetTreeNode goldDiamondUplink = getTreeNodeRepository().getAccountByAccountNum(qualifiedFiveStarNetTreeNode.getSnapshotDate(),
					uplinkAccountNum);
			long goldDiamondUplinkId = goldDiamondUplink.getId();
			String uplinkLevelLine = goldDiamondUplink.getLevelLine();
            Float opv = qualifiedFiveStarNetTreeNode.getOpv();
            List<Rank> ranks = goldRelationLine.get(id);
            List<QualifiedFiveStarNetTreeNode> childNodes = qualifiedFiveStarNetTreeNodeRepository.getChildNodesByUpid(id);
            if (StringUtils.equals(PinPosition.GOLD_DIAMOND,pin)){
                goldDiamondNetTreeNode.setUplinkId(goldDiamondUplink.getId());
                goldDiamondNetTreeNode.setOpv(opv);
                goldDiamondNetTreeNode.setPassUpOpv(opv);
			    // 如果是金钻职级
                if (ranks != null){
                    // 有上级金钻,获取line判断是否有翡翠以上职级
                    Boolean hasEmerald = false;
                    for (Rank rank : ranks){
                        if (Pin.descOf(PinPosition.EMERALD).getCode() <= Pin.descOf(rank.getPin()).getCode()){
                            hasEmerald = true;
                            break;
                        }
                    }
                    Rank upRank = ranks.get(0);
                    QualifiedFiveStarNetTreeNode up = qualifiedFiveStarNetTreeNodeRepository.findById(upRank.getId()).get();
                    GoldDiamondNetTreeNode g_up = goldDiamondNetTreeNodeRepository.findByAccountNum(snapshotDate, up.getData().getAccountNum());

                    Rank rank = ranks.get(1);
                    QualifiedFiveStarNetTreeNode q = qualifiedFiveStarNetTreeNodeRepository.findById(rank.getId()).get();
                    Boolean sign = q.getAboveEmeraldNodeSign();
                    Float q_opv = q.getOpv();

                    List<Float> opvs = nodeOpv.get(upRank);
                    if (hasEmerald){
                        // 如果有翡翠及以上职级,将此节点线上的金钻下级opv存入map
                        saveOpv2Map(sign,opvs,q_opv,g_up);
                    }else {
                        // 如果没有翡翠及以上职级,将此节点的opv存入map
                        saveOpv2Map(sign,opvs,opv,g_up);
                    }
                    // 上级金钻的opv-此节点的opv
                    g_up.setPassUpOpv(g_up.getPassUpOpv() - q_opv);
                    goldDiamondNetTreeNodeRepository.save(g_up);
                }
				// 获取金钻的所有直接下级
                if (childNodes.size() > 0){
                    for (TreeNode childNode : childNodes){
                        QualifiedFiveStarNetTreeNode qualifiedFiveChildNode = (QualifiedFiveStarNetTreeNode)childNode;
                        long qc_id = qualifiedFiveChildNode.getId();
                        relation.put(qc_id,id);

                        Rank rank = new Rank();
                        rank.setId(id);
                        rank.setPin(pin);
                        List<Rank> upRank = new ArrayList<>();
                        upRank.add(rank);
                        goldRelationLine.put(qc_id, upRank);
                    }
                }

				setuplinkLevelLineAndLevel(uplinkLevelLine,goldDiamondNetTreeNode,goldDiamondUplinkId);
				copyNetTree(qualifiedFiveStarNetTreeNode,goldDiamondNetTreeNode);
			}else {
			    // 如果不是金钻
			    if (goldDiamondUplink.getLevelNum() > 0){
                    if (ranks != null){
                        // 有上级金钻
                        Rank rank = new Rank();
                        rank.setId(id);
                        rank.setPin(pin);
                        ranks.add(rank);
                        // 获取当前元素的所有直接下级
                        if (childNodes.size() > 0){
                            for (TreeNode childNode : childNodes){
                                QualifiedFiveStarNetTreeNode qualifiedFiveChildNode = (QualifiedFiveStarNetTreeNode)childNode;
                                long qc_id = qualifiedFiveChildNode.getId();
                                goldRelationLine.put(qc_id,ranks);
                                relation.put(qc_id,map_uplinkId);
                            }
                        }
                        goldDiamondNetTreeNodeRepository.save(goldDiamondUplink);
                    }
                }
                goldRelationLine.remove(id);
				relation.remove(id);
			}
		}else {
		    // 如果map中没有上级id,并且level为0
            if (qualifiedFiveStarNetTreeNode.getLevelNum() == 0){
                goldDiamondNetTreeNode.setUplinkId(0);
                goldDiamondNetTreeNode.setLevelLine(String.valueOf(0));
                goldDiamondNetTreeNode.setLevelNum(0);
                // 获取根节点的所有直接下级
                List<QualifiedFiveStarNetTreeNode> childNodes = qualifiedFiveStarNetTreeNodeRepository.getChildNodesByUpid(id);
                for (TreeNode childNode : childNodes){
                    QualifiedFiveStarNetTreeNode qualifiedFiveChildNode = (QualifiedFiveStarNetTreeNode)childNode;
                    relation.put(qualifiedFiveChildNode.getId(),qualifiedFiveStarNetTreeNode.getId());
                }
                copyNetTree(qualifiedFiveStarNetTreeNode,goldDiamondNetTreeNode);
            }
		}
	}

    private void saveOpv2Map(Boolean sign,List<Float> opvs,Float opv,GoldDiamondNetTreeNode up){
        if (!sign){
            if (opvs != null){
                opvs.add(opv);
                nodeOpv.put(up.getId(),opvs);
            }else {
                List<Float> opvList = new ArrayList<>();
                opvList.add(opv);
                nodeOpv.put(up.getId(),opvList);
            }
        }
    }

	private void setuplinkLevelLineAndLevel(String uplinkLevelLine,GoldDiamondNetTreeNode goldDiamondNetTreeNode,Long goldDiamondUplinkId){
        String newUplinkLevelLine = String.valueOf(new StringBuilder().append(uplinkLevelLine).append("_").append(goldDiamondUplinkId));
        String[] newUplinkLevelLines = StringUtils.split(newUplinkLevelLine, "_");
        int level = newUplinkLevelLines.length-1;
        goldDiamondNetTreeNode.setLevelLine(newUplinkLevelLine);
        goldDiamondNetTreeNode.setLevelNum(level);
    }

	private void copyNetTree(QualifiedFiveStarNetTreeNode qualifiedFiveStarNetTreeNode,GoldDiamondNetTreeNode goldDiamondNetTreeNode){
        goldDiamondNetTreeNode.setSnapshotDate(qualifiedFiveStarNetTreeNode.getSnapshotDate());
        goldDiamondNetTreeNode.setData(qualifiedFiveStarNetTreeNode.getData());
        goldDiamondNetTreeNode.setGoldDiamondLine(qualifiedFiveStarNetTreeNode.getGoldDiamondLine());
        goldDiamondNetTreeNode.setMergePoints(0F);
        goldDiamondNetTreeNode.setPpv(qualifiedFiveStarNetTreeNode.getPpv());
        goldDiamondNetTreeNode.setGpv(qualifiedFiveStarNetTreeNode.getGpv());
		goldDiamondNetTreeNodeRepository.save(goldDiamondNetTreeNode);
	}

	@Override
	/**
	 * Update the entire tree's pass-up-gpv
	 */
	public void updateWholeTreeGoldDiamond(String snapshotDate) {
		int treeLevel = getMaxTreeLevel(snapshotDate);
		if (treeLevel < 0)
			return;
		while (treeLevel > 0) {
			List<GoldDiamondNetTreeNode> thisTreeLevelTreeList = goldDiamondNetTreeNodeRepository.getTreeNodesByLevel(treeLevel);
			for (GoldDiamondNetTreeNode goldDiamondNetTreeNode : thisTreeLevelTreeList) {
                long id = goldDiamondNetTreeNode.getId();
                Float opv = goldDiamondNetTreeNode.getOpv();
                // 获取当前节点每每条金钻线的opv
                List<Float> opvs = nodeOpv.get(id);
                if (opvs != null){
                    int size = opvs.size();
                    if (size > 0){
                        Boolean flag = false;
                        Float mergePoints = 0F;
                        if (size >= 7) {
                            // 如果有 x>=7 个直接下级，尝试合并
                            mergePoints = calculateMergingPoints(7, size, opvs);
                            judgeAndSaveReward(7, mergePoints, goldDiamondNetTreeNode, null);
                            if (mergePoints < 200000F) {
                                flag = true;
                            }
                        }
                        if (flag || (size >= 4 && size < 7)) {
                            // 如果有 x>=7 个直接下级但是合并失败 或 4<= x <7, 尝试合并
                            mergePoints = calculateMergingPoints(4, size, opvs);
                            judgeAndSaveReward(4, mergePoints, goldDiamondNetTreeNode, opv);
                        }
                        goldDiamondNetTreeNode.setMergePoints(mergePoints);
                    }
                }

                // 更新当前节点是否有子节点
                List<GoldDiamondNetTreeNode> childs = goldDiamondNetTreeNodeRepository.getChildNodesByUpid(id);
                if (childs.size()>0){
                    goldDiamondNetTreeNode.setHasChild(true);
                }else {
                    goldDiamondNetTreeNode.setHasChild(false);
                }
                goldDiamondNetTreeNodeRepository.save(goldDiamondNetTreeNode);
                nodeOpv.remove(id);
			} // end for loop
			treeLevel--;
		}
	}

    private void judgeAndSaveReward(int type, Float mergingPoints, GoldDiamondNetTreeNode goldDiamondNetTreeNode, Float opv) {
        switch (type) {
            case 7:
                if (mergingPoints >= 1000000F) {
                    // 4重奖励，双金钻
                    saveInfo(goldDiamondNetTreeNode, RewardPosition.QUADRUPLE_REWARD);
                }
                if (mergingPoints >= 500000F) {
                    // 3重奖励，双金钻
                    saveInfo(goldDiamondNetTreeNode, RewardPosition.TRIPLE_REWARD);
                }
                if (mergingPoints >= 200000F) {
                    // 2重奖励，双金钻
                    saveInfo(goldDiamondNetTreeNode, RewardPosition.DOUBLE_REWARD);
                }
                break;
            case 4:
                // 4条100万金钻线，且本人OPV达1000万
                if (mergingPoints >= 1000000F && opv >= 10000000F) {
                    // 1重奖励，双金钻
                    saveInfo(goldDiamondNetTreeNode, RewardPosition.ONCE_REWARD);
                }
                break;
        }
    }

    private void saveInfo(GoldDiamondNetTreeNode goldDiamondNetTreeNode, String reward) {
        goldDiamondNetTreeNode.setReward(reward);
        Account account = accountRepository.getAccountById(goldDiamondNetTreeNode.getData().getId());
        savePinAndHistory(account, PinPosition.DOUBLE_GOLD_DIAMOND);
    }

    private void savePinAndHistory(Account account,String pin){
        account.setPin(pin);
        accountRepository.save(account);
        String maxPin = account.getMaxPin();
        Integer max = Pin.descOf(pin).getCode();
        Integer temp = Pin.descOf(maxPin).getCode();
        if (max > temp){
            while (max > temp){
                temp+=1;
                String temp_pin = Pin.codeOf(temp).getDesc();
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

    private Float calculateMergingPoints(int line, int count, List<Float> opvs) {
        int avg = count / line;
        int mod = count % line;
        float[] every = new float[line];
        float total = 0F;
        for (int i = 0; i < line; i++) {
            int step = (i == line - 1) ? avg + mod : avg;
            for (int j = i * avg; j < (i * avg + step); j++) {
                every[i] += opvs.get(j);
            }
            total = (i == 0) ? every[i] : every[i] + total;
        }
        return total;
    }

	@Override
	public List<GoldDiamonndVo> convertGoldDiamondVo(String snapshotDate) {
        List<GoldDiamonndVo> goldDiamonndVos = new ArrayList<>();
        // 获取level为1的数据
        List<GoldDiamondNetTreeNode> goldDiamondNetTreeNodes = getTreeNodeRepository().getTreeNodesByLevelAndSnapshotDate(snapshotDate,1);
        if (goldDiamondNetTreeNodes.size() >0 ){
            for (GoldDiamondNetTreeNode goldDiamondNetTreeNode : goldDiamondNetTreeNodes){
                GoldDiamonndVo goldDiamonndVo = recursion(goldDiamondNetTreeNode);
                goldDiamonndVos.add(goldDiamonndVo);
            }
        }
        return goldDiamonndVos;
	}

    private GoldDiamonndVo recursion(GoldDiamondNetTreeNode goldDiamondNetTreeNode){
        List<GoldDiamondNetTreeNode> childs = getTreeNodeRepository().findByParentId(goldDiamondNetTreeNode.getId());
        List<GoldDiamonndVo> nodes = new ArrayList<>();
        if (childs != null){
            for (GoldDiamondNetTreeNode child : childs){
                GoldDiamonndVo node = recursion(child);
                nodes.add(node);
            }
        }
        return  convertChildFiveStarVo(goldDiamondNetTreeNode,nodes);
    }

    private GoldDiamonndVo convertChildFiveStarVo(GoldDiamondNetTreeNode child, List<GoldDiamonndVo> nodes){
        GoldDiamonndVo childVo = new GoldDiamonndVo();
        childVo.setLevelNum(child.getLevelNum());
        childVo.setName(child.getData().getName());
        childVo.setAccountNum(child.getData().getAccountNum());
        childVo.setPpv(child.getPpv());
        childVo.setGpv(child.getGpv());
        childVo.setOpv(child.getOpv());
        childVo.setQualifiedGoldDiamond(child.getGoldDiamondLine());
        childVo.setPin(Pin.descOf(child.getData().getPin()).getCode());
        childVo.setMaxPin(Pin.descOf(child.getData().getMaxPin()).getCode());
        childVo.setDownlines(nodes);
        return childVo;
    }

	public TreeNode getRootNode(String snapshotDate) {
		TreeNode rootNode = goldDiamondNetTreeNodeRepository.getRootTreeNodeOfMonth( snapshotDate );
		return rootNode;
	}

}
