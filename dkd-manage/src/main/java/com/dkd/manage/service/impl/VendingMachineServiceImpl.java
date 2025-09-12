package com.dkd.manage.service.impl;

import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.bean.BeanUtil;
import com.dkd.common.constant.DkdContants;
import com.dkd.common.utils.DateUtils;
import com.dkd.common.utils.uuid.UUIDUtils;
import com.dkd.manage.domain.Channel;
import com.dkd.manage.domain.Node;
import com.dkd.manage.domain.VmType;
import com.dkd.manage.mapper.VmTypeMapper;
import com.dkd.manage.service.IChannelService;
import com.dkd.manage.service.INodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dkd.manage.mapper.VendingMachineMapper;
import com.dkd.manage.domain.VendingMachine;
import com.dkd.manage.service.IVendingMachineService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 设备管理Service业务层处理
 *
 * @author ruoyi
 * @date 2025-09-11
 */
@Service
public class VendingMachineServiceImpl implements IVendingMachineService {
    @Autowired
    private VendingMachineMapper vendingMachineMapper;
    @Autowired
    private VmTypeMapper vmTypeMapper;
    @Autowired
    private INodeService nodeService;
    @Autowired
    private IChannelService channelService;

    /**
     * 查询设备管理
     *
     * @param id 设备管理主键
     * @return 设备管理
     */
    @Override
    public VendingMachine selectVendingMachineById(Long id) {
        return vendingMachineMapper.selectVendingMachineById(id);
    }

    /**
     * 查询设备管理列表
     *
     * @param vendingMachine 设备管理
     * @return 设备管理
     */
    @Override
    public List<VendingMachine> selectVendingMachineList(VendingMachine vendingMachine) {
        return vendingMachineMapper.selectVendingMachineList(vendingMachine);
    }

    /**
     * 新增设备管理
     *
     * @param vendingMachine 设备管理
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int insertVendingMachine(VendingMachine vendingMachine) {
        // 新增设备
        // 生成货道编号
        String innerCode = UUIDUtils.getUUID();
        vendingMachine.setInnerCode(innerCode);
        // 查询售货机类型，补充设备容量
        VmType vmType = vmTypeMapper.selectVmTypeById(vendingMachine.getVmTypeId());
        vendingMachine.setChannelMaxCapacity(vmType.getChannelMaxCapacity());
        // 查询点位表，补充区域、点位、合作商等信息
        Node node = nodeService.selectNodeById(vendingMachine.getNodeId());
        BeanUtil.copyProperties(node, vendingMachine, "id");// 包含了商圈类型，区域，合作商
        vendingMachine.setAddr(node.getAddress());// 添加地址
        // 设备状态
        vendingMachine.setVmStatus(DkdContants.VM_STATUS_NODEPLOY); // 默认未投放
        // 设置创建时间
        vendingMachine.setCreateTime(DateUtils.getNowDate());
        // 设置更新时间
        vendingMachine.setUpdateTime(DateUtils.getNowDate());
        // 保存
        int result = vendingMachineMapper.insertVendingMachine(vendingMachine);
        // 新增货道
        // 利用双层for循环创建货道行列
        // 不再单个保存货道，声明List集合存储货道
        List<Channel> channelList = new ArrayList<>();
        for (int i = 1; i <= vmType.getVmRow(); i++) {
            for (int j = 1; j <= vmType.getVmCol(); j++) {
                // 封装channel对象
                Channel channel = new Channel();
                channel.setInnerCode(innerCode + "_" + i + "_" + j); // 货道编号
                channel.setVmId(vendingMachine.getId());
                channel.setChannelCode(vendingMachine.getInnerCode()); //售货机编号
                channel.setMaxCapacity(vmType.getChannelMaxCapacity()); //货道容量
                channel.setCreateTime(DateUtils.getNowDate()); // 创建时间
                channel.setUpdateTime(DateUtils.getNowDate()); // 更新时间
                // 保存货道
                //channelService.insertChannel(channel);
                channelList.add(channel);
            }
        }
        // 批量保存货道
        channelService.insertChannelBatch(channelList);
        return result;
    }

    /**
     * 修改设备管理
     *
     * @param vendingMachine 设备管理
     * @return 结果
     */
    @Override
    public int updateVendingMachine(VendingMachine vendingMachine) {
        // 查询点位表，补充区域、点位、合作商等信息
        Node node = nodeService.selectNodeById(vendingMachine.getNodeId());
        BeanUtil.copyProperties(node, vendingMachine, "id"); // 包含了商圈类型，区域，合作商
        vendingMachine.setAddr(node.getAddress()); // 添加地址
        // 添加更新时间
        vendingMachine.setUpdateTime(DateUtils.getNowDate());
        return vendingMachineMapper.updateVendingMachine(vendingMachine);
    }

    /**
     * 批量删除设备管理
     *
     * @param ids 需要删除的设备管理主键
     * @return 结果
     */
    @Override
    public int deleteVendingMachineByIds(Long[] ids) {
        return vendingMachineMapper.deleteVendingMachineByIds(ids);
    }

    /**
     * 删除设备管理信息
     *
     * @param id 设备管理主键
     * @return 结果
     */
    @Override
    public int deleteVendingMachineById(Long id) {
        return vendingMachineMapper.deleteVendingMachineById(id);
    }
}
