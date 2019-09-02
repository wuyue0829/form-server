package com.tssk.form.business;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tssk.form.consts.FormConsts;
import com.tssk.fw.business.base.Business;
import com.tssk.fw.business.base.BusinessMethod;
import com.tssk.fw.business.bean.BusinessConst;
import com.tssk.fw.business.bean.BusinessRequest;
import com.tssk.fw.business.bean.BusinessResponse;
import com.tssk.fw.business.utils.BusinessUtils;
import com.tssk.fw.dao.bean.PageObj;
import com.tssk.fw.dao.bean.QueryBean;
import com.tssk.fw.dao.runner.SqlRunner;
import com.tssk.fw.dao.utils.DaoUtils;
import com.tssk.fw.service.utils.FwUtils;
import com.tssk.fw.utils.collection.CollectionUtils;
import com.tssk.fw.utils.date.DateUtils;
import com.tssk.fw.utils.lang.LangUtils;
import com.tssk.fw.utils.lang.StringUtils;

@Business(name = "FormTemplateMainBaseBusiness")
public class FormTemplateMainBaseBusiness {

	public SqlRunner getSqlRunner() {
		SqlRunner sqlRunner = DaoUtils.getSqlRunnerManger().getSqlRunner(FormConsts.DB_NAME);
		return sqlRunner;
	}

	@BusinessMethod
	public BusinessResponse add(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		// 获取用户所属租户编码
		String appCode = BusinessUtils.getCurrentAppCode(request);
		if (!StringUtils.isEmpty(appCode)) {
			// 获取参数
			String roleType = StringUtils.trimNull(request.getExt("roleType"));
			String busiGroup1 = StringUtils.trimNull(request.getExt("busiGroup1"));
			String busiGroup2 = StringUtils.trimNull(request.getExt("busiGroup2"));
			String busiGroup3 = StringUtils.trimNull(request.getExt("busiGroup3"));
			String classifyId = StringUtils.trimNull(request.getData("classifyId"));
			String formName = StringUtils.trimNull(request.getData("formName"));
			String status = StringUtils.trimNull(request.getData("status"), "1");
			long rowSort = LangUtils.parseLong(request.getData("rowSort"), 1000);
			String remarks = StringUtils.trimNull(request.getData("remarks"));
			// 参数验证
			if (!StringUtils.isEmpty(busiGroup1) && busiGroup1.length() > 64) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "busiGroup1学校不能超过64个字符！");
			} else if (!StringUtils.isEmpty(busiGroup2) && busiGroup2.length() > 64) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "busiGroup2学院不能超过64个字符！");
			} else if (!StringUtils.isEmpty(busiGroup3) && busiGroup3.length() > 64) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "busiGroup3专业不能超过64个字符！");
			} else if (StringUtils.isEmpty(classifyId)) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "分类id不能为空！");
			} else if (classifyId.length() > 20) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "分类编号不能超过20个字符！");
			} else if (StringUtils.isEmpty(formName)) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "表单名称不能为空！");
			} else if (formName.length() > 64) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "表单名称不能超过64个字符！");
			} else if (!"1".equals(status) && !"2".equals(status)) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "状态值非法(1 启用，2 禁用)！");
			} else if (rowSort < 0 || rowSort > 9999999999l) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "排序必须是0-9999999999");
			} else if (!StringUtils.isEmpty(remarks) && remarks.length() > 500) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "备注不能超过500个字符！");
			} else {
				String sql = "select * from form_classify where id=? ";
				Map<String, Object> classifyMap = this.getSqlRunner().queryBySql(request, sql, classifyId);
				if (classifyMap != null) {
					// 准备数据
					long id = FwUtils.getIdService().nextId();
					String createUser = BusinessUtils.getCurrentUserCode(request);
					String createTime = DateUtils.getTime();
					String lastUser = BusinessUtils.getCurrentUserCode(request);
					String lastTime = DateUtils.getTime();
					Map<String, Object> insertMap = new HashMap<String, Object>();
					String classifyCode = StringUtils.trimNull(classifyMap.get("classifyCode"));
					String classifyName = StringUtils.trimNull(classifyMap.get("classifyName"));
					insertMap.put("id", id);
					insertMap.put("appCode", appCode);
					insertMap.put("busiGroup1", busiGroup1);
					insertMap.put("busiGroup2", busiGroup2);
					insertMap.put("busiGroup3", busiGroup3);
					insertMap.put("classifyId", classifyId);
					insertMap.put("classifyCode", classifyCode);
					insertMap.put("classifyName", classifyName);
					insertMap.put("formType", "3");
					insertMap.put("roleType", roleType);
					insertMap.put("formName", formName);
					insertMap.put("status", status);
					insertMap.put("rowSort", rowSort);
					insertMap.put("remarks", remarks);
					insertMap.put("createUser", createUser);
					insertMap.put("createTime", createTime);
					insertMap.put("lastUser", lastUser);
					insertMap.put("lastTime", lastTime);
					int num = this.getSqlRunner().insert(request, "form_template_main_base", insertMap);
					if (num < 1) {
						response.setCodeAndMsg(BusinessConst.CODE_UNKNOW, "添加失败，请稍后重试！");
					} else {
						Map<String, Object> rsMap = new HashMap<>();
						rsMap.put("id", id);
						response.setData(rsMap);
					}
				}
			}
		} else {
			response.setCodeAndMsg(BusinessConst.CODE_AUTH, "请先登录系统！");
		}
		return response;
	}

	@BusinessMethod
	public BusinessResponse modify(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		// 获取用户所属租户编码
		String appCode = BusinessUtils.getCurrentAppCode(request);
		if (!StringUtils.isEmpty(appCode)) {
			// 获取参数
			String id = StringUtils.trimNull(request.getData("id"));

			String formName = StringUtils.trimNull(request.getData("formName"));
			String status = StringUtils.trimNull(request.getData("status"));
			long rowSort = LangUtils.parseLong(request.getData("rowSort"), 1000);
			String remarks = StringUtils.trimNull(request.getData("remarks"));
			// 参数验证
			if (StringUtils.isEmpty(id)) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "警告：非法操作！");
			} else if (StringUtils.isEmpty(formName)) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "表单名称不能为空！");
			} else if (formName.length() > 64) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "表单名称不能超过64个字符！");
			} else if (!"1".equals(status) && !"2".equals(status)) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "状态值非法(1 启用，2 禁用)！");
			} else if (rowSort < 0 || rowSort > 9999999999l) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "排序必须是0-9999999999");
			} else if (!StringUtils.isEmpty(remarks) && remarks.length() > 500) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "备注不能超过500个字符！");
			} else {
				// 准备数据
				String lastUser = BusinessUtils.getCurrentUserCode(request);
				String lastTime = DateUtils.getTime();
				Map<String, Object> updateMap = new HashMap<>();
				updateMap.put("id", id);
				updateMap.put("formName", formName);
				updateMap.put("status", status);
				updateMap.put("rowSort", rowSort);
				updateMap.put("remarks", remarks);
				updateMap.put("lastUser", lastUser);
				updateMap.put("lastTime", lastTime);
				int num = this.getSqlRunner().update(request, "form_template_main_base", updateMap);
				if (num < 1) {
					response.setCodeAndMsg(BusinessConst.CODE_PARAM, "数据可能已经被删除，请刷新后重试！");
				}
			}
		} else {
			response.setCodeAndMsg(BusinessConst.CODE_AUTH, "请先登录系统！");
		}
		return response;
	}

	@BusinessMethod
	public BusinessResponse remove(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		// 获取用户所属租户编码
		String appCode = BusinessUtils.getCurrentAppCode(request);
		if (!StringUtils.isEmpty(appCode)) {
			String id = StringUtils.trimNull(request.getData("id"));
			List<String> ids = CollectionUtils.valueOfList(id);
			if (ids.size() > 0) {
				// 删除数据
				int num = this.getSqlRunner().delete(request, "form_template_main_base", ids);
				if (num < 1) {
					response.setCodeAndMsg(BusinessConst.CODE_PARAM, "数据可能已经被删除，请核对！");
				}
			}
		} else {
			response.setCodeAndMsg(BusinessConst.CODE_AUTH, "请先登录系统！");
		}
		return response;
	}

	@BusinessMethod
	public BusinessResponse query(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		// 获取用户所属租户编码
		String appCode = BusinessUtils.getCurrentAppCode(request);
		if (!StringUtils.isEmpty(appCode)) {
			String id = StringUtils.trimNull(request.getData("id"));
			String sql = "select * from form_template_main_base where id=?";
			Map<String, Object> map = this.getSqlRunner().queryBySql(request, sql, id);
			if (map != null) {
				response.setData(map);
			} else {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "数据可能已经被删除，请核对！");
			}
		} else {
			response.setCodeAndMsg(BusinessConst.CODE_AUTH, "请先登录系统！");
		}
		return response;
	}

	@BusinessMethod
	public BusinessResponse listAll(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		// 获取用户所属租户编码
		String appCode = BusinessUtils.getCurrentAppCode(request);
		if (!StringUtils.isEmpty(appCode)) {
			String classifyId = StringUtils.trimNull(request.getData("classifyId"));
			String keyword = StringUtils.trimNull(request.getData("keyword"));
			String busiGroup1 = StringUtils.trimNull(request.getData("busiGroup1"));
			String busiGroup2 = StringUtils.trimNull(request.getData("busiGroup2"));
			String busiGroup3 = StringUtils.trimNull(request.getData("busiGroup3"));
			String busiGroup4 = StringUtils.trimNull(request.getData("busiGroup4"));
			QueryBean queryBean = new QueryBean(request);
			queryBean.addConditionEquals("classifyId", classifyId);
			queryBean.addConditionLike("formName",keyword);
			queryBean.addConditionEquals("classifyId", classifyId);
			queryBean.addConditionEquals("appCode", appCode);
			queryBean.addConditionEquals("busiGroup1", "xuexiaobiaozhi");
			if (!StringUtils.isEmpty(busiGroup1)) {
				queryBean.addConditionEquals("busiGroup1", busiGroup1);
			}
			if (!StringUtils.isEmpty(busiGroup2)) {
				queryBean.addConditionEquals("busiGroup2", busiGroup2);
			}
			if (!StringUtils.isEmpty(busiGroup3)) {
				queryBean.addConditionEquals("busiGroup3", busiGroup3);
			}
			if (!StringUtils.isEmpty(busiGroup4)) {
				queryBean.addConditionEquals("busiGroup4", busiGroup4);
			}
			queryBean.setTableName("form_template_main_base");
			queryBean.setOrderStr("rowSort asc");
			List<Map<String, Object>> pageObj = getSqlRunner().list(request, queryBean);
			response.setData(pageObj);
		} else {
			response.setCodeAndMsg(BusinessConst.CODE_AUTH, "请先登录系统！");
		}
		return response;
	}

	@BusinessMethod
	public BusinessResponse listPage(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		// 获取用户所属租户编码
		String appCode = BusinessUtils.getCurrentAppCode(request);
		if (!StringUtils.isEmpty(appCode)) {
			String busiGroup1 = StringUtils.trimNull(request.getData("busiGroup1"));
			String busiGroup2 = StringUtils.trimNull(request.getData("busiGroup2"));
			String busiGroup3 = StringUtils.trimNull(request.getData("busiGroup3"));
			String busiGroup4 = StringUtils.trimNull(request.getData("busiGroup4"));
			QueryBean queryBean = new QueryBean(request);
			queryBean.addConditionEquals("appCode", appCode);
			if (!StringUtils.isEmpty(busiGroup1)) {
				queryBean.addConditionEquals("busiGroup1", busiGroup1);
			}
			if (!StringUtils.isEmpty(busiGroup2)) {
				queryBean.addConditionEquals("busiGroup2", busiGroup2);
			}


			if (!StringUtils.isEmpty(busiGroup3)) {
				queryBean.addConditionEquals("busiGroup3", busiGroup3);
			}
			if (!StringUtils.isEmpty(busiGroup4)) {
				queryBean.addConditionEquals("busiGroup4", busiGroup4);
			}
			queryBean.setTableName("form_template_main_base");
			queryBean.setOrderStr("rowSort asc");
			PageObj<List<Map<String, Object>>> pageObj = getSqlRunner().listPage(request, queryBean);
			response.setData(pageObj);
		} else {
			response.setCodeAndMsg(BusinessConst.CODE_AUTH, "请先登录系统！");
		}
		return response;
	}

}
