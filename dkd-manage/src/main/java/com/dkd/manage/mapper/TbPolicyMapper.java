package com.dkd.manage.mapper;

import java.util.List;
import com.dkd.manage.domain.TbPolicy;

/**
 * 策略Mapper接口
 * 
 * @author ruoyi
 * @date 2025-09-13
 */
public interface TbPolicyMapper 
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
     * 删除策略
     * 
     * @param policyId 策略主键
     * @return 结果
     */
    public int deleteTbPolicyByPolicyId(Long policyId);

    /**
     * 批量删除策略
     * 
     * @param policyIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTbPolicyByPolicyIds(Long[] policyIds);
}
