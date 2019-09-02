package com.tssk.form.business;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import com.tssk.form.consts.FormConsts;
import com.tssk.form.utils.question.MailUtil;
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
import com.tssk.fw.http.netty.NettyHttpUtils;
import com.tssk.fw.service.utils.FwUtils;
import com.tssk.fw.utils.collection.CollectionUtils;
import com.tssk.fw.utils.date.DateUtils;
import com.tssk.fw.utils.lang.LangUtils;
import com.tssk.fw.utils.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

@Business(name = "FormMainBaseBusiness")
public class FormMainBaseBusiness {

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
			String busiGroup1 = StringUtils.trimNull(request.getExt("busiGroup1"), null);
			String busiGroup2 = StringUtils.trimNull(request.getExt("busiGroup2"), null);
			String busiGroup3 = StringUtils.trimNull(request.getExt("busiGroup3"), null);
			String classifyId = StringUtils.trimNull(request.getData("classifyId"));
			String formName = StringUtils.trimNull(request.getData("formName"));
			int formType = 3;
			String startTime = StringUtils.trimNull(request.getData("startTime"));
			String endTime = StringUtils.trimNull(request.getData("endTime"));
			int status = 1;
			long rowSort = LangUtils.parseLong(request.getData("rowSort"), 1000);
			String remarks = StringUtils.trimNull(request.getData("remarks"));
			String endWord = StringUtils.trimNull(request.getData("endWord"));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date startDate = null;
			Date endDate = null;
			try {
				startDate = sdf.parse(startTime);
			} catch (Exception e) {
			}
			try {
				endDate = sdf.parse(endTime);
			} catch (Exception e) {
			}
			// 参数验证
			if (StringUtils.isEmpty(roleType)) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "角色类型不能为空！");
			} else if (!"1".equals(roleType) && !"2".equals(roleType) && !"3".equals(roleType)
					&& !"4".equals(roleType)) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "角色类型值非法！");
			} else if (StringUtils.isEmpty(busiGroup1) && StringUtils.isEmpty(busiGroup2)
					&& StringUtils.isEmpty(busiGroup3)) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM,
						"业务分组busiGroup1(学校)、busiGroup2(学院)、busiGroup3(专业)至少一个不为空！");
			} else if (!StringUtils.isEmpty(busiGroup1) && busiGroup1.length() > 64) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "busiGroup1学校不能超过64个字符！");
			} else if (!StringUtils.isEmpty(busiGroup2) && busiGroup2.length() > 64) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "busiGroup2学院不能超过64个字符！");
			} else if (!StringUtils.isEmpty(busiGroup3) && busiGroup3.length() > 64) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "busiGroup3专业不能超过64个字符！");
			} else if (StringUtils.isEmpty(classifyId) || "0".equals(classifyId)) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "分类id不能为空！");
			} else if (StringUtils.isEmpty(formName)) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "表单名称不能为空！");
			} else if (formName.length() > 64) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "表单名称不能超过64个字符！");
			} else if (!StringUtils.isEmpty(startTime) && startDate == null) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "开始时间格式错误，正确格式如：2019-08-01 10:30:00！");
			} else if (!StringUtils.isEmpty(endTime) && endDate == null) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "结束时间格式错误，正确格式如：2019-08-01 10:30:00！");
			} else if (rowSort < 0 || rowSort > 9999999999l) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "排序必须是0-9999999999");
			} else if (!StringUtils.isEmpty(remarks) && remarks.length() > 500) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "备注不能超过500个字符！");
			} else if (!StringUtils.isEmpty(endWord) && endWord.length() > 256) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "结束描述不能超过256个字符！");
			} else {
				// 获取分类数据
				String sql = "select * from form_classify where id=? ";
				Map<String, Object> classifyMap = this.getSqlRunner().queryBySql(request, sql, classifyId);
				if (classifyMap != null) {
					// 准备数据
					long id = FwUtils.getIdService().nextId();
					Object classifyCode = classifyMap.get("classifyCode");
					Object classifyName = classifyMap.get("classifyName");
					String createUser = BusinessUtils.getCurrentUserCode(request);
					String createTime = DateUtils.getTime();
					String lastUser = BusinessUtils.getCurrentUserCode(request);
					String lastTime = DateUtils.getTime();
					Map<String, Object> insertMap = new HashMap<String, Object>();
					insertMap.put("id", id);
					insertMap.put("appCode", appCode);
					insertMap.put("roleType", roleType);
					insertMap.put("busiGroup1", busiGroup1);
					insertMap.put("busiGroup2", busiGroup2);
					insertMap.put("busiGroup3", busiGroup3);
					insertMap.put("classifyId", classifyId);
					insertMap.put("classifyCode", classifyCode);
					insertMap.put("classifyName", classifyName);
					insertMap.put("formName", formName);
					insertMap.put("formType", formType);
					insertMap.put("startTime", startDate);
					insertMap.put("endTime", endDate);
					insertMap.put("status", status);
					insertMap.put("rowSort", rowSort);
					insertMap.put("remarks", remarks);
					insertMap.put("endWord", endWord);
					insertMap.put("createUser", createUser);
					insertMap.put("createTime", createTime);
					insertMap.put("lastUser", lastUser);
					insertMap.put("lastTime", lastTime);
					int num = this.getSqlRunner().insert(request, "form_main_base", insertMap);
					if (num < 1) {
						response.setCodeAndMsg(BusinessConst.CODE_UNKNOW, "添加失败，请稍后重试！");
					} else {
						Map<String, Object> rsMap = new HashMap<>();
						rsMap.put("id", id);
						response.setData(rsMap);
					}
				} else {
					response.setCodeAndMsg(BusinessConst.CODE_PARAM, "指定的分类数据不存在，请核对！");
				}
			}
		} else {
			response.setCodeAndMsg(BusinessConst.CODE_AUTH, "请先登录系统！");
		}
		return response;
	}

	// 毕业要求达成评价问卷
	@BusinessMethod
	public BusinessResponse addRequirementsMet(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		// 获取用户所属租户编码
		String appCode = BusinessUtils.getCurrentAppCode(request);
		if (!StringUtils.isEmpty(appCode)) {
			// 获取参数
			String roleType = StringUtils.trimNull(request.getExt("roleType"));
			String busiGroup1 = StringUtils.trimNull(request.getExt("busiGroup1"), null);
			String busiGroup2 = StringUtils.trimNull(request.getExt("busiGroup2"), null);
			String busiGroup3 = StringUtils.trimNull(request.getExt("busiGroup3"), null);
			String formName = StringUtils.trimNull(request.getData("formName"));
			String ext1 = StringUtils.trimNull(request.getData("ext1"));
			String ext2 = StringUtils.trimNull(request.getData("ext2"));
			String autoSend = StringUtils.trimNull(request.getData("autoSend"));
			int formType = 1;
			String startTime = StringUtils.trimNull(request.getData("startTime"));
			String endTime = StringUtils.trimNull(request.getData("endTime"));
			int status = 1;
			long rowSort = LangUtils.parseLong(request.getData("rowSort"), 1000);
			String remarks = StringUtils.trimNull(request.getData("remarks"));
			String endWord = StringUtils.trimNull(request.getData("endWord"));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date startDate = null;
			Date endDate = null;
			try {
				startDate = sdf.parse(startTime);
			} catch (Exception e) {
			}
			try {
				endDate = sdf.parse(endTime);
			} catch (Exception e) {
			}
			// 参数验证
			if (StringUtils.isEmpty(roleType)) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "角色类型不能为空！");
			} else if (!"1".equals(roleType) && !"2".equals(roleType) && !"3".equals(roleType)
					&& !"4".equals(roleType)) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "角色类型值非法！");
			} else if (StringUtils.isEmpty(busiGroup1) && StringUtils.isEmpty(busiGroup2)
					&& StringUtils.isEmpty(busiGroup3)) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM,
						"业务分组busiGroup1(学校)、busiGroup2(学院)、busiGroup3(专业)至少一个不为空！");
			} else if (!StringUtils.isEmpty(busiGroup1) && busiGroup1.length() > 64) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "busiGroup1学校不能超过64个字符！");
			} else if (!StringUtils.isEmpty(busiGroup2) && busiGroup2.length() > 64) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "busiGroup2学院不能超过64个字符！");
			} else if (!StringUtils.isEmpty(busiGroup3) && busiGroup3.length() > 64) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "busiGroup3专业不能超过64个字符！");
			}  else if (StringUtils.isEmpty(formName)) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "表单名称不能为空！");
			} else if (formName.length() > 64) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "表单名称不能超过64个字符！");
			} else if (StringUtils.isEmpty(ext1)) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "培养方案版本不能为空！");
			} else if (StringUtils.isEmpty(ext2)) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "年级不能为空！");
			} else if (!StringUtils.isEmpty(startTime) && startDate == null) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "开始时间格式错误，正确格式如：2019-08-01 10:30:00！");
			} else if (!StringUtils.isEmpty(endTime) && endDate == null) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "结束时间格式错误，正确格式如：2019-08-01 10:30:00！");
			} else if (rowSort < 0 || rowSort > 9999999999l) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "排序必须是0-9999999999");
			} else if (!StringUtils.isEmpty(remarks) && remarks.length() > 500) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "备注不能超过500个字符！");
			} else if (!StringUtils.isEmpty(endWord) && endWord.length() > 256) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "结束描述不能超过256个字符！");
			} else {
				// 获取分类数据
				String sql = "select * from form_classify where id=? ";
				Map<String, Object> classifyMap = this.getSqlRunner().queryBySql(request, sql, "681570458688032768");
				String sqlqeryByStudent = "SELECT t.sys_user_id as num FROM user_student t WHERE t.grade = ?;";
				List<Map<String, Object>> stuNum = this.getSqlRunner().listBySql(request, sqlqeryByStudent, ext2.substring(0,ext2.length()-1));
				if (classifyMap != null) {
					// 准备数据
					long id = FwUtils.getIdService().nextId();
					Object classifyCode = classifyMap.get("classifyCode");
					Object classifyName = classifyMap.get("classifyName");
					String createUser = BusinessUtils.getCurrentUserCode(request);
					String createTime = DateUtils.getTime();
					String lastUser = BusinessUtils.getCurrentUserCode(request);
					String lastTime = DateUtils.getTime();
					Map<String, Object> insertMap = new HashMap<String, Object>();
					insertMap.put("id", id);
					insertMap.put("appCode", appCode);
					insertMap.put("roleType", roleType);
					insertMap.put("busiGroup1", busiGroup1);
					insertMap.put("busiGroup2", busiGroup2);
					insertMap.put("busiGroup3", busiGroup3);
					insertMap.put("classifyId", "681570458688032768");
					insertMap.put("classifyCode", classifyCode);
					insertMap.put("classifyName", classifyName);
					insertMap.put("formName", formName);
					insertMap.put("ext1", ext1);
					insertMap.put("ext2", ext2);
					insertMap.put("ext5", autoSend);
					insertMap.put("formType", formType);
					insertMap.put("sendNum", stuNum.size());
					insertMap.put("receiveNum", 0);
					insertMap.put("startTime", startDate);
					insertMap.put("endTime", endDate);
					insertMap.put("status", status);
					insertMap.put("rowSort", rowSort);
					insertMap.put("remarks", remarks);
					insertMap.put("endWord", endWord);
					insertMap.put("createUser", createUser);
					insertMap.put("createTime", createTime);
					insertMap.put("lastUser", lastUser);
					insertMap.put("lastTime", lastTime);

					for (Map<String, Object> map :stuNum){
						Map<String, Object> insertMap1 = new HashMap<String, Object>();
						insertMap1.put("id",FwUtils.getIdService().nextId());
						insertMap1.put("formId",id);
						insertMap1.put("status",3);
						insertMap1.put("createTime", createTime);
						insertMap1.put("sendUserId", map.get("num"));
						this.getSqlRunner().insert(request, "form_answer", insertMap1);
					}

					int num = this.getSqlRunner().insert(request, "form_main_base", insertMap);
					if (num < 1) {
						response.setCodeAndMsg(BusinessConst.CODE_UNKNOW, "添加失败，请稍后重试！");
					} else {
						Map<String, Object> rsMap = new HashMap<>();
						rsMap.put("formId", id);
						response.setData(rsMap);
					}
				} else {
					response.setCodeAndMsg(BusinessConst.CODE_PARAM, "指定的分类数据不存在，请核对！");
				}
			}
		} else {
			response.setCodeAndMsg(BusinessConst.CODE_AUTH, "请先登录系统！");
		}
		return response;
	}

	// 课程质量分析
	@BusinessMethod
	public BusinessResponse addCourseQuality(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		// 获取用户所属租户编码
		String appCode = BusinessUtils.getCurrentAppCode(request);
		if (!StringUtils.isEmpty(appCode)) {
			// 获取参数
			String roleType = StringUtils.trimNull(request.getExt("roleType"));
			String busiGroup1 = StringUtils.trimNull(request.getExt("busiGroup1"), null);
			String busiGroup2 = StringUtils.trimNull(request.getExt("busiGroup2"), null);
			String busiGroup3 = StringUtils.trimNull(request.getExt("busiGroup3"), null);
			String formName = StringUtils.trimNull(request.getData("formName"));
			String ext1 = StringUtils.trimNull(request.getData("ext1"));
			String ext2 = StringUtils.trimNull(request.getData("ext2"));
			String ext3 = StringUtils.trimNull(request.getData("ext3"));
			String ext4 = StringUtils.trimNull(request.getData("ext4"));
			int formType = 2;
			String startTime = StringUtils.trimNull(request.getData("startTime"));
			String endTime = StringUtils.trimNull(request.getData("endTime"));
			int status = 1;
			long rowSort = LangUtils.parseLong(request.getData("rowSort"), 1000);
			String remarks = StringUtils.trimNull(request.getData("remarks"));
			String endWord = StringUtils.trimNull(request.getData("endWord"));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date startDate = null;
			Date endDate = null;
			try {
				startDate = sdf.parse(startTime);
			} catch (Exception e) {
			}
			try {
				endDate = sdf.parse(endTime);
			} catch (Exception e) {
			}
			// 参数验证
			if (StringUtils.isEmpty(roleType)) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "角色类型不能为空！");
			} else if (!"1".equals(roleType) && !"2".equals(roleType) && !"3".equals(roleType)
					&& !"4".equals(roleType)) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "角色类型值非法！");
			} else if (StringUtils.isEmpty(busiGroup1) && StringUtils.isEmpty(busiGroup2)
					&& StringUtils.isEmpty(busiGroup3)) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM,
						"业务分组busiGroup1(学校)、busiGroup2(学院)、busiGroup3(专业)至少一个不为空！");
			} else if (!StringUtils.isEmpty(busiGroup1) && busiGroup1.length() > 64) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "busiGroup1学校不能超过64个字符！");
			} else if (!StringUtils.isEmpty(busiGroup2) && busiGroup2.length() > 64) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "busiGroup2学院不能超过64个字符！");
			} else if (!StringUtils.isEmpty(busiGroup3) && busiGroup3.length() > 64) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "busiGroup3专业不能超过64个字符！");
			} else if (StringUtils.isEmpty(formName)) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "表单名称不能为空！");
			} else if (formName.length() > 64) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "表单名称不能超过64个字符！");
			} else if (StringUtils.isEmpty(ext1)) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "培养方案版本不能为空！");
			} else if (StringUtils.isEmpty(ext2)) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "年级不能为空！");
			} else if (StringUtils.isEmpty(ext3)) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "评价课程不能为空！");
			} else if (!"1".equals(ext4) && !"2".equals(ext4)) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "是否开启评价提醒值非法(1:是,2:否)！");
			} else if (!StringUtils.isEmpty(startTime) && startDate == null) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "开始时间格式错误，正确格式如：2019-08-01 10:30:00！");
			} else if (!StringUtils.isEmpty(endTime) && endDate == null) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "结束时间格式错误，正确格式如：2019-08-01 10:30:00！");
			} else if (rowSort < 0 || rowSort > 9999999999l) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "排序必须是0-9999999999");
			} else if (!StringUtils.isEmpty(remarks) && remarks.length() > 500) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "备注不能超过500个字符！");
			} else if (!StringUtils.isEmpty(endWord) && endWord.length() > 256) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "结束描述不能超过256个字符！");
			} else {
				// 获取分类数据
				String sql = "select * from form_classify where id=? ";
				Map<String, Object> classifyMap = this.getSqlRunner().queryBySql(request, sql, "681570560928387072");
				String sqlqeryByStudent = "SELECT COUNT(t.id) AS num FROM user_student t WHERE t.grade = ?;";
				List<Map<String, Object>> stuNum = this.getSqlRunner().listBySql(request, sqlqeryByStudent, ext2.substring(0,ext2.length()-1));
				if (classifyMap != null) {
					// 准备数据
					long id = FwUtils.getIdService().nextId();
					Object classifyCode = classifyMap.get("classifyCode");
					Object classifyName = classifyMap.get("classifyName");
					String createUser = BusinessUtils.getCurrentUserCode(request);
					String createTime = DateUtils.getTime();
					String lastUser = BusinessUtils.getCurrentUserCode(request);
					String lastTime = DateUtils.getTime();
					Map<String, Object> insertMap = new HashMap<String, Object>();
					insertMap.put("id", id);
					insertMap.put("appCode", appCode);
					insertMap.put("roleType", roleType);
					insertMap.put("busiGroup1", busiGroup1);
					insertMap.put("busiGroup2", busiGroup2);
					insertMap.put("busiGroup3", busiGroup3);
					insertMap.put("classifyId", "681570560928387072");
					insertMap.put("classifyCode", classifyCode);
					insertMap.put("classifyName", classifyName);
					insertMap.put("formName", formName);
					insertMap.put("ext1", ext1);
					insertMap.put("ext2", ext2);
					insertMap.put("ext3", ext3);
					insertMap.put("ext4", ext4);
					insertMap.put("formType", formType);
					insertMap.put("startTime", startDate);
					insertMap.put("endTime", endDate);
					insertMap.put("status", status);
					insertMap.put("sendNum", stuNum.size());
					insertMap.put("receiveNum", 0);
					insertMap.put("rowSort", rowSort);
					insertMap.put("remarks", remarks);
					insertMap.put("endWord", endWord);
					insertMap.put("createUser", createUser);
					insertMap.put("createTime", createTime);
					insertMap.put("lastUser", lastUser);
					insertMap.put("lastTime", lastTime);
					for (Map<String, Object> map :stuNum){
						Map<String, Object> insertMap1 = new HashMap<String, Object>();
						insertMap1.put("id",FwUtils.getIdService().nextId());
						insertMap1.put("formId",id);
						insertMap1.put("status",3);
						insertMap1.put("createTime", createTime);
						insertMap1.put("sendUserId", map.get("num"));
						this.getSqlRunner().insert(request, "form_answer", insertMap1);
					}
					int num = this.getSqlRunner().insert(request, "form_main_base", insertMap);
					if (num < 1) {
						response.setCodeAndMsg(BusinessConst.CODE_UNKNOW, "添加失败，请稍后重试！");
					} else {
						Map<String, Object> rsMap = new HashMap<>();
						rsMap.put("formId", id);
						response.setData(rsMap);
					}
				} else {
					response.setCodeAndMsg(BusinessConst.CODE_PARAM, "指定的分类数据不存在，请核对！");
				}
			}
		} else {
			response.setCodeAndMsg(BusinessConst.CODE_AUTH, "请先登录系统！");
		}
		return response;
	}

	@BusinessMethod
	public BusinessResponse addByTemplate(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		// 获取用户所属租户编码
		String appCode = BusinessUtils.getCurrentAppCode(request);
		String roleType = StringUtils.trimNull(request.getExt("roleType"));
		String busiGroup1 = StringUtils.trimNull(request.getExt("busiGroup1"), null);
		String busiGroup2 = StringUtils.trimNull(request.getExt("busiGroup2"), null);
		String busiGroup3 = StringUtils.trimNull(request.getExt("busiGroup3"), null);
		if (!StringUtils.isEmpty(appCode)) {
			String templateId = StringUtils.trimNull(request.getData("templateId"));
			if (!StringUtils.isEmpty(templateId)) {
				String sql = "select * from form_template_main_base where id = ? and app_code = ? ";
				Map<String, Object> mainMap = getSqlRunner().queryBySql(request, sql, templateId, appCode);
				sql = "select * from form_template_main_grid where form_id = ? and app_code = ? ";
				List<Map<String, Object>> mainGrid = getSqlRunner().listBySql(request, sql, templateId, appCode);
				sql = "select * from form_template_question_base where form_id = ? and app_code = ? ";
				List<Map<String, Object>> questionList = getSqlRunner().listBySql(request, sql, templateId, appCode);
				sql = "select * from form_template_question_grid where form_id = ? and app_code = ? ";
				List<Map<String, Object>> questionGrid = getSqlRunner().listBySql(request, sql, templateId, appCode);
				if (mainMap != null && mainMap.size() > 0) {
					sql = "update form_template_main_base set ref_count = ref_count+1 where id = ? and app_code = ? ";
					getSqlRunner().execBySql(request, sql, templateId, appCode);
					long formId = FwUtils.getIdService().nextId();
					String createUser = BusinessUtils.getCurrentUserCode(request);
					String createTime = DateUtils.getTime();
					String lastUser = BusinessUtils.getCurrentUserCode(request);
					String lastTime = DateUtils.getTime();
					Map<String, Object> newMain = new HashMap<>();
					String classifyId = StringUtils.trimNull(mainMap.get("classifyId"));
					String classifyCode = StringUtils.trimNull(mainMap.get("classifyCode"));
					String classifyName = StringUtils.trimNull(mainMap.get("classifyName"));
					String formName = StringUtils.trimNull(mainMap.get("formName"));
					String remarks = StringUtils.trimNull(mainMap.get("remarks"));
					String endWord = StringUtils.trimNull(mainMap.get("endWord"));
					String formType = StringUtils.trimNull(mainMap.get("formType"));
					newMain.put("id", formId);
					newMain.put("appCode", appCode);
					newMain.put("classifyId", classifyId);
					newMain.put("classifyCode", classifyCode);
					newMain.put("classifyName", classifyName);
					newMain.put("formType", formType);
					newMain.put("formName", formName);
					newMain.put("templateId", templateId);
					newMain.put("roleType", roleType);
					newMain.put("busiGroup1", busiGroup1);
					newMain.put("busiGroup2", busiGroup2);
					newMain.put("busiGroup3", busiGroup3);
					newMain.put("remarks", remarks);
					newMain.put("endWord", endWord);
					newMain.put("createUser", createUser);
					newMain.put("createTime", createTime);
					newMain.put("lastUser", lastUser);
					newMain.put("lastTime", lastTime);
					int num = this.getSqlRunner().insert(request, "form_main_base", newMain);
					if (num > 0) {
						if (mainGrid != null && mainGrid.size() > 0) {
							for (int i = 0; i < mainGrid.size(); i++) {
								Map<String, Object> gridMap = mainGrid.get(i);
								if (gridMap != null && gridMap.size() > 0) {
									long gridId = FwUtils.getIdService().nextId();
									gridMap.put("id", gridId);
									gridMap.put("appCode", appCode);
									gridMap.put("formId", formId);
									num = this.getSqlRunner().insert(request, "form_main_grid", gridMap);
								}
							}
						}
						Map<String, Object> questionIdMap = new HashMap<String, Object>();
						if (questionList != null && questionList.size() > 0) {
							for (int i = 0; i < questionList.size(); i++) {
								Map<String, Object> questionMap = questionList.get(i);
								if (questionMap != null && questionMap.size() > 0) {
									long questionId = FwUtils.getIdService().nextId();
									questionIdMap.put(StringUtils.trimNull(questionMap.get("id")), questionId);
									questionMap.put("id", questionId);
									questionMap.put("appCode", appCode);
									questionMap.put("formId", formId);
									num = this.getSqlRunner().insert(request, "form_question_base", questionMap);
								}
							}
						}
						if (questionGrid != null && questionGrid.size() > 0) {
							for (int i = 0; i < questionGrid.size(); i++) {
								Map<String, Object> questionGridMap = questionGrid.get(i);
								if (questionGridMap != null && questionGridMap.size() > 0) {
									long questionGridId = FwUtils.getIdService().nextId();
									questionGridMap.put("id", questionGridId);
									questionGridMap.put("questionId",
											questionIdMap.get(StringUtils.trimNull(questionGridMap.get("questionId"))));
									questionGridMap.put("appCode", appCode);
									questionGridMap.put("formId", formId);
									num = this.getSqlRunner().insert(request, "form_question_grid", questionGridMap);
								}
							}
						}
						Map<String, Object> res = new HashMap<String, Object>();
						res.put("id", formId);
						response.setData(res);
					} else {
						response.setCodeAndMsg(BusinessConst.CODE_UNKNOW, "创建失败，请稍后重试！");
					}
				}
			} else {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "警告：非法操作，模板id缺失！");
			}
		} else {
			response.setCodeAndMsg(BusinessConst.CODE_AUTH, "请先登录系统！");
		}
		return response;
	}


	/**
	 * 回显毕业要求
	 */
	@BusinessMethod
	public BusinessResponse displayRequirementsMet(BusinessRequest request){
		BusinessResponse response = new BusinessResponse();
		// 获取用户所属租户编码
		String appCode = BusinessUtils.getCurrentAppCode(request);
		if (!StringUtils.isEmpty(appCode)) {
			long id = LangUtils.parseLong((request.getData("formId")));
			String busiGroup1 = StringUtils.trimNull(request.getData("busiGroup1"));
			String busiGroup2 = StringUtils.trimNull(request.getData("busiGroup2"));
			String busiGroup3 = StringUtils.trimNull(request.getData("busiGroup3"));
			QueryBean queryBean = new QueryBean(request);
			queryBean.addConditionEquals("appCode", appCode);
			queryBean.addConditionEquals("formType", 1);
			if (!StringUtils.isEmpty(busiGroup1)) {
				queryBean.addConditionEquals("busiGroup1", busiGroup1);
			}
			if (!StringUtils.isEmpty(busiGroup2)) {
				queryBean.addConditionEquals("busiGroup2", busiGroup2);
			}
			if (!StringUtils.isEmpty(busiGroup3)) {
				queryBean.addConditionEquals("busiGroup3", busiGroup3);
			}
			queryBean.addConditionEquals("id", id);
			queryBean.setTableName("form_main_base");
			queryBean.setOrderStr("rowSort asc");
			List<Map<String, Object>> pageObj = getSqlRunner().list(request, queryBean);
			response.setData(pageObj);
		} else {
			response.setCodeAndMsg(BusinessConst.CODE_AUTH, "请先登录系统！");
		}
		return response;
	}


	/**
	 * 回显课程
	 */
	@BusinessMethod
	public BusinessResponse displayCourseQuality(BusinessRequest request){
		BusinessResponse response = new BusinessResponse();
		// 获取用户所属租户编码
		String appCode = BusinessUtils.getCurrentAppCode(request);
		if (!StringUtils.isEmpty(appCode)) {
			long id = LangUtils.parseLong((request.getData("formId")));
			String busiGroup1 = StringUtils.trimNull(request.getData("busiGroup1"));
			String busiGroup2 = StringUtils.trimNull(request.getData("busiGroup2"));
			String busiGroup3 = StringUtils.trimNull(request.getData("busiGroup3"));
			QueryBean queryBean = new QueryBean(request);
			queryBean.addConditionEquals("appCode", appCode);
			queryBean.addConditionEquals("formType", 2);
			if (!StringUtils.isEmpty(busiGroup1)) {
				queryBean.addConditionEquals("busiGroup1", busiGroup1);
			}
			if (!StringUtils.isEmpty(busiGroup2)) {
				queryBean.addConditionEquals("busiGroup2", busiGroup2);
			}
			if (!StringUtils.isEmpty(busiGroup3)) {
				queryBean.addConditionEquals("busiGroup3", busiGroup3);
			}
			queryBean.addConditionEquals("id", id);
			queryBean.setTableName("form_main_base");
			queryBean.setOrderStr("rowSort asc");
			List<Map<String, Object>> pageObj = getSqlRunner().list(request, queryBean);
			response.setData(pageObj);
		} else {
			response.setCodeAndMsg(BusinessConst.CODE_AUTH, "请先登录系统！");
		}
		return response;
	}


	/**
	 * 修改毕业要求达成评价问卷
	 * @param request
	 * @return
	 */
	@BusinessMethod
	public BusinessResponse modifyRequirementsMet(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		// 获取用户所属租户编码
		String appCode = BusinessUtils.getCurrentAppCode(request);
		if (!StringUtils.isEmpty(appCode)) {
			// 获取参数
			long id = LangUtils.parseLong((request.getData("formId")));
			String formName = StringUtils.trimNull(request.getData("formName"));
			String ext1 = StringUtils.trimNull(request.getData("ext1"));
			String ext2 = StringUtils.trimNull(request.getData("ext2"));
			String startTime = StringUtils.trimNull(request.getData("startTime"));
			String endTime = StringUtils.trimNull(request.getData("endTime"));
			long rowSort = LangUtils.parseLong(request.getData("rowSort"));
			String remarks = StringUtils.trimNull(request.getData("remarks"));
			String endWord = StringUtils.trimNull(request.getData("endWord"));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date startDate = null;
			Date endDate = null;
			try {
				startDate = sdf.parse(startTime);
			} catch (Exception e) {
			}
			try {
				endDate = sdf.parse(endTime);
			} catch (Exception e) {
			}
			// 参数验证
			if (StringUtils.isEmpty(id)) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "警告：非法操作！");
			}  else if (StringUtils.isEmpty(formName)) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "表单名称不能为空！");
			} else if (formName.length() > 64) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "表单名称不能超过64个字符！");
			} else if (StringUtils.isEmpty(ext1)) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "培养方案版本不能为空！");
			} else if (StringUtils.isEmpty(ext2)) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "年级不能为空！");
			} else if (!StringUtils.isEmpty(startTime) && startDate == null) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "开始时间格式错误，正确格式如：2019-08-01 10:30:00！");
			} else if (!StringUtils.isEmpty(endTime) && endDate == null) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "结束时间格式错误，正确格式如：2019-08-01 10:30:00！");
			} else if (rowSort < 0 || rowSort > 9999999999l) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "排序必须是0-9999999999");
			} else if (!StringUtils.isEmpty(remarks) && remarks.length() > 500) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "备注不能超过500个字符！");
			} else if (!StringUtils.isEmpty(endWord) && endWord.length() > 256) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "结束描述不能超过256个字符！");
			} else {
				String lastUser = BusinessUtils.getCurrentUserCode(request);
				String lastTime = DateUtils.getTime();
				Map<String, Object> updateMap = new HashMap<>();
				updateMap.put("id", id);
				updateMap.put("formName", formName);
				updateMap.put("ext1", ext1);
				updateMap.put("ext2", ext2);
				updateMap.put("startTime", startTime);
				updateMap.put("endTime", endTime);
				updateMap.put("rowSort", rowSort);
				updateMap.put("remarks", remarks);
				updateMap.put("endWord", endWord);
				updateMap.put("lastUser", lastUser);
				updateMap.put("lastTime", lastTime);
				int num = this.getSqlRunner().update(request, "form_main_base", updateMap);
				if (num < 1) {
					response.setCodeAndMsg(BusinessConst.CODE_PARAM, "数据可能已经被删除，请刷新后重试！");
				}
			}
		} else {
			response.setCodeAndMsg(BusinessConst.CODE_AUTH, "请先登录系统！");
		}
		return response;
	}

	/**
	 * 修改课程质量评价问卷
	 * @param request
	 * @return
	 */
	@BusinessMethod
	public BusinessResponse modifyCourseQuality(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		// 获取用户所属租户编码
		String appCode = BusinessUtils.getCurrentAppCode(request);
		if (!StringUtils.isEmpty(appCode)) {
			// 获取参数
			String id = StringUtils.trimNull(request.getData("formId"));
			String formName = StringUtils.trimNull(request.getData("formName"));
			String ext1 = StringUtils.trimNull(request.getData("ext1"));
			String ext2 = StringUtils.trimNull(request.getData("ext2"));
			String ext3 = StringUtils.trimNull(request.getData("ext3"));
			String ext4 = StringUtils.trimNull(request.getData("ext4"));
			String startTime = StringUtils.trimNull(request.getData("startTime"));
			String endTime = StringUtils.trimNull(request.getData("endTime"));
			long rowSort = LangUtils.parseLong(request.getData("rowSort"));
			String remarks = StringUtils.trimNull(request.getData("remarks"));
			String endWord = StringUtils.trimNull(request.getData("endWord"));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date startDate = null;
			Date endDate = null;
			try {
				startDate = sdf.parse(startTime);
			} catch (Exception e) {
			}
			try {
				endDate = sdf.parse(endTime);
			} catch (Exception e) {
			}
			// 参数验证
			if (StringUtils.isEmpty(id)) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "警告：非法操作！");
			}
			if (StringUtils.isEmpty(formName)) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "表单名称不能为空！");
			} else if (formName.length() > 64) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "表单名称不能超过64个字符！");
			} else if (StringUtils.isEmpty(ext1)) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "培养方案版本不能为空！");
			} else if (StringUtils.isEmpty(ext2)) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "年级不能为空！");
			} else if (StringUtils.isEmpty(ext3)) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "评价课程不能为空！");
			} else if (!"1".equals(ext4) && !"2".equals(ext4)) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "是否开启评价提醒值非法(1:是,2:否)！");
			} else if (!StringUtils.isEmpty(startTime) && startDate == null) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "开始时间格式错误，正确格式如：2019-08-01 10:30:00！");
			} else if (!StringUtils.isEmpty(endTime) && endDate == null) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "结束时间格式错误，正确格式如：2019-08-01 10:30:00！");
			} else if (rowSort < 0 || rowSort > 9999999999l) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "排序必须是0-9999999999");
			} else if (!StringUtils.isEmpty(remarks) && remarks.length() > 500) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "备注不能超过500个字符！");
			} else if (!StringUtils.isEmpty(endWord) && endWord.length() > 256) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "结束描述不能超过256个字符！");
			} else {
				String lastUser = BusinessUtils.getCurrentUserCode(request);
				String lastTime = DateUtils.getTime();
				Map<String, Object> updateMap = new HashMap<>();
				updateMap.put("id", id);
				updateMap.put("formName", formName);
				updateMap.put("ext1", ext1);
				updateMap.put("ext2", ext2);
				updateMap.put("ext3", ext3);
				updateMap.put("ext4", ext4);
				updateMap.put("startTime", startDate);
				updateMap.put("endTime", endDate);
				updateMap.put("rowSort", rowSort);
				updateMap.put("remarks", remarks);
				updateMap.put("endWord", endWord);
				updateMap.put("lastUser", lastUser);
				updateMap.put("lastTime", lastTime);
				int num = this.getSqlRunner().update(request, "form_main_base", updateMap);
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
			if (ids.size() > 0) {
				Map<String, Object> sqlForInMap = this.getSqlForIn(ids);
				if (sqlForInMap != null && sqlForInMap.size() > 0) {
					String sqlForIn = StringUtils.trimNull(sqlForInMap.get("sqlForIn"));
					List<Object> sqlForInList = (List<Object>) (sqlForInMap.get("paramList"));
					Object[] objs = new Object[sqlForInList.size()];
					for (int i = 0; i < sqlForInList.size(); i++) {
						objs[i] = sqlForInList.get(i);
					}
					String sql = "select count(1) num from form_main_invitation where form_id in(" + sqlForIn + ")";
					long count = this.getSqlRunner().queryCountBySql(request, sql, objs);
					if (count > 0) {
						response.setCodeAndMsg(BusinessConst.CODE_PARAM, "删除失败：已经发送邀请！");
					} else {
						// 删除数据
						int num = this.getSqlRunner().delete(request, "form_main_base", ids);
						if (num > 0) {
							sql = "delete from form_main_grid where form_id in(" + sqlForIn + ")";
							this.getSqlRunner().execBySql(request, sql, objs);
							sql = "delete from form_question_base where form_id in(" + sqlForIn + ")";
							this.getSqlRunner().execBySql(request, sql, objs);
							sql = "delete from form_question_grid where form_id in(" + sqlForIn + ")";
							this.getSqlRunner().execBySql(request, sql, objs);
						} else {
							response.setCodeAndMsg(BusinessConst.CODE_PARAM, "数据可能已经被删除，请核对！");
						}
					}
				} else {
					response.setCodeAndMsg(BusinessConst.CODE_PARAM, "警告：非法操作！");
				}
			}
		} else {
			response.setCodeAndMsg(BusinessConst.CODE_AUTH, "请先登录系统！");
		}
		return response;
	}

	/**
	 * 发布问卷
	 * @param request
	 * @return
	 */
	@BusinessMethod
	public BusinessResponse publish(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		// 获取用户所属租户编码
		String appCode = BusinessUtils.getCurrentAppCode(request);
		if (!StringUtils.isEmpty(appCode)) {
			String id = StringUtils.trimNull(request.getData("formId"));
			String startTime = StringUtils.trimNull(request.getData("startTime"));
			String endTIme = StringUtils.trimNull(request.getData("endTIme"));
			String isIpLimit = StringUtils.trimNull(request.getData("isIpLimit"));
			String data = StringUtils.trimNull(request.getData("data"));
			String title = StringUtils.trimNull(request.getData("sendName"));
			String subject = StringUtils.trimNull(request.getData("subject"));
			String html = StringUtils.trimNull(request.getData("html"));
			if (StringUtils.isEmpty(startTime)) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "开始时间不能为空！");
			}else if(StringUtils.isEmpty(endTIme)){
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "结束时间不能为空！");
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date startDate = null;
			Date endDate = null;
			try {
				startDate = sdf.parse(startTime);
			} catch (Exception e) {
			}
			try {
				endDate = sdf.parse(endTIme);
			} catch (Exception e) {
			}
			List<Object> dataMap = FwUtils.getJsonService().toList(data);
			int status = 2;
			String lastUser = BusinessUtils.getCurrentUserCode(request);
			String lastTime = DateUtils.getTime();
			Map<String, Object> updateMap = new HashMap<String, Object>();
			updateMap.put("id", id);
			updateMap.put("status", status);
			updateMap.put("lastUser", lastUser);
			updateMap.put("lastTime", lastTime);
			updateMap.put("startTime", startDate);
			updateMap.put("endTime", endDate);
			updateMap.put("ext5", isIpLimit);

			if (null !=dataMap && dataMap.size() >0){
				updateMap.put("sendNum", dataMap.size());
			}else{
				updateMap.put("sendNum", "1000");
			}
			updateMap.put("receiveNum", 0);
			int num = this.getSqlRunner().update(request, "form_main_base", updateMap);
			if (num < 1) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "数据可能已经被删除，请刷新后重试！");
			}
			if(null !=dataMap && dataMap.size() >0){
				for(Object object:dataMap){
					Map<String, Object> oneMap = (Map<String, Object>) object;
					String email = StringUtils.trimNull(oneMap.get("email"));
					String userId = StringUtils.trimNull(oneMap.get("id"));
					String htmlResult = html;
					htmlResult = htmlResult +"<p>\n" +
							"<span style=\"color: rgb(44, 62, 80); font-family: &quot;Microsoft YaHei&quot;; font-size: 14px; font-weight: 700; background-color: rgb(255, 255, 255);\">" +
							"问卷地址:&nbsp;</span><a href=\"http://192.168.1.175:82/#/test?id="+id+/*"userId"+userId+*/" \" style=\"white-space: normal; font-family: &quot;Microsoft YaHei&quot;; font-size: 14px; font-weight: 700;\">" +
							"http://192.168.1.175:82/#/test?id="+id+"&userId"+userId+"</a>\n" +
							"</p>";
					Map<String, Object> insertMap = new HashMap<String, Object>();
					insertMap.put("id", FwUtils.getIdService().nextId());
					insertMap.put("type", 3);
					insertMap.put("sendUserId", userId);
					insertMap.put("createTime", lastTime);
					insertMap.put("status", 0);
					insertMap.put("formId", id);
					getSqlRunner().insert(request,"form_answer",insertMap);
					String finalHtmlResult = htmlResult;
					new Thread(new Runnable() {
						@Override
						public void run() {
							if(StringUtils.isEmpty(title)){
								sendEmailForListPeople(email, finalHtmlResult,subject,"基于OBE工程教育认证系统");
							}else{
								sendEmailForListPeople(email, finalHtmlResult,subject,title);
							}
						}
					}).start();
					return response;
				}
			}
		} else {
			response.setCodeAndMsg(BusinessConst.CODE_AUTH, "请先登录系统！");
		}
		return response;
	}

	/**
	 * 发起调查
	 * @param request
	 * @return
	 */
	@BusinessMethod
	public BusinessResponse startInvestigation(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		// 获取用户所属租户编码
		String appCode = BusinessUtils.getCurrentAppCode(request);
		if (!StringUtils.isEmpty(appCode)) {
			String id = StringUtils.trimNull(request.getData("formId"));
			int status = 2;
			String lastUser = BusinessUtils.getCurrentUserCode(request);
			String lastTime = DateUtils.getTime();
			Map<String, Object> updateMap = new HashMap<String, Object>();
			updateMap.put("id", id);
			updateMap.put("status", status);
			updateMap.put("lastUser", lastUser);
			updateMap.put("lastTime", lastTime);
			int num = this.getSqlRunner().update(request, "form_main_base", updateMap);

			if (num < 1) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "数据可能已经被删除，请刷新后重试！");
			}
		} else {
			response.setCodeAndMsg(BusinessConst.CODE_AUTH, "请先登录系统！");
		}
		return response;
	}

	// 废弃问卷
	@BusinessMethod
	public BusinessResponse discard(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		// 获取用户所属租户编码
		String appCode = BusinessUtils.getCurrentAppCode(request);
		if (!StringUtils.isEmpty(appCode)) {
			String id = StringUtils.trimNull(request.getData("formId"));
			int status = 3;
			String lastUser = BusinessUtils.getCurrentUserCode(request);
			String lastTime = DateUtils.getTime();
			Map<String, Object> updateMap = new HashMap<String, Object>();
			updateMap.put("id", id);
			updateMap.put("status", status);
			updateMap.put("lastUser", lastUser);
			updateMap.put("lastTime", lastTime);
			int num = this.getSqlRunner().update(request, "form_main_base", updateMap);
			if (num < 1) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "数据可能已经被删除，请刷新后重试！");
			}
		} else {
			response.setCodeAndMsg(BusinessConst.CODE_AUTH, "请先登录系统！");
		}
		return response;
	}

	// 停止问卷
	@BusinessMethod
	public BusinessResponse stop(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		// 获取用户所属租户编码
		String appCode = BusinessUtils.getCurrentAppCode(request);
		if (!StringUtils.isEmpty(appCode)) {
			String id = StringUtils.trimNull(request.getData("formId"));
			int status = 5;
			String lastUser = BusinessUtils.getCurrentUserCode(request);
			String lastTime = DateUtils.getTime();
			Map<String, Object> updateMap = new HashMap<String, Object>();
			updateMap.put("id", id);
			updateMap.put("status", status);
			updateMap.put("lastUser", lastUser);
			updateMap.put("lastTime", lastTime);
			int num = this.getSqlRunner().update(request, "form_main_base", updateMap);
			if (num < 1) {
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "数据可能已经被删除，请刷新后重试！");
			}
		} else {
			response.setCodeAndMsg(BusinessConst.CODE_AUTH, "请先登录系统！");
		}
		return response;
	}

	// 查询问卷
	@BusinessMethod
	public BusinessResponse query(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		// 获取用户所属租户编码
		String appCode = BusinessUtils.getCurrentAppCode(request);
		if (!StringUtils.isEmpty(appCode)) {
			String id = StringUtils.trimNull(request.getData("id"));
			String sql = "select * from form_main_base where id=?";
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
	public BusinessResponse listUnSubmit(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		// 获取用户所属租户编码
		String appCode = BusinessUtils.getCurrentAppCode(request);
		if (!StringUtils.isEmpty(appCode)) {
			String classifyId = StringUtils.trimNull(request.getData("classifyId"));
			String busiGroup1 = StringUtils.trimNull(request.getData("busiGroup1"));
			String busiGroup2 = StringUtils.trimNull(request.getData("busiGroup2"));
			String busiGroup3 = StringUtils.trimNull(request.getData("busiGroup3"));
			String keyWord = StringUtils.trimNull(request.getData("keyWord"));
			String ext1 = StringUtils.trimNull(request.getData("ext1"));
			String ext2 = StringUtils.trimNull(request.getData("ext2"));
			QueryBean queryBean = new QueryBean(request);
			queryBean.addConditionEquals("appCode", appCode);
			queryBean.addConditionEquals("formType", 3);
			queryBean.addConditionEquals("status", 1);
			if (!StringUtils.isEmpty(classifyId) && !"0".equals(classifyId)) {
				queryBean.addConditionEquals("classifyId", classifyId);
			}
			if (!StringUtils.isEmpty(busiGroup1)) {
				queryBean.addConditionEquals("busiGroup1", busiGroup1);
			}
			if (!StringUtils.isEmpty(busiGroup2)) {
				queryBean.addConditionEquals("busiGroup2", busiGroup2);
			}
			if (!StringUtils.isEmpty(busiGroup3)) {
				queryBean.addConditionEquals("busiGroup3", busiGroup3);
			}
			if (!StringUtils.isEmpty(keyWord)) {
				queryBean.addConditionLike("formName", keyWord);
			}
			if (!StringUtils.isEmpty(ext1)) {
				queryBean.addConditionLike("ext1", ext1);
			}
			if (!StringUtils.isEmpty(ext2)) {
				queryBean.addConditionLike("ext2", ext2);
			}
			queryBean.setTableName("form_main_base");
			queryBean.setOrderStr("lastTime desc");
			PageObj<List<Map<String, Object>>> pageObj = getSqlRunner().listPage(request, queryBean);
			response.setData(pageObj);
		} else {
			response.setCodeAndMsg(BusinessConst.CODE_AUTH, "请先登录系统！");
		}
		return response;
	}


	/**
	 * 获取毕业要求达成评价的列表
	 * @param request
	 * @return
	 */
	@BusinessMethod
	public BusinessResponse listUnSubmitRequirementsMet(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		// 获取用户所属租户编码
		String appCode = BusinessUtils.getCurrentAppCode(request);
		if (!StringUtils.isEmpty(appCode)) {
			String busiGroup1 = StringUtils.trimNull(request.getData("busiGroup1"));
			String busiGroup2 = StringUtils.trimNull(request.getData("busiGroup2"));
			String busiGroup3 = StringUtils.trimNull(request.getData("busiGroup3"));
			String keyWord = StringUtils.trimNull(request.getData("keyWord"));
			String ext1 = StringUtils.trimNull(request.getData("ext1"));
			String ext2 = StringUtils.trimNull(request.getData("ext2"));
			QueryBean queryBean = new QueryBean(request);
			queryBean.addConditionEquals("appCode", appCode);
			queryBean.addConditionEquals("formType", 1);
			queryBean.addConditionEquals("status", 1);
			queryBean.addConditionEquals("classifyId", "681570458688032768");
			if (!StringUtils.isEmpty(busiGroup1)) {
				queryBean.addConditionEquals("busiGroup1", busiGroup1);
			}
			if (!StringUtils.isEmpty(busiGroup2)) {
				queryBean.addConditionEquals("busiGroup2", busiGroup2);
			}
			if (!StringUtils.isEmpty(busiGroup3)) {
				queryBean.addConditionEquals("busiGroup3", busiGroup3);
			}
			if (!StringUtils.isEmpty(keyWord)) {
				queryBean.addConditionLike("formName", keyWord);
			}
			if (!StringUtils.isEmpty(ext1)) {
				queryBean.addConditionLike("ext1", ext1);
			}
			if (!StringUtils.isEmpty(ext2)) {
				queryBean.addConditionLike("ext2", ext2);
			}
			queryBean.setTableName("form_main_base");
			queryBean.setOrderStr("rowSort asc");
			PageObj<List<Map<String, Object>>> pageObj = getSqlRunner().listPage(request, queryBean);
			List<Map<String, Object>> list = pageObj.getRows();
			for (Map<String, Object> map:list){
				QueryBean queryBean1 = new QueryBean(request);
				queryBean1.addConditionEquals("id", map.get("ext3"));
				queryBean1.setTableName("tra_course");
				List<Map<String, Object>> list1 = getSqlRunner().list(request,queryBean1);
				if(null != list1&&list1.size()>0){
					map.put("ext3",list1.get(0).get("name"));
				}
			}
			response.setData(pageObj);
		} else {
			response.setCodeAndMsg(BusinessConst.CODE_AUTH, "请先登录系统！");
		}
		return response;
	}

	@BusinessMethod
	public BusinessResponse listUnSubmitCourseQuality(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		// 获取用户所属租户编码
		String appCode = BusinessUtils.getCurrentAppCode(request);
		if (!StringUtils.isEmpty(appCode)) {
			String busiGroup1 = StringUtils.trimNull(request.getData("busiGroup1"));
			String busiGroup2 = StringUtils.trimNull(request.getData("busiGroup2"));
			String busiGroup3 = StringUtils.trimNull(request.getData("busiGroup3"));
			String keyWord = StringUtils.trimNull(request.getData("keyWord"));
			String ext1 = StringUtils.trimNull(request.getData("ext1"));
			String ext2 = StringUtils.trimNull(request.getData("ext2"));
			QueryBean queryBean = new QueryBean(request);
			queryBean.addConditionEquals("appCode", appCode);
			queryBean.addConditionEquals("formType", 2);
			queryBean.addConditionEquals("status", 1);
			queryBean.addConditionEquals("classifyId", "681570560928387072");
			if (!StringUtils.isEmpty(busiGroup1)) {
				queryBean.addConditionEquals("busiGroup1", busiGroup1);
			}
			if (!StringUtils.isEmpty(busiGroup2)) {
				queryBean.addConditionEquals("busiGroup2", busiGroup2);
			}
			if (!StringUtils.isEmpty(busiGroup3)) {
				queryBean.addConditionEquals("busiGroup3", busiGroup3);
			}
			if (!StringUtils.isEmpty(keyWord)) {
				queryBean.addConditionLike("formName", keyWord);
			}
			if (!StringUtils.isEmpty(ext1)) {
				queryBean.addConditionLike("ext1", ext1);
			}
			if (!StringUtils.isEmpty(ext2)) {
				queryBean.addConditionLike("ext2", ext2);
			}
			queryBean.setTableName("form_main_base");
			queryBean.setOrderStr("rowSort asc");
			PageObj<List<Map<String, Object>>> pageObj = getSqlRunner().listPage(request, queryBean);
			List<Map<String, Object>> list = pageObj.getRows();
			for (Map<String, Object> map:list){
				QueryBean queryBean1 = new QueryBean(request);
				queryBean1.addConditionEquals("id", map.get("ext3"));
				queryBean1.setTableName("tra_course");
				List<Map<String, Object>> list1 = getSqlRunner().list(request,queryBean1);
				if(null != list1&&list1.size()>0){
					map.put("ext3",list1.get(0).get("name"));
				}
			}
			response.setData(pageObj);
		} else {
			response.setCodeAndMsg(BusinessConst.CODE_AUTH, "请先登录系统！");
		}
		return response;
	}

	/**
	 * 获取问卷列表
	 * @param request
	 * @return
	 */
	@BusinessMethod
	public BusinessResponse listProcess(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		// 获取用户所属租户编码
		String appCode = BusinessUtils.getCurrentAppCode(request);
		if (!StringUtils.isEmpty(appCode)) {
			String classifyId = StringUtils.trimNull(request.getData("classifyId"));
			String busiGroup1 = StringUtils.trimNull(request.getData("busiGroup1"));
			String busiGroup2 = StringUtils.trimNull(request.getData("busiGroup2"));
			String busiGroup3 = StringUtils.trimNull(request.getData("busiGroup3"));
			String keyWord = StringUtils.trimNull(request.getData("keyWord"));
			String ext1 = StringUtils.trimNull(request.getData("ext1"));
			String ext2 = StringUtils.trimNull(request.getData("ext2"));
			QueryBean queryBean = new QueryBean();
			queryBean.addConditionEquals("appCode", appCode);
			queryBean.addConditionEquals("formType", 3);
			queryBean.addConditionEquals("status", 2);
			if (!StringUtils.isEmpty(classifyId) && !"0".equals(classifyId)) {
				queryBean.addConditionEquals("classifyId", classifyId);
			}
			if (!StringUtils.isEmpty(busiGroup1)) {
				queryBean.addConditionEquals("busiGroup1", busiGroup1);
			}
			if (!StringUtils.isEmpty(busiGroup2)) {
				queryBean.addConditionEquals("busiGroup2", busiGroup2);
			}
			if (!StringUtils.isEmpty(busiGroup3)) {
				queryBean.addConditionEquals("busiGroup3", busiGroup3);
			}
			if (!StringUtils.isEmpty(keyWord)) {
				queryBean.addConditionLike("formName", keyWord);
			}
			if (!StringUtils.isEmpty(ext1)) {
				queryBean.addConditionLike("ext1", ext1);
			}
			if (!StringUtils.isEmpty(ext2)) {
				queryBean.addConditionLike("ext2", ext2);
			}
			queryBean.setTableName("form_main_base");
			queryBean.setOrderStr("lastTime");
			PageObj<List<Map<String, Object>>> pageObj = getSqlRunner().listPage(request, queryBean);
			response.setData(pageObj);
		} else {
			response.setCodeAndMsg(BusinessConst.CODE_AUTH, "请先登录系统！");
		}
		return response;
	}



	/**
	 * 获取历史答卷
	 * @param request
	 * @return
	 */
	@BusinessMethod
	public BusinessResponse listFinish(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		// 获取用户所属租户编码
		String appCode = BusinessUtils.getCurrentAppCode(request);
		if (!StringUtils.isEmpty(appCode)) {
			String classifyId = StringUtils.trimNull(request.getData("classifyId"));
			String keyWord = StringUtils.trimNull(request.getData("keyWord"));
			String userId = StringUtils.trimNull(request.getExt().get(BusinessConst.EXT_USER_ID));
			String startTime = StringUtils.trimNull(request.getData().get("startTime"));
			String endTime = StringUtils.trimNull(request.getData().get("endTime"));

			int pageSize = LangUtils.parseInt(StringUtils.trimNull(request.getData("rows")),-1);
			int page = LangUtils.parseInt(StringUtils.trimNull(request.getData("page")),-1);
			if(pageSize<0 || page<0){
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "分页参数page,pageSize错误！");
			}else{
				String sql = "SELECT v.id AS formId,v.form_name AS formName,v.classify_name AS classifyName,v.start_time AS startTime,v.end_time AS endTime,t.send_user_id as userId FROM form_answer t LEFT JOIN form_main_base v ON t.form_id = v.id WHERE t.send_user_id = ? AND t.`status` = 1 AND v.classify_id LIKE ? AND v.form_name LIKE ?  AND v.start_time > ?  AND v.end_time > ?";
				long startRow = page * pageSize;
				PageObj<List<Map<String, Object>>> pageObj = getSqlRunner().listPageBySql(request,sql,startRow,pageSize,userId,"%"+classifyId+"%","%"+keyWord+"%",startTime,endTime);
				response.setData(pageObj);
			}
		} else {
			response.setCodeAndMsg(BusinessConst.CODE_AUTH, "请先登录系统！");
		}
		return response;
	}


	/**
	 * 获取未完成答卷
	 * @param request
	 * @return
	 */
	@BusinessMethod
	public BusinessResponse listUnFinish(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		// 获取用户所属租户编码
		String appCode = BusinessUtils.getCurrentAppCode(request);
		if (!StringUtils.isEmpty(appCode)) {
			String userId = StringUtils.trimNull(request.getExt().get(BusinessConst.EXT_USER_ID));
			String classifyId = StringUtils.trimNull(request.getData("classifyId"));
			int pageSize = LangUtils.parseInt(StringUtils.trimNull(request.getData("rows")),-1);
			int page = LangUtils.parseInt(StringUtils.trimNull(request.getData("page")),-1);
			if(pageSize<0 || page<0){
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "分页参数page,pageSize错误！");
			}else{
				String sql = "SELECT v.id AS formId,v.form_name AS formName,v.classify_name AS classifyName,v.start_time AS startTime,v.end_time AS endTime,t.send_user_id as userId FROM form_answer t LEFT JOIN form_main_base v ON t.form_id = v.id WHERE t.send_user_id = ? AND v.classify_id like ?";
				long startRow = page * pageSize;
				PageObj<List<Map<String, Object>>> pageObj = getSqlRunner().listPageBySql(request,sql,startRow,pageSize,userId,"%"+classifyId+"%");
				response.setData(pageObj);
			}

		} else {
			response.setCodeAndMsg(BusinessConst.CODE_AUTH, "请先登录系统！");
		}
		return response;
	}


	/**
	 * 毕业要求达成评价调查进度
	 * @param request
	 * @return
	 */
	@BusinessMethod
	public BusinessResponse listProcessRequirementsMet(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		// 获取用户所属租户编码
		String appCode = BusinessUtils.getCurrentAppCode(request);
		if (!StringUtils.isEmpty(appCode)) {
			String busiGroup1 = StringUtils.trimNull(request.getData("busiGroup1"));
			String busiGroup2 = StringUtils.trimNull(request.getData("busiGroup2"));
			String busiGroup3 = StringUtils.trimNull(request.getData("busiGroup3"));
			String keyWord = StringUtils.trimNull(request.getData("keyWord"));
			String ext1 = StringUtils.trimNull(request.getData("ext1"));
			String ext2 = StringUtils.trimNull(request.getData("ext2"));
			QueryBean queryBean = new QueryBean(request);
			queryBean.addConditionEquals("appCode", appCode);
			queryBean.addConditionEquals("formType", 1);
			queryBean.addConditionEquals("status", 2);
			queryBean.addConditionEquals("classifyId", "681570458688032768");
			if (!StringUtils.isEmpty(busiGroup1)) {
				queryBean.addConditionEquals("busiGroup1", busiGroup1);
			}
			if (!StringUtils.isEmpty(busiGroup2)) {
				queryBean.addConditionEquals("busiGroup2", busiGroup2);
			}
			if (!StringUtils.isEmpty(busiGroup3)) {
				queryBean.addConditionEquals("busiGroup3", busiGroup3);
			}
			if (!StringUtils.isEmpty(keyWord)) {
				queryBean.addConditionLike("formName", keyWord);
			}
			if (!StringUtils.isEmpty(ext1)) {
				queryBean.addConditionLike("ext1", ext1);
			}
			if (!StringUtils.isEmpty(ext2)) {
				queryBean.addConditionLike("ext2", ext2);
			}
			queryBean.setTableName("form_main_base");
			queryBean.setOrderStr("rowSort asc");
			PageObj<List<Map<String, Object>>> pageObj = getSqlRunner().listPage(request, queryBean);
			List<Map<String, Object>> list = pageObj.getRows();
			for (Map<String, Object> map:list){
				QueryBean queryBean1 = new QueryBean(request);
				queryBean1.addConditionEquals("id", map.get("ext3"));
				queryBean1.setTableName("tra_course");
				List<Map<String, Object>> list1 = getSqlRunner().list(request,queryBean1);
				if(null != list1&&list1.size()>0){
					map.put("ext3",list1.get(0).get("name"));
				}
			}
			response.setData(pageObj);
		} else {
			response.setCodeAndMsg(BusinessConst.CODE_AUTH, "请先登录系统！");
		}
		return response;
	}


	/**
	 * 毕业要求调查分析列表
	 * @param request
	 * @return
	 */
	@BusinessMethod
	public BusinessResponse listFinishRequirementsMet(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		// 获取用户所属租户编码
		String appCode = BusinessUtils.getCurrentAppCode(request);
		if (!StringUtils.isEmpty(appCode)) {
			String busiGroup1 = StringUtils.trimNull(request.getData("busiGroup1"));
			String busiGroup2 = StringUtils.trimNull(request.getData("busiGroup2"));
			String busiGroup3 = StringUtils.trimNull(request.getData("busiGroup3"));
			String keyWord = StringUtils.trimNull(request.getData("keyWord"));
			String ext1 = StringUtils.trimNull(request.getData("ext1"));
			String ext2 = StringUtils.trimNull(request.getData("ext2"));
			QueryBean queryBean = new QueryBean(request);
			queryBean.addConditionEquals("appCode", appCode);
			queryBean.addConditionEquals("formType", 1);
			queryBean.addConditionEquals("status", 5);
			queryBean.addConditionEquals("classifyId", "681570458688032768");
			if (!StringUtils.isEmpty(busiGroup1)) {
				queryBean.addConditionEquals("busiGroup1", busiGroup1);
			}
			if (!StringUtils.isEmpty(busiGroup2)) {
				queryBean.addConditionEquals("busiGroup2", busiGroup2);
			}
			if (!StringUtils.isEmpty(busiGroup3)) {
				queryBean.addConditionEquals("busiGroup3", busiGroup3);
			}
			if (!StringUtils.isEmpty(keyWord)) {
				queryBean.addConditionLike("formName", keyWord);
			}
			if (!StringUtils.isEmpty(ext1)) {
				queryBean.addConditionLike("ext1", ext1);
			}
			if (!StringUtils.isEmpty(ext2)) {
				queryBean.addConditionLike("ext2", ext2);
			}
			queryBean.setTableName("form_main_base");
			queryBean.setOrderStr("rowSort asc");
			PageObj<List<Map<String, Object>>> pageObj = getSqlRunner().listPage(request, queryBean);
			List<Map<String, Object>> list = pageObj.getRows();
			for (Map<String, Object> map:list){
				QueryBean queryBean1 = new QueryBean(request);
				queryBean1.addConditionEquals("id", map.get("ext3"));
				queryBean1.setTableName("tra_course");
				List<Map<String, Object>> list1 = getSqlRunner().list(request,queryBean1);
				if(null != list1&&list1.size()>0){
					map.put("ext3",list1.get(0).get("name"));
				}
			}
			response.setData(pageObj);
		} else {
			response.setCodeAndMsg(BusinessConst.CODE_AUTH, "请先登录系统！");
		}
		return response;
	}


	@BusinessMethod
	public BusinessResponse listProcessCourseQuality(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		// 获取用户所属租户编码
		String appCode = BusinessUtils.getCurrentAppCode(request);
		if (!StringUtils.isEmpty(appCode)) {
			String busiGroup1 = StringUtils.trimNull(request.getData("busiGroup1"));
			String busiGroup2 = StringUtils.trimNull(request.getData("busiGroup2"));
			String busiGroup3 = StringUtils.trimNull(request.getData("busiGroup3"));
			String keyWord = StringUtils.trimNull(request.getData("keyWord"));
			String ext1 = StringUtils.trimNull(request.getData("ext1"));
			String ext2 = StringUtils.trimNull(request.getData("ext2"));
			String ext3 = StringUtils.trimNull(request.getData("ext3"));
			QueryBean queryBean = new QueryBean(request);
			queryBean.addConditionEquals("appCode", appCode);
			queryBean.addConditionEquals("formType", 2);
			queryBean.addConditionEquals("status", 2);
			queryBean.addConditionEquals("classifyId", "681570560928387072");
			if (!StringUtils.isEmpty(busiGroup1)) {
				queryBean.addConditionEquals("busiGroup1", busiGroup1);
			}
			if (!StringUtils.isEmpty(busiGroup2)) {
				queryBean.addConditionEquals("busiGroup2", busiGroup2);
			}
			if (!StringUtils.isEmpty(busiGroup3)) {
				queryBean.addConditionEquals("busiGroup3", busiGroup3);
			}
			if (!StringUtils.isEmpty(keyWord)) {
				queryBean.addConditionLike("formName", keyWord);
			}
			if (!StringUtils.isEmpty(ext1)) {
				queryBean.addConditionLike("ext1", ext1);
			}
			if (!StringUtils.isEmpty(ext2)) {
				queryBean.addConditionLike("ext2", ext2);
			}
			if (!StringUtils.isEmpty(ext3)) {
				queryBean.addConditionLike("ext3", ext3);
			}
			queryBean.setTableName("form_main_base");
			queryBean.setOrderStr("rowSort asc");
			PageObj<List<Map<String, Object>>> pageObj = getSqlRunner().listPage(request, queryBean);
			List<Map<String, Object>> list = pageObj.getRows();
			for (Map<String, Object> map:list){
				QueryBean queryBean1 = new QueryBean(request);
				queryBean1.addConditionEquals("id", map.get("ext3"));
				queryBean1.setTableName("tra_course");
				List<Map<String, Object>> list1 = getSqlRunner().list(request,queryBean1);
				if(null != list1&&list1.size()>0){
					map.put("ext3",list1.get(0).get("name"));
				}
			}
			response.setData(pageObj);
		} else {
			response.setCodeAndMsg(BusinessConst.CODE_AUTH, "请先登录系统！");
		}
		return response;
	}


	@BusinessMethod
	public BusinessResponse listFinishCourseQuality(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		// 获取用户所属租户编码
		String appCode = BusinessUtils.getCurrentAppCode(request);
		if (!StringUtils.isEmpty(appCode)) {
			String busiGroup1 = StringUtils.trimNull(request.getData("busiGroup1"));
			String busiGroup2 = StringUtils.trimNull(request.getData("busiGroup2"));
			String busiGroup3 = StringUtils.trimNull(request.getData("busiGroup3"));
			String keyWord = StringUtils.trimNull(request.getData("keyWord"));
			String ext1 = StringUtils.trimNull(request.getData("ext1"));
			String ext2 = StringUtils.trimNull(request.getData("ext2"));
			String ext3 = StringUtils.trimNull(request.getData("ext3"));
			QueryBean queryBean = new QueryBean(request);
			queryBean.addConditionEquals("appCode", appCode);
			queryBean.addConditionEquals("formType", 2);
			queryBean.addConditionEquals("status", 5);
			queryBean.addConditionEquals("classifyId", "681570560928387072");
			if (!StringUtils.isEmpty(busiGroup1)) {
				queryBean.addConditionEquals("busiGroup1", busiGroup1);
			}
			if (!StringUtils.isEmpty(busiGroup2)) {
				queryBean.addConditionEquals("busiGroup2", busiGroup2);
			}
			if (!StringUtils.isEmpty(busiGroup3)) {
				queryBean.addConditionEquals("busiGroup3", busiGroup3);
			}
			if (!StringUtils.isEmpty(keyWord)) {
				queryBean.addConditionLike("formName", keyWord);
			}
			if (!StringUtils.isEmpty(ext1)) {
				queryBean.addConditionEquals("ext1", ext1);
			}
			if (!StringUtils.isEmpty(ext2)) {
				queryBean.addConditionLike("ext2", ext2);
			}
			if (!StringUtils.isEmpty(ext3)) {
				queryBean.addConditionLike("ext3", ext3);
			}
			queryBean.setTableName("form_main_base");
			queryBean.setOrderStr("rowSort asc");
			PageObj<List<Map<String, Object>>> pageObj = getSqlRunner().listPage(request, queryBean);
			List<Map<String, Object>> list = pageObj.getRows();
			for (Map<String, Object> map:list){
				QueryBean queryBean1 = new QueryBean(request);
				queryBean1.addConditionEquals("id", map.get("ext3"));
				queryBean1.setTableName("tra_course");
				List<Map<String, Object>> list1 = getSqlRunner().list(request,queryBean1);
				if(null != list1&&list1.size()>0){
					map.put("ext3",list1.get(0).get("name"));
				}
			}
			response.setData(pageObj);
		} else {
			response.setCodeAndMsg(BusinessConst.CODE_AUTH, "请先登录系统！");
		}
		return response;
	}

	@BusinessMethod
	public BusinessResponse listEnd(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		// 获取用户所属租户编码
		String appCode = BusinessUtils.getCurrentAppCode(request);
		if (!StringUtils.isEmpty(appCode)) {
			String classifyId = StringUtils.trimNull(request.getData("classifyId"));
			String busiGroup1 = StringUtils.trimNull(request.getData("busiGroup1"));
			String busiGroup2 = StringUtils.trimNull(request.getData("busiGroup2"));
			String busiGroup3 = StringUtils.trimNull(request.getData("busiGroup3"));
			String keyWord = StringUtils.trimNull(request.getData("keyWord"));
			String ext1 = StringUtils.trimNull(request.getData("ext1"));
			String ext2 = StringUtils.trimNull(request.getData("ext2"));
			QueryBean queryBean = new QueryBean(request);
			List<Object> listForIn = new ArrayList<Object>();
			listForIn.add(4);
			listForIn.add(5);
			queryBean.addConditionEquals("appCode", appCode);
			queryBean.addConditionEquals("formType", 3);
			queryBean.addConditionIn("status", listForIn);
			if (!StringUtils.isEmpty(classifyId) && !"0".equals(classifyId)) {
				queryBean.addConditionEquals("classifyId", classifyId);
			}
			if (!StringUtils.isEmpty(busiGroup1)) {
				queryBean.addConditionEquals("busiGroup1", busiGroup1);
			}
			if (!StringUtils.isEmpty(busiGroup2)) {
				queryBean.addConditionEquals("busiGroup2", busiGroup2);
			}
			if (!StringUtils.isEmpty(busiGroup3)) {
				queryBean.addConditionEquals("busiGroup3", busiGroup3);
			}
			if (!StringUtils.isEmpty(keyWord)) {
				queryBean.addConditionLike("formName", keyWord);
			}
			if (!StringUtils.isEmpty(ext1)) {
				queryBean.addConditionLike("ext1", ext1);
			}
			if (!StringUtils.isEmpty(ext2)) {
				queryBean.addConditionLike("ext2", ext2);
			}
			queryBean.setTableName("form_main_base");
			queryBean.setOrderStr("rowSort asc");
			PageObj<List<Map<String, Object>>> pageObj = getSqlRunner().listPage(request, queryBean);
			response.setData(pageObj);
		} else {
			response.setCodeAndMsg(BusinessConst.CODE_AUTH, "请先登录系统！");
		}
		return response;
	}

	@BusinessMethod
	public BusinessResponse listEndRequirementsMet(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		// 获取用户所属租户编码
		String appCode = BusinessUtils.getCurrentAppCode(request);
		if (!StringUtils.isEmpty(appCode)) {
			String classifyId = StringUtils.trimNull(request.getData("classifyId"));
			String busiGroup1 = StringUtils.trimNull(request.getData("busiGroup1"));
			String busiGroup2 = StringUtils.trimNull(request.getData("busiGroup2"));
			String busiGroup3 = StringUtils.trimNull(request.getData("busiGroup3"));
			String keyWord = StringUtils.trimNull(request.getData("keyWord"));
			String ext1 = StringUtils.trimNull(request.getData("ext1"));
			String ext2 = StringUtils.trimNull(request.getData("ext2"));
			QueryBean queryBean = new QueryBean(request);
			List<Object> listForIn = new ArrayList<Object>();
			listForIn.add(3);
			listForIn.add(4);
			listForIn.add(5);
			queryBean.addConditionEquals("appCode", appCode);
			queryBean.addConditionEquals("formType", 1);
			queryBean.addConditionIn("status", listForIn);
			if (!StringUtils.isEmpty(classifyId) && !"0".equals(classifyId)) {
				queryBean.addConditionEquals("classifyId", classifyId);
			}
			if (!StringUtils.isEmpty(busiGroup1)) {
				queryBean.addConditionEquals("busiGroup1", busiGroup1);
			}
			if (!StringUtils.isEmpty(busiGroup2)) {
				queryBean.addConditionEquals("busiGroup2", busiGroup2);
			}
			if (!StringUtils.isEmpty(busiGroup3)) {
				queryBean.addConditionEquals("busiGroup3", busiGroup3);
			}
			if (!StringUtils.isEmpty(keyWord)) {
				queryBean.addConditionLike("formName", keyWord);
			}
			if (!StringUtils.isEmpty(ext1)) {
				queryBean.addConditionLike("ext1", ext1);
			}
			if (!StringUtils.isEmpty(ext2)) {
				queryBean.addConditionLike("ext2", ext2);
			}
			queryBean.setTableName("form_main_base");
			queryBean.setOrderStr("rowSort asc");
			PageObj<List<Map<String, Object>>> pageObj = getSqlRunner().listPage(request, queryBean);
			List<Map<String, Object>> list = pageObj.getRows();
			for (Map<String, Object> map:list){
				QueryBean queryBean1 = new QueryBean(request);
				queryBean1.addConditionEquals("id", map.get("ext3"));
				queryBean1.setTableName("tra_course");
				List<Map<String, Object>> list1 = getSqlRunner().list(request,queryBean1);
				if(null != list1&&list1.size()>0){
					map.put("ext3",list1.get(0).get("name"));
				}
			}
			response.setData(pageObj);
		} else {
			response.setCodeAndMsg(BusinessConst.CODE_AUTH, "请先登录系统！");
		}
		return response;
	}

	/**
	 * 邮件分享时人群列表
	 */
	@BusinessMethod
	public BusinessResponse listEndShareForRole(BusinessRequest request){
		BusinessResponse response = new BusinessResponse();
		String appCode = BusinessUtils.getCurrentAppCode(request);
		if (!StringUtils.isEmpty(appCode)) {
			String keyWord = StringUtils.trimNull(request.getData("keyWord"));
			String sql = "SELECT id,real_name,email FROM sys_user WHERE type = ? and real_name like ?";
			List<Map<String, Object>> objectMap = getSqlRunner().listBySql(request,sql,"2","%"+keyWord+"%");
			List<Map<String, Object>> objectMap1 = getSqlRunner().listBySql(request,sql,"1","%"+keyWord+"%");
			List<Map<String, Object>> objectMap3 = getSqlRunner().listBySql(request,sql,"5","%"+keyWord+"%");
			Map<String, Object> map1 = new HashMap<>();
			map1.put("student",objectMap);
			map1.put("teacher",objectMap1);
			map1.put("zhuanjia",objectMap3);
			response.setData(map1);
		}
		return response;
	}

	/**
	 * 获取问卷地址短链接和二维码
	 */
	@BusinessMethod
	public BusinessResponse generateUrlPic(BusinessRequest request){
		BusinessResponse response = new BusinessResponse();
		String appCode = BusinessUtils.getCurrentAppCode(request);
		if (!StringUtils.isEmpty(appCode)) {
			String id = StringUtils.trimNull(request.getData("formId"));
			String sql = "SELECT id as formID,url,url_pic,url_base64 FROM form_main_base WHERE id = ?";
			List<Map<String, Object>> list = getSqlRunner().listBySql(request, sql, id);
			if (list != null){
				response.setData(list.get(0));
			}else{
				response.setCodeAndMsg(BusinessConst.CODE_AUTH, "此问卷已被删除！");
			}
		}else {
			response.setCodeAndMsg(BusinessConst.CODE_AUTH, "请先登录系统！");
		}
		return response;
	}

	@BusinessMethod
	public BusinessResponse listEndCourseQuality(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		// 获取用户所属租户编码
		String appCode = BusinessUtils.getCurrentAppCode(request);
		if (!StringUtils.isEmpty(appCode)) {
			String classifyId = StringUtils.trimNull(request.getData("classifyId"));
			String busiGroup1 = StringUtils.trimNull(request.getData("busiGroup1"));
			String busiGroup2 = StringUtils.trimNull(request.getData("busiGroup2"));
			String busiGroup3 = StringUtils.trimNull(request.getData("busiGroup3"));
			String keyWord = StringUtils.trimNull(request.getData("keyWord"));
			String ext1 = StringUtils.trimNull(request.getData("ext1"));
			String ext2 = StringUtils.trimNull(request.getData("ext2"));
			QueryBean queryBean = new QueryBean(request);
			List<Object> listForIn = new ArrayList<Object>();
			listForIn.add(3);
			listForIn.add(4);
			listForIn.add(5);
			queryBean.addConditionEquals("appCode", appCode);
			queryBean.addConditionEquals("formType", 2);
			queryBean.addConditionIn("status", listForIn);
			if (!StringUtils.isEmpty(classifyId) && !"0".equals(classifyId)) {
				queryBean.addConditionEquals("classifyId", classifyId);
			}
			if (!StringUtils.isEmpty(busiGroup1)) {
				queryBean.addConditionEquals("busiGroup1", busiGroup1);
			}
			if (!StringUtils.isEmpty(busiGroup2)) {
				queryBean.addConditionEquals("busiGroup2", busiGroup2);
			}
			if (!StringUtils.isEmpty(busiGroup3)) {
				queryBean.addConditionEquals("busiGroup3", busiGroup3);
			}
			if (!StringUtils.isEmpty(keyWord)) {
				queryBean.addConditionLike("formName", keyWord);
			}
			if (!StringUtils.isEmpty(ext1)) {
				queryBean.addConditionEquals("ext1", ext1);
			}
			if (!StringUtils.isEmpty(ext2)) {
				queryBean.addConditionEquals("ext2", ext2);
			}
			queryBean.setTableName("form_main_base");
			queryBean.setOrderStr("rowSort asc");
			PageObj<List<Map<String, Object>>> pageObj = getSqlRunner().listPage(request, queryBean);
			List<Map<String, Object>> list = pageObj.getRows();
			for (Map<String, Object> map:list){
				QueryBean queryBean1 = new QueryBean(request);
				queryBean1.addConditionEquals("id", map.get("ext3"));
				queryBean1.setTableName("tra_course");
				List<Map<String, Object>> list1 = getSqlRunner().list(request,queryBean1);
				if(null != list1&&list1.size()>0){
					map.put("ext3",list1.get(0).get("name"));
				}
			}
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


	/**
	 *发送邮件
	 */
	public void sendEmailForListPeople(String tos,String html,String subject,String title) {
		try {
			MailUtil.sendMail(tos, html, subject, title);
		} catch ( javax.mail.MessagingException e) {
			e.printStackTrace();
		}

	}

}
