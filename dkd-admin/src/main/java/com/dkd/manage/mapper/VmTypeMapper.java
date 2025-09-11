package com.dkd.manage.mapper;

import java.util.List;
import com.dkd.manage.domain.VmType;

/**
 * 设备类型Mapper接口
 * 
 * @author ruoyi
 * @date 2025-09-11
 */
public interface VmTypeMapper 
{
    /**
     * 查询设备类型
     * 
     * @param id 设备类型主键
     * @return 设备类型
     */
    public VmType selectVmTypeById(Long id);

    /**
     * 查询设备类型列表
     * 
     * @param vmType 设备类型
     * @return 设备类型集合
     */
    public List<VmType> selectVmTypeList(VmType vmType);

    /**
     * 新增设备类型
     * 
     * @param vmType 设备类型
     * @return 结果
     */
    public int insertVmType(VmType vmType);

    /**
     * 修改设备类型
     * 
     * @param vmType 设备类型
     * @return 结果
     */
    public int updateVmType(VmType vmType);

    /**
     * 删除设备类型
     * 
     * @param id 设备类型主键
     * @return 结果
     */
    public int deleteVmTypeById(Long id);

    /**
     * 批量删除设备类型
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteVmTypeByIds(Long[] ids);
}
