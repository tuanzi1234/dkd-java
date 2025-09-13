package com.dkd.manage.service.impl;

import java.util.List;
import com.dkd.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dkd.manage.mapper.TbPolicyMapper;
import com.dkd.manage.domain.TbPolicy;
import com.dkd.manage.service.ITbPolicyService;

/**
 * 策略Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-09-13
 */
@Service
public class TbPolicyServiceImpl implements ITbPolicyService 
{
    @Autowired
    private TbPolicyMapper tbPolicyMapper;

    /**
     * 查询策略
     * 
     * @param policyId 策略主键
     * @return 策略
     */
    @Override
    public TbPolicy selectTbPolicyByPolicyId(Long policyId)
    {
        return tbPolicyMapper.selectTbPolicyByPolicyId(policyId);
    }

    /**
     * 查询策略列表
     * 
     * @param tbPolicy 策略
     * @return 策略
     */
    @Override
    public List<TbPolicy> selectTbPolicyList(TbPolicy tbPolicy)
    {
        return tbPolicyMapper.selectTbPolicyList(tbPolicy);
    }

    /**
     * 新增策略
     * 
     * @param tbPolicy 策略
     * @return 结果
     */
    @Override
    public int insertTbPolicy(TbPolicy tbPolicy)
    {
        tbPolicy.setCreateTime(DateUtils.getNowDate());
        return tbPolicyMapper.insertTbPolicy(tbPolicy);
    }

    /**
     * 修改策略
     * 
     * @param tbPolicy 策略
     * @return 结果
     */
    @Override
    public int updateTbPolicy(TbPolicy tbPolicy)
    {
        tbPolicy.setUpdateTime(DateUtils.getNowDate());
        return tbPolicyMapper.updateTbPolicy(tbPolicy);
    }

    /**
     * 批量删除策略
     * 
     * @param policyIds 需要删除的策略主键
     * @return 结果
     */
    @Override
    public int deleteTbPolicyByPolicyIds(Long[] policyIds)
    {
        return tbPolicyMapper.deleteTbPolicyByPolicyIds(policyIds);
    }

    /**
     * 删除策略信息
     * 
     * @param policyId 策略主键
     * @return 结果
     */
    @Override
    public int deleteTbPolicyByPolicyId(Long policyId)
    {
        return tbPolicyMapper.deleteTbPolicyByPolicyId(policyId);
    }
}
