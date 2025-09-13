package com.dkd.manage.service;

import java.util.List;
import com.dkd.manage.domain.TbPolicy;

/**
 * 策略Service接口
 * 
 * @author ruoyi
 * @date 2025-09-13
 */
public interface ITbPolicyService 
{
    /**
     * 查询策略
     * 
     * @param policyId 策略主键
     * @return 策略
     */
    public TbPolicy selectTbPolicyByPolicyId(Long policyId);

    /**
     * 查询策略列表
     * 
     * @param tbPolicy 策略
     * @return 策略集合
     */
    public List<TbPolicy> selectTbPolicyList(TbPolicy tbPolicy);

    /**
     * 新增策略
     * 
     * @param tbPolicy 策略
     * @return 结果
     */
    public int insertTbPolicy(TbPolicy tbPolicy);

    /**
     * 修改策略
     * 
     * @param tbPolicy 策略
     * @return 结果
     */
    public int updateTbPolicy(TbPolicy tbPolicy);

    /**
     * 批量删除策略
     * 
     * @param policyIds 需要删除的策略主键集合
     * @return 结果
     */
    public int deleteTbPolicyByPolicyIds(Long[] policyIds);

    /**
     * 删除策略信息
     * 
     * @param policyId 策略主键
     * @return 结果
     */
    public int deleteTbPolicyByPolicyId(Long policyId);
}
