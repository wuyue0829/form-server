package com.tssk.form.utils.question;

import java.text.NumberFormat;
import java.util.ArrayList;
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

public class GradeRadioUtils {

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
		String required = StringUtils.trimNull(bean.getGrids().get(0).get("propValue10"),"false");
		rsMap.put("required", Boolean.valueOf(required));
		int requiredMin = LangUtils.parseInt(bean.getGrids().get(0).get("propValue2"));
		rsMap.put("requiredMin", requiredMin);
		int requiredMax = LangUtils.parseInt(bean.getGrids().get(0).get("propValue3"));
		rsMap.put("requiredMax", requiredMax);
		String formatType = StringUtils.trimNull(bean.getGrids().get(0).get("propValue4"));
		rsMap.put("formatType", formatType);

		//====================
		List<Map<String, Object>> items = bean.attrList(id);
		List<Map<String, Object>> itemList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> map : items) {
			long userId = bean.getUserId();
			if(userId != 0){
				String sql = "";
				getSqlRunner().queryBySql(null,sql,userId);
			}
			String propKey = StringUtils.trimNull(map.get("propKey"));
			if ("item".equals(propKey)) {
				Map<String, Object> tempMap = new HashMap<>();
				String propValue1 = StringUtils.trimNull(map.get("propValue1"));
				String propValue2 = StringUtils.trimNull(map.get("propValue2"));
				tempMap.put("text", propValue1);
				if(bean.getFormType() == 1){
					rsMap.put("propValue1", StringUtils.trimNull(map.get("propValue11")));
					rsMap.put("propValue2", StringUtils.trimNull(map.get("propValue12")));
				}else if(bean.getFormType() == 2){
					rsMap.put("propValue1", StringUtils.trimNull(map.get("propValue12")));
					rsMap.put("propValue2", StringUtils.trimNull(map.get("propValue13")));
				}
				String answer = StringUtils.trimNull(map.get("propValue15"));
				if(null != answer && answer.length()>0){
					tempMap.put("answer", true);
				}else{
					tempMap.put("answer", false);
				}
				tempMap.put("scores", propValue2);
				itemList.add(tempMap);
			}
		}
		rsMap.put("items", itemList);
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


		/*// 必填
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
		validMap.put("rowSort", "1000");
		bean.getGrids().add(validMap);*/

		List<?> items = (List<?>) dataMap.get("items");
		if (items != null) {
			for (Object object : items) {
				Map<?, ?> itemMap = (Map<?, ?>) object;
				String text = StringUtils.trimNull(itemMap.get("text"));
				int scores = LangUtils.parseInt(itemMap.get("scores"));
				/*String required = "false";
				if(null != bean.getGrids()){
					required = StringUtils.trimNull(bean.getGrids().get(0).get("propValue10"),"false");
				}*/
				String required = StringUtils.trimNull(dataMap.get("required"),"false");
				Map<String, Object> item = createItem(appCode, formId, id, text, scores,required);
				bean.getGrids().add(item);
			}
		}

		return bean;
	}

	public static Map<String, Object> createItem(String appCode, long formId, long questionId, String text, int grade,String required) {
		Map<String, Object> propMap = new HashMap<>();
		IdService idService = FwUtils.getIdService();
		propMap.put("id", idService.nextId());
		propMap.put("appCode", appCode);
		propMap.put("formId", formId);
		propMap.put("questionId", questionId);
		propMap.put("propType", "item");
		propMap.put("propKey", "item");
		propMap.put("propValue1", text);
		propMap.put("propValue2", grade);
		propMap.put("propValue10", required);
		propMap.put("rowSort", "1000");
		return propMap;
	}


	public static Map<String, Object> createItemType(String appCode, long formId, long questionId, String text, int grade,String required, long propValue1,long propValue2,long propValue3,long propValue4) {
		Map<String, Object> propMap = new HashMap<>();
		IdService idService = FwUtils.getIdService();
		propMap.put("id", idService.nextId());
		propMap.put("appCode", appCode);
		propMap.put("formId", formId);
		propMap.put("questionId", questionId);
		propMap.put("propType", "item");
		propMap.put("propKey", "item");
		propMap.put("propValue1", text);
		propMap.put("propValue2", grade);
		propMap.put("rowSort", "1000");
		propMap.put("propValue10", required);
		propMap.put("propValue11", propValue1);
		propMap.put("propValue12", propValue2);
		propMap.put("propValue13", propValue3);
		propMap.put("propValue14", propValue4);
		return propMap;
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
		String required = StringUtils.trimNull(bean.getGrids().get(0).get("propValue10"),"false");
		rsMap.put("required", Boolean.valueOf(required));
		int requiredMin = LangUtils.parseInt(bean.getGrids().get(0).get("propValue2"));
		rsMap.put("requiredMin", requiredMin);
		int requiredMax = LangUtils.parseInt(bean.getGrids().get(0).get("propValue3"));
		rsMap.put("requiredMax", requiredMax);
		String formatType = StringUtils.trimNull(bean.getGrids().get(0).get("propValue4"));
		rsMap.put("formatType", formatType);
		//====================
		List<Map<String, Object>> items = bean.attrList(id);
		List<Map<String, Object>> itemList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> map : items) {
			String propKey = StringUtils.trimNull(map.get("propKey"));
			if ("item".equals(propKey)) {
				Map<String, Object> tempMap = new HashMap<>();
				String propValue1 = StringUtils.trimNull(map.get("propValue1"));
				String propValue2 = StringUtils.trimNull(map.get("propValue2"));
				String sql = "SELECT d.question_value FROM form_answer_question d WHERE d.question_id = ? and d.question_value = ?;";
				List<Map<String, Object>> list = getSqlRunner().listBySql(request,sql,id,propValue1);
				String sql1 = "SELECT d.question_value FROM form_answer_question d WHERE d.question_id = ?";
				List<Map<String, Object>> list1 = getSqlRunner().listBySql(request,sql1,id);
				tempMap.put("text", propValue1);
				tempMap.put("scores", Integer.parseInt(propValue2));
				tempMap.put("number",list.size());
				NumberFormat format = NumberFormat.getIntegerInstance();
				format.setMaximumFractionDigits(2);//设置保留几位小数
				if(list.size() < 1){
					tempMap.put("proportion",Integer.parseInt(format.format(0)));
				}else{
					tempMap.put("proportion",Integer.parseInt(format.format(((double) list.size()*100/(double)list1.size()))));
				}
				itemList.add(tempMap);
			}
		}
		rsMap.put("items", itemList);
		return rsMap;
	}



	public static QuestionBean unwraptype(String appCode, long formId, Map<String, Object> dataMap,int typeByUI) {
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

		List<?> items = (List<?>) dataMap.get("items");
		if (items != null) {
			for (Object object : items) {
				Map<?, ?> itemMap = (Map<?, ?>) object;
				String text = StringUtils.trimNull(itemMap.get("text"));
				int scores = LangUtils.parseInt(itemMap.get("scores"));
				long propValue11 = 0;
				long propValue12 = 0;
				long propValue13 = 0;
				long propValue14 = 0;
				if(typeByUI == 1){
					propValue11 = LangUtils.parseLong(dataMap.get("propValue1"));
					propValue12 = LangUtils.parseLong(dataMap.get("propValue2"));
				}else{
					propValue13 = LangUtils.parseLong(dataMap.get("propValue1"));
					propValue14 = LangUtils.parseLong(dataMap.get("propValue2"));
				}
				String required = StringUtils.trimNull(dataMap.get("required"),"false");
				Map<String, Object> item = createItemType(appCode, formId, id, text, scores,required,propValue11,propValue12,propValue13,propValue14);
				bean.getGrids().add(item);
			}
		}

		return bean;
	}

}
