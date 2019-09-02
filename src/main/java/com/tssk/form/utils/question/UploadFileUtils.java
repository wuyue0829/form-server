package com.tssk.form.utils.question;

import java.util.HashMap;
import java.util.Map;

import com.tssk.fw.id.base.IdService;
import com.tssk.fw.service.utils.FwUtils;
import com.tssk.fw.utils.lang.LangUtils;
import com.tssk.fw.utils.lang.StringUtils;

public class UploadFileUtils {

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
		int requiredMin = LangUtils.parseInt(validMap.get("propValue2"));
		rsMap.put("requiredMin", requiredMin);
		int requiredMax = LangUtils.parseInt(validMap.get("propValue3"));
		rsMap.put("requiredMax", requiredMax);
		String formatType = StringUtils.trimNull(validMap.get("propValue4"));
		rsMap.put("formatType", formatType);
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
		// ============================================
		Map<String, Object> validMap = new HashMap<>();
		IdService idService = FwUtils.getIdService();
		validMap.put("id", idService.nextId());
		validMap.put("appCode", appCode);
		validMap.put("formId", formId);
		validMap.put("questionId", id);
		validMap.put("propType", "valid");
		validMap.put("propKey", "valid");
		String required = StringUtils.trimNull(validMap.get("required"),"false");
		validMap.put("propValue1", required);
		int requiredMin = LangUtils.parseInt(dataMap.get("requiredMin"));
		validMap.put("propValue2", requiredMin);
		int requiredMax = LangUtils.parseInt(dataMap.get("requiredMax"));
		validMap.put("propValue3", requiredMax);
		String formatType = StringUtils.trimNull(dataMap.get("formatType"));
		validMap.put("propValue4", formatType);
		validMap.put("rowSort", "1000");
		bean.getGrids().add(validMap);
		return bean;
	}

}
