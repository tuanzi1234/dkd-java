package com.dkd.manage.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.dkd.common.annotation.Log;
import com.dkd.common.core.controller.BaseController;
import com.dkd.common.core.domain.AjaxResult;
import com.dkd.common.enums.BusinessType;
import com.dkd.manage.domain.TbPolicy;
import com.dkd.manage.service.ITbPolicyService;
import com.dkd.common.utils.poi.ExcelUtil;
import com.dkd.common.core.page.TableDataInfo;

/**
 * 策略Controller
 * 
 * @author ruoyi
 * @date 2025-09-13
 */
@RestController
@RequestMapping("/manage/policy")
public class TbPolicyController extends BaseController
{
    @Autowired
    private ITbPolicyService tbPolicyService;

    /**
     * 查询策略列表
     */
    @PreAuthorize("@ss.hasPermi('manage:policy:list')")
    @GetMapping("/list")
    public TableDataInfo list(TbPolicy tbPolicy)
    {
        startPage();
        List<TbPolicy> list = tbPolicyService.selectTbPolicyList(tbPolicy);
        return getDataTable(list);
    }

    /**
     * 导出策略列表
     */
    @PreAuthorize("@ss.hasPermi('manage:policy:export')")
    @Log(title = "策略", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TbPolicy tbPolicy)
    {
        List<TbPolicy> list = tbPolicyService.selectTbPolicyList(tbPolicy);
        ExcelUtil<TbPolicy> util = new ExcelUtil<TbPolicy>(TbPolicy.class);
        util.exportExcel(response, list, "策略数据");
    }

    /**
     * 获取策略详细信息
     */
    @PreAuthorize("@ss.hasPermi('manage:policy:query')")
    @GetMapping(value = "/{policyId}")
    public AjaxResult getInfo(@PathVariable("policyId") Long policyId)
    {
        return success(tbPolicyService.selectTbPolicyByPolicyId(policyId));
    }

    /**
     * 新增策略
     */
    @PreAuthorize("@ss.hasPermi('manage:policy:add')")
    @Log(title = "策略", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TbPolicy tbPolicy)
    {
        return toAjax(tbPolicyService.insertTbPolicy(tbPolicy));
    }

    /**
     * 修改策略
     */
    @PreAuthorize("@ss.hasPermi('manage:policy:edit')")
    @Log(title = "策略", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TbPolicy tbPolicy)
    {
        return toAjax(tbPolicyService.updateTbPolicy(tbPolicy));
    }

    /**
     * 删除策略
     */
    @PreAuthorize("@ss.hasPermi('manage:policy:remove')")
    @Log(title = "策略", businessType = BusinessType.DELETE)
	@DeleteMapping("/{policyIds}")
    public AjaxResult remove(@PathVariable Long[] policyIds)
    {
        return toAjax(tbPolicyService.deleteTbPolicyByPolicyIds(policyIds));
    }
}
