package com.tssk.form.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.tssk.form.consts.FormConsts;
import com.tssk.fw.business.base.Business;
import com.tssk.fw.business.base.BusinessMethod;
import com.tssk.fw.business.bean.BusinessConst;
import com.tssk.fw.business.bean.BusinessRequest;
import com.tssk.fw.business.bean.BusinessResponse;
import com.tssk.fw.business.utils.BusinessUtils;
import com.tssk.fw.dao.bean.PageObj;
import com.tssk.fw.dao.runner.SqlRunner;
import com.tssk.fw.dao.utils.DaoUtils;
import com.tssk.fw.service.utils.FwUtils;
import com.tssk.fw.utils.collection.CollectionUtils;
import com.tssk.fw.utils.date.DateUtils;
import com.tssk.fw.utils.lang.LangUtils;
import com.tssk.fw.utils.lang.StringUtils;

@Business(name = "FormClassifyBusiness")
public class FormClassifyBusiness {

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
			String busiGroup1 = StringUtils.trimNull(request.getExt("busiGroup1"),null);
			String busiGroup2 = StringUtils.trimNull(request.getExt("busiGroup2"),null);
			String busiGroup3 = StringUtils.trimNull(request.getExt("busiGroup3"),null);
			String classifyCode = StringUtils.trimNull(request.getData("classifyCode"));
			String classifyName = StringUtils.trimNull(request.getData("classifyName"));
			int flag = LangUtils.parseInt(request.getData("flag"), 3);
			String status = StringUtils.trimNull(request.getData("status"),"1");
			long rowSort = LangUtils.parseLong(request.getData("rowSort"), 1000);
			String remarks = StringUtils.trimNull(request.getData("remarks"));
			// 参数验证
			if (StringUtils.isEmpty(roleType)) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "角色类型不能为空！");
			} else if (!"1".equals(roleType) && !"2".equals(roleType) && !"3".equals(roleType) && !"4".equals(roleType)) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "角色类型值非法！");
			} else if(StringUtils.isEmpty(busiGroup1) && StringUtils.isEmpty(busiGroup2) && StringUtils.isEmpty(busiGroup3)){
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "业务分组busiGroup1(学校)、busiGroup2(学院)、busiGroup3(专业)至少一个不为空！");
			} else if (!StringUtils.isEmpty(busiGroup1) && busiGroup1.length() > 64) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "busiGroup1学校不能超过64个字符！");
			} else if (!StringUtils.isEmpty(busiGroup2) && busiGroup2.length() > 64) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "busiGroup2学院不能超过64个字符！");
			} else if (!StringUtils.isEmpty(busiGroup3) && busiGroup3.length() > 64) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "busiGroup3专业不能超过64个字符！");
			} else if (rowSort < 0 || rowSort > 9999999999l) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "排序必须是0-9999999999");
			} else if (!StringUtils.isEmpty(remarks) && remarks.length() > 500) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "备注不能超过500个字符！");
			} else {
				// 准备数据
				long id = FwUtils.getIdService().nextId();
				long createUser = BusinessUtils.getCurrentUserId(request);
				String createTime = DateUtils.getTime();
				long lastUser = BusinessUtils.getCurrentUserId(request);
				String lastTime = DateUtils.getTime();
				Map<String, Object> insertMap = new HashMap<String, Object>();
				insertMap.put("id", id);
				insertMap.put("appCode", appCode);
				insertMap.put("roleType", roleType);
				insertMap.put("busiGroup1", busiGroup1);
				insertMap.put("busiGroup2", busiGroup2);
				insertMap.put("busiGroup3", busiGroup3);
				insertMap.put("classifyCode", classifyCode);
				insertMap.put("classifyName", classifyName);
				insertMap.put("flag", flag);
				insertMap.put("status", status);
				insertMap.put("rowSort", rowSort);
				insertMap.put("remarks", remarks);
				insertMap.put("createUser", createUser);
				insertMap.put("createTime", createTime);
				insertMap.put("lastUser", lastUser);
				insertMap.put("lastTime", lastTime);
				int num = this.getSqlRunner().insert(request, "form_classify", insertMap);
				if (num < 1) {
					response.setCodeAndMsg(BusinessConst.CODE_UNKNOW, "添加失败，请稍后重试！");
				}else {
					Map<String,Object> rsMap = new HashMap<String,Object>();
					rsMap.put("id", id);
					response.setData(rsMap);
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
			String classifyCode = StringUtils.trimNull(request.getData("classifyCode"));
			String classifyName = StringUtils.trimNull(request.getData("classifyName"));
			int rowSort = LangUtils.parseInt(request.getData("rowSort"), 1000);
			String remarks = StringUtils.trimNull(request.getData("remarks"));
			// 参数验证
			if (StringUtils.isEmpty(id)) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "警告：非法操作！");
			} /*else if (StringUtils.isEmpty(classifyCode)) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "分类编号不能为空！");
			} else if (classifyCode.length() > 16) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "分类编号不能超过16个字符！");
			} else if (StringUtils.isEmpty(classifyName)) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "分类名称不能为空！");
			} else if (classifyName.length() > 20) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "分类名称不能超过20个字符！");
			} else if (rowSort < 0 || rowSort > 9999999999l) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "排序必须是0-9999999999");
			}*/ else if (!StringUtils.isEmpty(remarks) && remarks.length() > 500) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "备注不能超过500个字符！");
			} else {
				// 准备数据
				long lastUser = BusinessUtils.getCurrentUserId(request);
				String lastTime = DateUtils.getTime();
				Map<String, Object> updateMap = new HashMap<>();
				updateMap.put("id", id);
				updateMap.put("classifyCode", classifyCode);
				updateMap.put("classifyName", classifyName);
				updateMap.put("rowSort", rowSort);
				updateMap.put("remarks", remarks);
				updateMap.put("lastUser", lastUser);
				updateMap.put("lastTime", lastTime);
				int num = this.getSqlRunner().update(request, "form_classify", updateMap);
				if (num < 1) {
					response.setCodeAndMsg(BusinessConst.CODE_PARAM, "数据可能已经被删除，请刷新后重试！");
				}
			}
		} else {
			response.setCodeAndMsg(BusinessConst.CODE_AUTH, "请先登录系统！");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	@BusinessMethod
	public BusinessResponse remove(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		// 获取用户所属租户编码
		String appCode = BusinessUtils.getCurrentAppCode(request);
		if (!StringUtils.isEmpty(appCode)) {
			String id = StringUtils.trimNull(request.getData("id"));
			List<String> ids = CollectionUtils.valueOfList(id);
			if (ids != null && ids.size() > 0) {
				// 验证分类下是否有表单
				Map<String, Object> sqlForInMap = this.getSqlForIn(ids);
				String validateRes = "";
				if (sqlForInMap != null && sqlForInMap.size() > 0) {
					String sqlForIn = StringUtils.trimNull(sqlForInMap.get("sqlForIn"));
					List<Object> sqlForInList = (List<Object>) (sqlForInMap.get("paramList"));
					Object[] objs = new Object[sqlForInList.size()];
					for (int i = 0; i < sqlForInList.size(); i++) {
						objs[i] = sqlForInList.get(i);
					}
					String sql = "SELECT a.classify_name,b.form_name FROM form_classify a,form_main_base b WHERE a.id=b.classify_id and a.id in("
							+ sqlForIn + ") order by a.classify_name,b.form_name";
					List<Map<String, Object>> list = this.getSqlRunner().listBySql(request, sql, objs);
					if (list != null && list.size() > 0) {
						Map<String, String> msgMap = new HashMap<String, String>();
						for (int i = 0; i < list.size(); i++) {
							Map<String, Object> map = list.get(i);
							if (map != null && map.size() > 0) {
								String classifyName = StringUtils.trimNull(map.get("classifyName"));
								String formName = StringUtils.trimNull(map.get("formName"));
								String msg = msgMap.get(classifyName);
								if (msg == null) {
									msgMap.put(classifyName, formName);
								} else {
									msgMap.put(classifyName, msg + "," + formName);
								}
							}
						}
						if (msgMap.size() > 0) {
							for (Entry<String, String> entry : msgMap.entrySet()) {
								if (StringUtils.isEmpty(validateRes)) {
									validateRes += entry.getKey() + "(问卷：" + entry.getValue() + ")";
								} else {
									validateRes += "，" + entry.getKey() + "(问卷：" + entry.getValue() + ")";
								}
							}
						}
					}
				}
				if (StringUtils.isEmpty(validateRes)) {
					// 删除数据
					int num = this.getSqlRunner().delete(request, "form_classify", ids);
					if (num < 1) {
						response.setCodeAndMsg(BusinessConst.CODE_PARAM, "数据可能已经被删除，请核对！");
					}
				} else {
					response.setCodeAndMsg(BusinessConst.CODE_PARAM, "请先删除如下分类下的问卷：" + validateRes);
				}
			} else {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "警告：非法操作！");
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
			String sql = "select * from form_classify where id = ? ";
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
	public BusinessResponse listPage(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		// 获取用户所属租户编码
		String appCode = BusinessUtils.getCurrentAppCode(request);
		if (!StringUtils.isEmpty(appCode)) {
			Integer pageSize = LangUtils.parseInt(StringUtils.trimNull(request.getData("pageSize")),-1);
			int page = LangUtils.parseInt(StringUtils.trimNull(request.getData("page")),-1);
			if(pageSize<0 || page<0){
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "分页参数page,pageSize错误！");
			}else{
				int startRow = page*pageSize;
				String userCode = BusinessUtils.getCurrentUserCode(request);
				String roleType = StringUtils.trimNull(request.getExt("roleType"));
				String busiGroup1 = StringUtils.trimNull(request.getExt("busiGroup1"),null);
				String busiGroup2 = StringUtils.trimNull(request.getExt("busiGroup2"),null);
				String busiGroup3 = StringUtils.trimNull(request.getExt("busiGroup3"),null);
				int flag = LangUtils.parseInt(request.getData("flag"), 3);
				int status = 1;
				String keyWord = StringUtils.trimNull(request.getData("keyWord"));
				StringBuilder sb = new StringBuilder();
				List<Object> paramList = new ArrayList<Object>();
				sb.append(" select * from form_classify where app_code=? ");
				paramList.add(appCode);
				if("1".equals(roleType)){
					sb.append(" and busi_group1=? and (busi_group2 is null or busi_group2='') and (busi_group3 is null or busi_group3='') ");
					paramList.add(busiGroup1);
				}else if("2".equals(roleType)){
					sb.append(" busi_group1=? and busi_group2=? ");
					paramList.add(busiGroup1);
					paramList.add(busiGroup2);
				}else if("3".equals(roleType)){
					sb.append(" busi_group1=? and busi_group2=? and busi_group3=? ");
					paramList.add(busiGroup1);
					paramList.add(busiGroup2);
					paramList.add(busiGroup3);
				}else if("4".equals(roleType)){
					sb.append(" busi_group1=? and busi_group2=? and busi_group3=? and create_user=? ");
					paramList.add(busiGroup1);
					paramList.add(busiGroup2);
					paramList.add(busiGroup3);
					paramList.add(userCode);
				}
				sb.append(" and flag=? and status=? ");
				paramList.add(flag);
				paramList.add(status);
				if (!StringUtils.isEmpty(keyWord)) {
					sb.append(" and classify_name like ? ");
					paramList.add("%"+keyWord+"%");
				}
				sb.append(" order by row_sort asc ");
				String sql = sb.toString();
				PageObj<List<Map<String, Object>>> pageObj = getSqlRunner().listPageBySql(request, sql, startRow, pageSize, paramList.toArray());
				response.setData(pageObj);
			}
		} else {
			response.setCodeAndMsg(BusinessConst.CODE_AUTH, "请先登录系统！");
		}
		return response;
	}
	
	@BusinessMethod
	public BusinessResponse listAll(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		String appCode = BusinessUtils.getCurrentAppCode(request);
		if (!StringUtils.isEmpty(appCode)) {
			String userCode = BusinessUtils.getCurrentUserCode(request);
			String roleType = StringUtils.trimNull(request.getExt("roleType"));
			String busiGroup1 = StringUtils.trimNull(request.getExt("busiGroup1"),null);
			String busiGroup2 = StringUtils.trimNull(request.getExt("busiGroup2"),null);
			String busiGroup3 = StringUtils.trimNull(request.getExt("busiGroup3"),null);
			int flag = LangUtils.parseInt(request.getData("flag"), 3);
			int status = 1;
			StringBuilder sb = new StringBuilder();
			List<Object> paramList = new ArrayList<Object>();
			sb.append(" select * from form_classify where app_code=? ");
			paramList.add(appCode);
			if("1".equals(roleType)){
				sb.append(" and busi_group1=? and (busi_group2 is null or busi_group2='') and (busi_group3 is null or busi_group3='') ");
				paramList.add(busiGroup1);
			}else if("2".equals(roleType)){
				sb.append(" busi_group1=? and busi_group2=? ");
				paramList.add(busiGroup1);
				paramList.add(busiGroup2);
			}else if("3".equals(roleType)){
				sb.append(" busi_group1=? and busi_group2=? and busi_group3=? ");
				paramList.add(busiGroup1);
				paramList.add(busiGroup2);
				paramList.add(busiGroup3);
			}else if("4".equals(roleType)){
				sb.append(" busi_group1=? and busi_group2=? and busi_group3=? and create_user=? ");
				paramList.add(busiGroup1);
				paramList.add(busiGroup2);
				paramList.add(busiGroup3);
				paramList.add(userCode);
			}
			sb.append(" and flag=? and status=? order by row_sort asc ");
			paramList.add(flag);
			paramList.add(status);
			String sql = sb.toString();
			List<Map<String, Object>> pageObj = getSqlRunner().listBySql(request, sql, paramList.toArray());
			response.setData(pageObj);
		} else {
			response.setCodeAndMsg(BusinessConst.CODE_AUTH, "请先登录系统！");
		}
		return response;
	}

	private Map<String, Object> getSqlForIn(List<String> list) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (list != null && list.size() > 0) {
			String value = "";
			List<Object> paramList = new ArrayList<Object>();
			for (int i = 0; i < list.size(); i++) {
				value += ",?";
				paramList.add(list.get(i));
			}
			value = value.substring(1);
			map.put("sqlForIn", value);
			map.put("paramList", paramList);
		}
		return map;
	}

}
