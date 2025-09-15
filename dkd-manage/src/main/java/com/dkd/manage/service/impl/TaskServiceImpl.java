package com.dkd.manage.service.impl;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.dkd.common.constant.DkdContants;
import com.dkd.common.exception.ServiceException;
import com.dkd.common.utils.DateUtils;
import com.dkd.manage.domain.Emp;
import com.dkd.manage.domain.TaskDetails;
import com.dkd.manage.domain.VendingMachine;
import com.dkd.manage.domain.dto.TaskDetailsDto;
import com.dkd.manage.domain.dto.TaskDto;
import com.dkd.manage.domain.vo.TaskVo;
import com.dkd.manage.service.IEmpService;
import com.dkd.manage.service.ITaskDetailsService;
import com.dkd.manage.service.IVendingMachineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.dkd.manage.mapper.TaskMapper;
import com.dkd.manage.domain.Task;
import com.dkd.manage.service.ITaskService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 工单Service业务层处理
 *
 * @author ruoyi
 * @date 2025-09-15
 */
@Service
public class TaskServiceImpl implements ITaskService {
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private IVendingMachineService vendingMachineService;
    @Autowired
    private IEmpService empService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ITaskDetailsService taskDetailsService;

    /**
     * 查询工单
     *
     * @param taskId 工单主键
     * @return 工单
     */
    @Override
    public Task selectTaskByTaskId(Long taskId) {
        return taskMapper.selectTaskByTaskId(taskId);
    }

    /**
     * 查询工单列表
     *
     * @param task 工单
     * @return 工单
     */
    @Override
    public List<Task> selectTaskList(Task task) {
        return taskMapper.selectTaskList(task);
    }

    /**
     * 新增工单
     *
     * @param task 工单
     * @return 结果
     */
    @Override
    public int insertTask(Task task) {
        task.setCreateTime(DateUtils.getNowDate());
        return taskMapper.insertTask(task);
    }

    /**
     * 修改工单
     *
     * @param task 工单
     * @return 结果
     */
    @Override
    public int updateTask(Task task) {
        task.setUpdateTime(DateUtils.getNowDate());
        return taskMapper.updateTask(task);
    }

    /**
     * 批量删除工单
     *
     * @param taskIds 需要删除的工单主键
     * @return 结果
     */
    @Override
    public int deleteTaskByTaskIds(Long[] taskIds) {
        return taskMapper.deleteTaskByTaskIds(taskIds);
    }

    /**
     * 删除工单信息
     *
     * @param taskId 工单主键
     * @return 结果
     */
    @Override
    public int deleteTaskByTaskId(Long taskId) {
        return taskMapper.deleteTaskByTaskId(taskId);
    }

    /**
     * 查询工单列表
     *
     * @param task 工单
     * @return 工单
     */
    @Override
    public List<TaskVo> selectTaskVoList(Task task) {
        return taskMapper.selectTaskVoList(task);
    }

    /**
     * 新增运营、运维工单
     *
     * @param taskDto 工单
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int insertTaskDto(TaskDto taskDto) {
        // 1.查询售货机是否存在
        VendingMachine vendingMachine = vendingMachineService.selectVendingMachineByInnerCode(taskDto.getInnerCode());
        if (vendingMachine == null) {
            throw new ServiceException("售货机不存在");
        }
        // 2.校验售货机状态和工单类型是否相符
        checkCreateTask(vendingMachine.getVmStatus(), taskDto.getProductTypeId());
        // 3.检查设备是否有未完成的同类型工单
        hasTask(taskDto);
        // 4.查询员工是否存在
        Emp emp = empService.selectEmpById(taskDto.getUserId());
        if (emp == null) {
            throw new ServiceException("员工不存在");
        }
        // 5.校验员工区域是否匹配
        if (!emp.getRegionId().equals(vendingMachine.getRegionId())){
            throw new ServiceException("员工区域不匹配,无法处理此工单");
        }
        // 6.将Dto转化为po补充属性
        Task task = BeanUtil.copyProperties(taskDto, Task.class); // 复制属性
        task.setTaskStatus(DkdContants.TASK_STATUS_CREATE); // 创建工单
        task.setUserName(emp.getUserName()); // 执行人名称
        task.setRegionId(emp.getRegionId()); // 所属区域
        task.setAddr(vendingMachine.getAddr()); // 设备地址
        task.setCreateTime(DateUtils.getNowDate()); // 创建时间
        task.setTaskCode(getTaskNo()); // 工单编号
        int taskResult = taskMapper.insertTask(task);
        // 7.判断是否为补货工单
        if (taskDto.getProductTypeId().equals(DkdContants.TASK_TYPE_SUPPLY)) {
            // 8.保存工单详情
            List<TaskDetailsDto> details = taskDto.getDetails();
            // 判断详情是否为空
            if (CollUtil.isEmpty(details)) {
                throw new ServiceException("工单详情不能为空");
            }
            // 将dto转为po
            List<TaskDetails> taskDetailsList = details.stream().map(dto -> {
                TaskDetails taskDetails = BeanUtil.copyProperties(dto, TaskDetails.class);
                taskDetails.setTaskId(task.getTaskId());
                return taskDetails;
            }).collect(Collectors.toList());
            // 批量新增
            taskDetailsService.insertTaskDetailsBatch(taskDetailsList);
        }
        return taskResult;
    }

    // 生成并获取当天的工单编号
    private String getTaskNo() {
        // 获取当前日期并格式化为yyyyMMdd
        String taskNo = DateUtils.getDate().replace("-", "");
        // 根据日期生成redis的值
        String key = "task_no:" + taskNo;
        // 判断key是否存在
        if(!redisTemplate.hasKey(key)){
            // 如果不存在，设置初始值为1，指定国企时间为1天
            redisTemplate.opsForValue().set(key, 1, Duration.ofDays(1));
            // 返回生成的工单编号(日期+0001)
            return taskNo + "0001";
        }
        // 如果存在，计数器加1，确保字符串长度为4位
        return taskNo + StrUtil.padPre(redisTemplate.opsForValue().increment(key).toString(), 4, "0");
    }

    // 检查设备是否有未完成的同类型工单
    private void hasTask(TaskDto taskDto) {
        // 创建Task对象，设置设备编号和工单类型，以及工单状态为进行中
        Task task = new Task();
        task.setInnerCode(taskDto.getInnerCode());
        task.setProductTypeId(taskDto.getProductTypeId());
        task.setTaskStatus(DkdContants.TASK_STATUS_PROGRESS);
        // 查看是否有符合条件的工单列表
        List<Task> taskList = taskMapper.selectTaskList(task);
        if (taskList != null && !taskList.isEmpty()) {
            throw new ServiceException("设备有未完成的工单,不能重复创建");
        }
    }

    // 2.校验售货机状态和工单类型是否相符
    private void checkCreateTask(Long vmStatus, Long productTypeId) {
        // 如果是投放工单，设备再运行中，抛出异常
        if (Objects.equals(productTypeId, DkdContants.TASK_TYPE_DEPLOY) && Objects.equals(vmStatus, DkdContants.VM_STATUS_RUNNING)) {
            throw new ServiceException("设备为运行中，无法进行投放");
        }
        // 如果是维修工单，设备不在运行中，抛出异常
        if (Objects.equals(productTypeId, DkdContants.TASK_TYPE_REPAIR) && !Objects.equals(vmStatus, DkdContants.VM_STATUS_RUNNING)) {
            throw new ServiceException("设备不在运行中，无法进行维修");
        }
        // 如果是补货工单，设备不在运行中，抛出异常
        if (Objects.equals(productTypeId, DkdContants.TASK_TYPE_SUPPLY) && !Objects.equals(vmStatus, DkdContants.VM_STATUS_RUNNING)) {
            throw new ServiceException("设备不在运行中，无法进行补货");
        }
        // 如果是撤机工单，设备不在运行中，抛出异常
        if (Objects.equals(productTypeId, DkdContants.TASK_TYPE_REVOKE) && !Objects.equals(vmStatus, DkdContants.VM_STATUS_RUNNING)) {
            throw new ServiceException("设备不在运行中，无法进行撤机");
        }
    }
}
