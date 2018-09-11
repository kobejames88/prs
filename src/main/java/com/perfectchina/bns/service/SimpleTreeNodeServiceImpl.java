package com.perfectchina.bns.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.perfectchina.bns.common.utils.DateUtils;
import com.perfectchina.bns.model.treenode.DoubleGoldDiamondNetTreeNode;
import com.perfectchina.bns.model.vo.DoubleGoldDiamonndVo;
import com.perfectchina.bns.model.vo.SimpleVo;
import com.perfectchina.bns.repositories.SimpleNetTreeNodeRepository;
import com.perfectchina.bns.service.Enum.Pin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.perfectchina.bns.model.SalesRecord;
import com.perfectchina.bns.model.treenode.SimpleNetTreeNode;
import com.perfectchina.bns.model.treenode.TreeNode;
import com.perfectchina.bns.repositories.TreeNodeRepository;

@Service
public class SimpleTreeNodeServiceImpl extends TreeNodeServiceImpl implements SimpleTreeNodeService {
	
    private static final Logger logger = LoggerFactory.getLogger(SimpleTreeNodeServiceImpl.class);
	
	@Autowired
	private SalesRecordService salesRecordService;
    
	@Autowired
	private SimpleNetTreeNodeRepository simpleTreeNodeRepository;
	
    private Date previousDateEndTime; // Parameter to set calculate PPV for which date
    
    
	public Date getPreviousDateEndTime() {
		return previousDateEndTime;
	}

	public void setPreviousDateEndTime(Date previousDateEndTime) {
		this.previousDateEndTime = previousDateEndTime;
	}

	public TreeNodeRepository<SimpleNetTreeNode> getTreeNodeRepository() {
		return simpleTreeNodeRepository;
	}
	
	public boolean isReadyToUpdate() {
		// assume sales record ready for update
		return true;		
	}
	
	protected void process( TreeNode node ) {
		logger.debug("process, update node="+ node.getData().getAccountNum() +"/"+ node.getData().getName()+
				", level ["+ node.getLevelNum()+"]." );
		
		// retrieve sales records of given period to calculate the PPV
		Long accountId = node.getData().getId();
		Date lastMonthEndDate = DateUtils.getLastMonthEndDate(new Date());
		List<SalesRecord> accountMonthlySales = salesRecordService.retrieveSelfSaleRecordOfLastMonth( accountId, lastMonthEndDate );
		SalesRecord totalSales = salesRecordService.findOutPersonalSalesRecordTotal(accountMonthlySales);
		
		SimpleNetTreeNode thisNode = (SimpleNetTreeNode) node ;
        float salesPV = totalSales.getSalesPV();
        thisNode.setPpv( salesPV );
        thisNode.setTotalSales(salesPV);
		getTreeNodeRepository().saveAndFlush( thisNode);	
		
	}

	protected void process( TreeNode node,String snapshotDate ) {
		logger.debug("process, update node="+ node.getData().getAccountNum() +"/"+ node.getData().getName()+
				", level ["+ node.getLevelNum()+"]." );

		// retrieve sales records of given period to calculate the PPV
		Long accountId = node.getData().getId();
		Date date = null;
		try {
			date = new SimpleDateFormat("yyyyMM").parse(snapshotDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Date snapshotDateEndDate = DateUtils.getCurrentMonthEndDate(date);
		List<SalesRecord> accountMonthlySales = salesRecordService.retrieveSelfSaleRecordOfLastMonth( accountId, snapshotDateEndDate );
		SalesRecord totalSales = salesRecordService.findOutPersonalSalesRecordTotal(accountMonthlySales);

		SimpleNetTreeNode thisNode = (SimpleNetTreeNode) node ;
        float salesPV = totalSales.getSalesPV();
        thisNode.setPpv( salesPV );
        thisNode.setTotalSales(salesPV);

        //先复制上个月AOPV，防止不活跃丢失累计opv
		SimpleNetTreeNode lastMonthNode = simpleTreeNodeRepository.findByAccountNum(DateUtils.getLastMonthSnapshotDate(snapshotDate), thisNode.getData().getAccountNum());
		if(lastMonthNode!=null){
			thisNode.setAopv(lastMonthNode.getAopv());
		}
		getTreeNodeRepository().saveAndFlush( thisNode);

	}

	@Override
	public List<SimpleVo> convertSimpleVo(String snapshotDate) {
        List<SimpleVo> simpleVos = new ArrayList<>();
        // 获取level为1的数据
        List<SimpleNetTreeNode> simpleNetTreeNodes = getTreeNodeRepository().getTreeNodesByLevelAndSnapshotDate(snapshotDate,1);
        if (simpleNetTreeNodes.size() >0 ){
            for (SimpleNetTreeNode simpleNetTreeNode : simpleNetTreeNodes){
                SimpleVo simpleVo = recursion(simpleNetTreeNode);
                simpleVos.add(simpleVo);
            }
        }
        return simpleVos;
	}

    private SimpleVo recursion(SimpleNetTreeNode simpleNetTreeNode){
        List<SimpleNetTreeNode> childs = getTreeNodeRepository().findByParentId(simpleNetTreeNode.getId());
        List<SimpleVo> nodes = new ArrayList<>();
        if (childs != null){
            for (SimpleNetTreeNode child : childs){
                SimpleVo node = recursion(child);
                nodes.add(node);
            }
        }
        return  convertChildFiveStarVo(simpleNetTreeNode,nodes);
    }

    private SimpleVo convertChildFiveStarVo(SimpleNetTreeNode child, List<SimpleVo> nodes){
        SimpleVo childVo = new SimpleVo();
        childVo.setLevelNum(child.getLevelNum());
        childVo.setName(child.getData().getName());
        childVo.setAccountNum(child.getData().getAccountNum());
        childVo.setPpv(child.getPpv());
        childVo.setMoney(child.getTotalSales());
        childVo.setChildren(nodes);
        return childVo;
    }
}
