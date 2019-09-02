package com.tssk.form.utils.question;

import java.util.HashMap;
import java.util.Map;

import com.tssk.fw.service.utils.FwUtils;
import com.tssk.fw.utils.lang.LangUtils;
import com.tssk.fw.utils.lang.StringUtils;

public class SplitLineUtils {

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

		return bean;
	}

}
