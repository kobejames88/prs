package com.perfectchina.bns.service;

import com.perfectchina.bns.model.Rank;
import com.perfectchina.bns.model.treenode.FiveStarNetTreeNode;
import com.perfectchina.bns.model.treenode.GoldDiamondNetTreeNode;
import com.perfectchina.bns.model.treenode.QualifiedFiveStarNetTreeNode;
import com.perfectchina.bns.model.treenode.TreeNode;
import com.perfectchina.bns.model.vo.GoldDiamonndVo;
import com.perfectchina.bns.repositories.FiveStarNetTreeNodeRepository;
import com.perfectchina.bns.repositories.GoldDiamondNetTreeNodeRepository;
import com.perfectchina.bns.repositories.QualifiedFiveStarNetTreeNodeRepository;
import com.perfectchina.bns.repositories.TreeNodeRepository;
import com.perfectchina.bns.service.Enum.Pin;
import com.perfectchina.bns.service.pin.PinPosition;
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


//	@Autowired
//	private SimpleNetTreeNodeRepository simpleNetTreeNodeRepository;
    @Autowired
    private FiveStarNetTreeNodeRepository fiveStarNetTreeNodeRepository;

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
                        if (Pin.codeOf(PinPosition.EMERALD).getCode() <= Pin.codeOf(rank.getPin()).getCode()){
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
                        saveOpv2Map(sign,opvs,q_opv,upRank);
                    }else {
                        // 如果没有翡翠及以上职级,将此节点的opv存入map
                        saveOpv2Map(sign,opvs,opv,upRank);
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
                        goldDiamondNetTreeNodeRepository.saveAndFlush(goldDiamondUplink);
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

    private void saveOpv2Map(Boolean sign,List<Float> opvs,Float opv,Rank upRank){
        if (!sign){
            if (opvs != null){
                opvs.add(opv);
                nodeOpv.put(upRank.getId(),opvs);
            }else {
                List<Float> opvList = new ArrayList<>();
                opvList.add(opv);
                nodeOpv.put(upRank.getId(),opvList);
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
		goldDiamondNetTreeNodeRepository.saveAndFlush(goldDiamondNetTreeNode);
	}

	@Override
	/**
	 * Update the entire tree's pass-up-gpv
	 */
	public void updateWholeTreeGoldDiamond(String snapshotDate) {
		// Get the level of the original tree
		int treeLevel = getMaxTreeLevel(snapshotDate);
		if (treeLevel < 0)
			return;
		while (treeLevel > 0) {
			List<GoldDiamondNetTreeNode> thisTreeLevelTreeList = goldDiamondNetTreeNodeRepository.getTreeNodesByLevel(treeLevel);
			// loop for the children to calculate OPV at the lowest level
			for (GoldDiamondNetTreeNode goldDiamondNetTreeNode : thisTreeLevelTreeList) {
                long id = goldDiamondNetTreeNode.getId();
                List<GoldDiamondNetTreeNode> childs = goldDiamondNetTreeNodeRepository.getChildNodesByUpid(id);
                if (childs.size()>0){
                    goldDiamondNetTreeNode.setHasChild(true);
                }else {
                    goldDiamondNetTreeNode.setHasChild(false);
                }
                goldDiamondNetTreeNodeRepository.saveAndFlush(goldDiamondNetTreeNode);
			} // end for loop
			treeLevel--;
		}
	}

	@Override
	public List<GoldDiamonndVo> convertGoldDiamondVo(String snapshotDate) {
        List<GoldDiamonndVo> goldDiamonndVos = new ArrayList<>();
        List<GoldDiamondNetTreeNode> goldDiamondNetTreeNodes = getTreeNodeRepository().getTreeNodesBySnapshotDate(snapshotDate);
        if ( goldDiamondNetTreeNodes.size() > 0){
            for (GoldDiamondNetTreeNode goldDiamondNetTreeNode : goldDiamondNetTreeNodes){
                if (goldDiamondNetTreeNode.getUplinkId() == 0) continue;
                GoldDiamonndVo goldDiamonndVo = new GoldDiamonndVo();
                List<GoldDiamondNetTreeNode> childNodes = goldDiamondNetTreeNodeRepository.getChildNodesByUpid(goldDiamondNetTreeNode.getId());
                int count = childNodes.size();
                FiveStarNetTreeNode fiveStarAccountNum = fiveStarNetTreeNodeRepository.findByAccountNum(snapshotDate, goldDiamondNetTreeNode.getData().getAccountNum());
                goldDiamonndVo.setLevelNum(goldDiamondNetTreeNode.getLevelNum());
                goldDiamonndVo.setName(goldDiamondNetTreeNode.getData().getName());
                goldDiamonndVo.setAccountNum(goldDiamondNetTreeNode.getData().getAccountNum());
                goldDiamonndVo.setPpv(fiveStarAccountNum.getPpv());
                goldDiamonndVo.setGpv(fiveStarAccountNum.getGpv());
                goldDiamonndVo.setOpv(fiveStarAccountNum.getOpv());
                goldDiamonndVo.setQualifiedGoldDiamond(count);
                String pin = goldDiamondNetTreeNode.getData().getPin();
                goldDiamonndVo.setPin(Pin.codeOf(pin).getCode());
                // todo 获取每人的历史最高PIN
                goldDiamonndVo.setMaxPin(Pin.codeOf(pin).getCode());
                goldDiamonndVos.add(goldDiamonndVo);
            }
        }
        return goldDiamonndVos;
	}

	public TreeNode getRootNode(String snapshotDate) {
		TreeNode rootNode = goldDiamondNetTreeNodeRepository.getRootTreeNodeOfMonth( snapshotDate );
		return rootNode;
	}

}
