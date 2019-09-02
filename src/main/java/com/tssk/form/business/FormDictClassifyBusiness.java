package com.tssk.form.business;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tssk.form.consts.FormConsts;
import com.tssk.fw.business.base.Business;
import com.tssk.fw.business.base.BusinessMethod;
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

@Business(name = "FormDictClassifyBusiness")
public class FormDictClassifyBusiness {

	public SqlRunner getSqlRunner() {
		SqlRunner sqlRunner = DaoUtils.getSqlRunnerManger().getSqlRunner(FormConsts.DB_NAME);
		return sqlRunner;
	}

	@BusinessMethod
	public BusinessResponse add(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		Map<String, Object> insertMap = new HashMap<>();
		String classifyCode = StringUtils.trimNull(request.getData("classifyCode"));
		String classifyName = StringUtils.trimNull(request.getData("classifyName"));
		insertMap.put("classifyCode", classifyCode);
		insertMap.put("classifyName", classifyName);
		// 扩展信息
		long id = FwUtils.getIdService().nextId();
		insertMap.put("id", id);
		int status = 1;
		int rowSort = LangUtils.parseInt(request.getData("rowSort"), 1000);
		String remarks = StringUtils.trimNull(request.getData("remarks"));
		String createUser = BusinessUtils.getCurrentUserCode(request);
		String createTime = DateUtils.getTime();
		String lastUser = BusinessUtils.getCurrentUserCode(request);
		String lastTime = DateUtils.getTime();
		insertMap.put("status", status);
		insertMap.put("rowSort", rowSort);
		insertMap.put("remarks", remarks);
		insertMap.put("createUser", createUser);
		insertMap.put("createTime", createTime);
		insertMap.put("lastUser", lastUser);
		insertMap.put("lastTime", lastTime);
		getSqlRunner().insert(request, "form_dict_classify", insertMap);
		return response;
	}

	@BusinessMethod
	public BusinessResponse remove(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		String id = StringUtils.trimNull(request.getData("id"));
		List<String> ids = CollectionUtils.valueOfList(id);
		if (ids.size() > 0) {
			// 删除数据
			getSqlRunner().delete(request, "form_dict_classify", ids);
		}
		return response;
	}

	@BusinessMethod
	public BusinessResponse modify(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		String classifyCode = StringUtils.trimNull(request.getData("classifyCode"));
		String classifyName = StringUtils.trimNull(request.getData("classifyName"));
		
		long id = LangUtils.parseLong(request.getData("id"));
		int rowSort = LangUtils.parseInt(request.getData("rowSort"), 1000);
		String remarks = StringUtils.trimNull(request.getData("remarks"));
		String lastUser = BusinessUtils.getCurrentUserCode(request);
		String lastTime = DateUtils.getTime();
		Map<String, Object> updateMap = new HashMap<>();
		updateMap.put("classifyCode", classifyCode);
		updateMap.put("classifyName", classifyName);
		updateMap.put("lastUser", lastUser);
		updateMap.put("lastTime", lastTime);
		updateMap.put("rowSort", rowSort);
		updateMap.put("remarks", remarks);
		updateMap.put("id", id);
		getSqlRunner().update(request, "form_dict_classify", updateMap);
		return response;
	}
	
	@BusinessMethod
	public BusinessResponse listAll(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		QueryBean queryBean = new QueryBean(request);
		queryBean.setTableName("form_dict_classify");
		List<Map<String, Object>> pageObj = getSqlRunner().list(request, queryBean);
		response.setData(pageObj);
		return response;
	}

	@BusinessMethod
	public BusinessResponse listPage(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		QueryBean queryBean = new QueryBean(request);
		queryBean.setTableName("form_dict_classify");
		PageObj<List<Map<String, Object>>> pageObj = getSqlRunner().listPage(request, queryBean);
		response.setData(pageObj);
		return response;
	}
}
