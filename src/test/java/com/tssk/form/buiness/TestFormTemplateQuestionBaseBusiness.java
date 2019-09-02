package com.tssk.form.buiness;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.tssk.form.http.HttpUtils;
import com.tssk.fw.boot.Launcher;
import com.tssk.fw.business.bean.BusinessRequest;
import com.tssk.fw.business.bean.BusinessResponse;
import com.tssk.fw.http.consts.HttpConst;
import com.tssk.fw.service.utils.FwUtils;

public class TestFormTemplateQuestionBaseBusiness {

	public static void main(String[] args) {
		boolean isLocal = true;
		System.out.println("=========" + isLocal);
		if (isLocal) {
			init();
		}
		String str = getSaveData();
		System.out.println(str);
		System.out.println(str);
		saveQuestionAll(isLocal);
		queryQuestionAll(isLocal);
	}

	public static void init() {
		Map<String, Object> cfg = new HashMap<String, Object>();
		cfg.put(HttpConst.HTTP_MODULE_ENABLE, "false");
		Launcher.start(cfg);
	}

	public static void saveQuestionAll(boolean isLocal) {
		String service = "FormTemplateQuestionBusiness";
		String method = "saveQuestionAll";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("formId", "123");
		param.put("data", getSaveData());
		test(isLocal, service, method, param);
	}

	public static void queryQuestionAll(boolean isLocal) {
		String service = "FormTemplateQuestionBusiness";
		String method = "queryQuestionAll";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("formId", "123");
		test(isLocal, service, method, param);
	}

	public static void test(boolean isLocal, String service, String method, Map<String, Object> param) {
		if (isLocal) {
			BusinessRequest request = new BusinessRequest();
			request.setService(service);
			request.setMethod(method);
			request.setVersion("v1");
			if (param != null && param.size() > 0) {
				for (Entry<String, Object> entry : param.entrySet()) {
					request.putData(entry.getKey(), entry.getValue());
				}
			}
			BusinessResponse response = FwUtils.getBusinessService().doHandler(request);
			System.out.println("code:" + response.getCode());
			System.out.println("msg:" + response.getMsg());
			System.out.println("data:" + FwUtils.getJsonService().toJson(response.getData()));
		} else {
			String url = "/" + service + "/" + method + "/v1";
			String str = HttpUtils.doHandler(url, param);
			System.out.println(str);
		}
	}

	public static String getSaveData() {
		String str = "[{\"id\":\"\",\"title\":\"请您对此项目进行评价\",\"type\":\"baseGauge\",\"required\":true,\"startDesc\":\"非常不满意\",\"endDesc\":\"非常满意\",\"startIndex\":1,\"itemNum\":5},{\"id\":\"\",\"title\":\"请您对以下各行内容进行评价\",\"type\":\"matrixGauge\",\"required\":true,\"startDesc\":\"非常不满意\",\"endDesc\":\"非常满意\",\"startIndex\":1,\"itemNum\":5,\"rowDesc\":[{\"text\":\"标题1\",\"isShow\":false},{\"text\":\"标题2\",\"isShow\":false}]}]";
		return str;
	}

	public static String getSaveData1() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append("{");
		sb.append("\"id\":\"" + 0 + "\",");
		sb.append("\"type\":\"" + "baseText" + "\",");
		sb.append("\"title\":\"" + "填空题标题" + "\",");
		sb.append("\"desc\":\"" + "填空题描述" + "\",");
		sb.append("\"required\":\"" + "2" + "\",");
		sb.append("\"requiredMin\":\"" + "" + "\",");
		sb.append("\"requiredMax\":\"" + "" + "\",");
		sb.append("\"formatType\":\"" + "" + "\"");
		sb.append("},");
		sb.append("{");
		sb.append("\"id\":\"" + 0 + "\",");
		sb.append("\"type\":\"" + "baseTextArea" + "\",");
		sb.append("\"title\":\"" + "简答题标题" + "\",");
		sb.append("\"desc\":\"" + "简答题描述" + "\",");
		sb.append("\"required\":\"" + "2" + "\",");
		sb.append("\"requiredMin\":\"" + "" + "\",");
		sb.append("\"requiredMax\":\"" + "" + "\",");
		sb.append("\"formatType\":\"" + "" + "\"");
		sb.append("},");
		// ====================
		sb.append("{");
		sb.append("\"id\":\"" + 0 + "\",");
		sb.append("\"type\":\"" + "gradeRadio" + "\",");
		sb.append("\"title\":\"" + "单选题标题" + "\",");
		sb.append("\"desc\":\"" + "单选题描述" + "\",");
		sb.append("\"required\":\"" + "2" + "\",");
		sb.append("\"items\":[");
		sb.append("{");
		sb.append("\"text\":\"单选选型1\",");
		sb.append("\"scores\":\"1\"");
		sb.append("},");
		sb.append("{");
		sb.append("\"text\":\"单选选型2\",");
		sb.append("\"scores\":\"2\"");
		sb.append("}");
		sb.append("]");
		sb.append("},");
		// ====================
		sb.append("{");
		sb.append("\"id\":\"" + 0 + "\",");
		sb.append("\"type\":\"" + "gradeCheckbox" + "\",");
		sb.append("\"title\":\"" + "多选题标题" + "\",");
		sb.append("\"desc\":\"" + "多选题描述" + "\",");
		sb.append("\"required\":\"" + "2" + "\",");
		sb.append("\"items\":[");
		sb.append("{");
		sb.append("\"text\":\"多选选型1\",");
		sb.append("\"grade\":\"1\"");
		sb.append("},");
		sb.append("{");
		sb.append("\"text\":\"多选选型2\",");
		sb.append("\"grade\":\"2\"");
		sb.append("}");
		sb.append("]");
		sb.append("},");
		// ====================
		sb.append("{");
		sb.append("\"id\":\"" + 0 + "\",");
		sb.append("\"type\":\"" + "baseGauge" + "\",");
		sb.append("\"title\":\"" + "量表题标题" + "\",");
		sb.append("\"desc\":\"" + "量表题描述" + "\",");
		sb.append("\"required\":\"" + "2" + "\",");
		sb.append("\"itemDesc\":\"" + "满意度" + "\",");
		sb.append("\"startDesc\":\"" + "开始描述" + "\",");
		sb.append("\"endDesc\":\"" + "结束描述" + "\",");
		sb.append("\"startIndex\":\"" + "1" + "\",");
		sb.append("\"itemNum\":\"" + "4" + "\",");
		sb.append("\"showStytle\":\"" + "1" + "\"");
		sb.append("},");
		//
		sb.append("{");
		sb.append("\"id\":\"" + 0 + "\",");
		sb.append("\"type\":\"" + "matrixGauge" + "\",");
		sb.append("\"title\":\"" + "矩阵量表题标题" + "\",");
		sb.append("\"desc\":\"" + "矩阵量表题描述" + "\",");
		sb.append("\"required\":\"" + "2" + "\",");
		sb.append("\"rowDesc\":[");
		sb.append("{\"name\":\"" + "项目1" + "\"},");
		sb.append("{\"name\":\"" + "项目2" + "\"}");
		sb.append("],");
		sb.append("\"startDesc\":\"" + "开始描述" + "\",");
		sb.append("\"endDesc\":\"" + "结束描述" + "\",");
		sb.append("\"startIndex\":\"" + "1" + "\",");
		sb.append("\"itemNum\":\"" + "4" + "\",");
		sb.append("\"showStytle\":\"" + "1" + "\"");
		sb.append("},");
		sb.append("{");
		sb.append("\"id\":\"" + 0 + "\",");
		sb.append("\"type\":\"" + "uploadFile" + "\",");
		sb.append("\"title\":\"" + "文件上传标题" + "\",");
		sb.append("\"desc\":\"" + "文件上传描述" + "\",");
		sb.append("\"required\":\"" + "2" + "\",");
		sb.append("\"requiredMin\":\"" + "" + "\",");
		sb.append("\"requiredMax\":\"" + "" + "\"");
		sb.append("},");
		sb.append("{");
		sb.append("\"id\":\"" + 0 + "\",");
		sb.append("\"type\":\"" + "splitLine" + "\",");
		sb.append("\"title\":\"" + "分割线标题" + "\",");
		sb.append("\"desc\":\"" + "分割线描述" + "\"");
		sb.append("},");
		sb.append("{");
		sb.append("\"id\":\"" + 0 + "\",");
		sb.append("\"type\":\"" + "baseRemark" + "\",");
		sb.append("\"title\":\"" + "备注标题" + "\",");
		sb.append("\"desc\":\"" + "备注描述" + "\"");
		sb.append("},");
		sb.append("{");
		sb.append("\"id\":\"" + 0 + "\",");
		sb.append("\"type\":\"" + "name" + "\",");
		sb.append("\"title\":\"" + "姓名" + "\",");
		sb.append("\"desc\":\"" + "姓名描述" + "\"");
		sb.append("},");
		sb.append("{");
		sb.append("\"id\":\"" + 0 + "\",");
		sb.append("\"type\":\"" + "phone" + "\",");
		sb.append("\"title\":\"" + "手机" + "\",");
		sb.append("\"desc\":\"" + "手机描述" + "\"");
		sb.append("},");
		sb.append("{");
		sb.append("\"id\":\"" + 0 + "\",");
		sb.append("\"type\":\"" + "email" + "\",");
		sb.append("\"title\":\"" + "邮箱" + "\",");
		sb.append("\"desc\":\"" + "邮箱描述" + "\"");
		sb.append("},");
		sb.append("{");
		sb.append("\"id\":\"" + 0 + "\",");
		sb.append("\"type\":\"" + "sex" + "\",");
		sb.append("\"title\":\"" + "性别" + "\",");
		sb.append("\"desc\":\"" + "性别描述" + "\",");
		sb.append("\"items\":[");
		sb.append("{");
		sb.append("\"text\":\"男\",");
		sb.append("\"grade\":\"1\"");
		sb.append("},");
		sb.append("{");
		sb.append("\"text\":\"女\",");
		sb.append("\"grade\":\"2\"");
		sb.append("}]");
		sb.append("},");

		sb.append("{");
		sb.append("\"id\":\"" + 0 + "\",");
		sb.append("\"type\":\"" + "academy" + "\",");
		sb.append("\"title\":\"" + "院校" + "\",");
		sb.append("\"desc\":\"" + "院校描述" + "\"");
		sb.append("},");
		sb.append("{");
		sb.append("\"id\":\"" + 0 + "\",");
		sb.append("\"type\":\"" + "pecialty" + "\",");
		sb.append("\"title\":\"" + "专业" + "\",");
		sb.append("\"desc\":\"" + "专业描述" + "\"");
		sb.append("},");
		sb.append("{");
		sb.append("\"id\":\"" + 0 + "\",");
		sb.append("\"type\":\"" + "industry" + "\",");
		sb.append("\"title\":\"" + "行业" + "\",");
		sb.append("\"desc\":\"" + "行业描述" + "\"");
		sb.append("},");
		sb.append("{");
		sb.append("\"id\":\"" + 0 + "\",");
		sb.append("\"type\":\"" + "profession" + "\",");
		sb.append("\"title\":\"" + "职业" + "\",");
		sb.append("\"desc\":\"" + "职业描述" + "\"");
		sb.append("}");
		sb.append("]");
		return sb.toString();
	}

}
