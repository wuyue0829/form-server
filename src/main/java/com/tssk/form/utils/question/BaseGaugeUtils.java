package com.tssk.form.utils.question;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tssk.form.consts.FormConsts;
import com.tssk.fw.business.bean.BusinessRequest;
import com.tssk.fw.dao.runner.SqlRunner;
import com.tssk.fw.dao.utils.DaoUtils;
import com.tssk.fw.id.base.IdService;
import com.tssk.fw.service.utils.FwUtils;
import com.tssk.fw.utils.lang.LangUtils;
import com.tssk.fw.utils.lang.StringUtils;

public class BaseGaugeUtils {


	public static SqlRunner getSqlRunner() {
		SqlRunner sqlRunner = DaoUtils.getSqlRunnerManger().getSqlRunner(FormConsts.DB_NAME);
		return sqlRunner;
	}


	public static Map<String, Object> wrap(QuestionBean bean) {
		Map<String, Object> rsMap = new HashMap<String, Object>();
		Map<String, Object> baseMap = bean.getBase();

		long id = LangUtils.parseLong(baseMap.get("id"));
		// String baseType = StringUtils.trimNull(baseMap.get("baseType"));
		String type = StringUtils.trimNull(baseMap.get("questionType"));
		String title = StringUtils.trimNull(baseMap.get("questionName"));
		String desc = StringUtils.trimNull(baseMap.get("questionDesc"));
		rsMap.put("id", id);
		rsMap.put("type", type);
		rsMap.put("title", title);
		rsMap.put("desc", desc);
		Map<String, Object> validMap = bean.attrMap(id, "valid");
		String required = StringUtils.trimNull(validMap.get("propValue10"),"false");
		rsMap.put("required", Boolean.valueOf(required));
		//
		Map<String, Object> gaugeMap = bean.attrMap(id, "gauge");
		String itemDesc = StringUtils.trimNull(gaugeMap.get("propValue1"));
		rsMap.put("itemDesc", itemDesc);
		String startDesc = StringUtils.trimNull(gaugeMap.get("propValue2"));
		rsMap.put("startDesc", startDesc);
		String endDesc = StringUtils.trimNull(gaugeMap.get("propValue3"));
		rsMap.put("endDesc", endDesc);
		String showStytle = StringUtils.trimNull(gaugeMap.get("propValue4"));
		rsMap.put("showStytle", showStytle);
		int startIndex = LangUtils.parseInt(gaugeMap.get("propValue8"), 1);
		rsMap.put("startIndex", startIndex);
		int itemNum = LangUtils.parseInt(gaugeMap.get("propValue9"), 5);
		int selectNum = LangUtils.parseInt(gaugeMap.get("propValue15"), 0);
		rsMap.put("selectNum", selectNum);
		rsMap.put("itemNum", itemNum);
		return rsMap;
	}


	public static Map<String, Object> wrapResult(QuestionBean bean, BusinessRequest request) {
		Map<String, Object> rsMap = new HashMap<String, Object>();
		Map<String, Object> baseMap = bean.getBase();

		long id = LangUtils.parseLong(baseMap.get("id"));
		// String baseType = StringUtils.trimNull(baseMap.get("baseType"));
		String type = StringUtils.trimNull(baseMap.get("questionType"));
		String title = StringUtils.trimNull(baseMap.get("questionName"));
		String desc = StringUtils.trimNull(baseMap.get("questionDesc"));
		rsMap.put("id", id);
		rsMap.put("type", type);
		rsMap.put("title", title);
		rsMap.put("desc", desc);
		Map<String, Object> validMap = bean.attrMap(id, "valid");
		String required = StringUtils.trimNull(validMap.get("propValue10"),"false");
		rsMap.put("required", Boolean.valueOf(required));
		//
		Map<String, Object> gaugeMap = bean.attrMap(id, "gauge");
		String itemDesc = StringUtils.trimNull(gaugeMap.get("propValue1"));
		rsMap.put("itemDesc", itemDesc);
		String startDesc = StringUtils.trimNull(gaugeMap.get("propValue2"));
		rsMap.put("startDesc", startDesc);
		String endDesc = StringUtils.trimNull(gaugeMap.get("propValue3"));
		rsMap.put("endDesc", endDesc);
		String showStytle = StringUtils.trimNull(gaugeMap.get("propValue4"));
		rsMap.put("showStytle", showStytle);
		int startIndex = LangUtils.parseInt(gaugeMap.get("propValue8"), 1);
		rsMap.put("startIndex", startIndex);
		int itemNum = LangUtils.parseInt(gaugeMap.get("propValue9"), 5);
		rsMap.put("itemNum", itemNum);
		Map<String, Object> itemValue = new HashMap<>();
		for (int i = startIndex; i<= itemNum;i++){
			String sql = "SELECT d.question_value FROM form_answer_question d WHERE d.question_id = ? and d.question_value = ?;";
			List<Map<String, Object>> list = getSqlRunner().listBySql(request,sql,id,startIndex+"");
			itemValue.put("item"+startIndex,list.size());
		}
		rsMap.put("itemValue", itemValue);
		return rsMap;
	}

	public static QuestionBean unwrap(String appCode, long formId, Map<String, Object> dataMap) {
		long id = LangUtils.parseLong(dataMap.get("id"));
		if (id == 0) {
			id = FwUtils.getIdService().nextId();
		}
		String type = StringUtils.trimNull(dataMap.get("type"));
		String baseType = StringUtils.trimNull(dataMap.get("baseType"));
		String title = StringUtils.trimNull(dataMap.get("title"));
		String desc = StringUtils.trimNull(dataMap.get("desc"));
		int rowSort = LangUtils.parseInt(dataMap.get("rowSort"), 1000);
		QuestionBean bean = new QuestionBean();
		bean.getBase().put("id", id);
		bean.getBase().put("appCode", appCode);
		bean.getBase().put("formId", formId);
		bean.getBase().put("baseType", baseType);
		bean.getBase().put("questionType", type);
		bean.getBase().put("questionName", title);
		bean.getBase().put("questionDesc", desc);
		bean.getBase().put("rowSort", rowSort);
		// 必填
		/*// ============================================
		Map<String, Object> validMap = new HashMap<>();
		IdService idService = FwUtils.getIdService();*/
		/*validMap.put("id", idService.nextId());
		validMap.put("appCode", appCode);
		validMap.put("formId", formId);
		validMap.put("questionId", id);
		validMap.put("propType", "valid");
		validMap.put("propKey", "valid");
		String required = StringUtils.trimNull(validMap.get("required"),"false");
		validMap.put("propValue1", required);
		validMap.put("rowSort", "1000");
		bean.getGrids().add(validMap);*/
		//
		IdService idService = FwUtils.getIdService();
		Map<String, Object> gaugeMap = new HashMap<>();
		gaugeMap.put("id", idService.nextId());
		gaugeMap.put("appCode", appCode);
		gaugeMap.put("formId", formId);
		gaugeMap.put("questionId", id);
		gaugeMap.put("propType", "gauge");
		gaugeMap.put("propKey", "gauge");

		String itemDesc = StringUtils.trimNull(dataMap.get("itemDesc"));
		String startDesc = StringUtils.trimNull(dataMap.get("startDesc"));
		String endDesc = StringUtils.trimNull(dataMap.get("endDesc"));

		String showStytle = StringUtils.trimNull(dataMap.get("showStytle"));
		int startIndex = LangUtils.parseInt(dataMap.get("startIndex"), 1);
		int itemNum = LangUtils.parseInt(dataMap.get("itemNum"), 5);
		gaugeMap.put("propValue1", itemDesc);
		gaugeMap.put("propValue2", startDesc);
		gaugeMap.put("propValue3", endDesc);
		gaugeMap.put("propValue4", showStytle);
		gaugeMap.put("propValue8", startIndex);
		gaugeMap.put("propValue9", itemNum);
		gaugeMap.put("rowSort", "1000");
		bean.getGrids().add(gaugeMap);
		return bean;
	}

}
