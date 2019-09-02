package com.tssk.form.utils.question;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tssk.fw.utils.lang.LangUtils;
import com.tssk.fw.utils.lang.StringUtils;

public class QuestionBean {

	private int formType;
	private long userId = 0;
	private Map<String, Object> base = new HashMap<String, Object>();
	private List<Map<String, Object>> grids = new ArrayList<>();

	public Map<String, Object> getBase() {
		return base;
	}

	public int getFormType() {
		return formType;
	}

	public void setFormType(int formType) {
		this.formType = formType;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public void setBase(Map<String, Object> base) {
		this.base = base;
	}

	public List<Map<String, Object>> getGrids() {
		return grids;
	}

	public void setGrids(List<Map<String, Object>> grids) {
		this.grids = grids;
	}

	public List<Map<String, Object>> attrList(long questionId) {
		return getAttrList(grids, questionId);
	}

	public static List<Map<String, Object>> getAttrList(List<Map<String, Object>> grids, long questionId) {
		List<Map<String, Object>> rsList = new ArrayList<>();
		for (Map<String, Object> map : grids) {
			long tquestionId = LangUtils.parseLong(map.get("questionId"));
			if (tquestionId == questionId) {
				rsList.add(map);
			}
		}
		return rsList;
	}


	public Map<String, Object> attrMap(long questionId, String columnName) {
		return getAttrMap(grids, questionId, columnName);
	}

	
	public static Map<String, Object> getAttrMap(List<Map<String, Object>> grids, long questionId, String propKey) {
		Map<String, Object> rsMap = new HashMap<>();
		for (Map<String, Object> map : grids) {
			long tquestionId = LangUtils.parseLong(map.get("questionId"));
			String tpropKey = StringUtils.trimNull(map.get("propKey"));
			if (tquestionId == questionId && tpropKey.equals(propKey)) {
				rsMap = map;
				break;
			}
		}
		return rsMap;
	}

}
