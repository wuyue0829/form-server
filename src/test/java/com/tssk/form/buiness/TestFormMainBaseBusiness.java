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

public class TestFormMainBaseBusiness {

	public static void main(String[] args) {
		boolean isLocal =  true;
		if(isLocal){
			init();
		}
		String service = "FormMainBaseBusiness";
//		add(isLocal,service);
//		addRequirementsMet(isLocal,service);
//		addCourseQuality(isLocal,service);
//		addByTemplate(isLocal,service);
//		modify(isLocal,service);
//		modifyRequirementsMet(isLocal,service);
		modifyCourseQuality(isLocal,service);
//		remove(isLocal,service);
//		query(isLocal,service);
//		list(isLocal,service);
	}

	public static void init() {
		Map<String, Object> cfg = new HashMap<String, Object>();
		cfg.put(HttpConst.HTTP_MODULE_ENABLE, "false");
		Launcher.start(cfg);
	}
	
	public static void add(boolean isLocal,String service){
		String method = "add";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("classifyId", "676810636247699456");
		param.put("formName", "毕业生就业岗位调查");
		param.put("startTime", "2019-08-01 10:30:00");
		param.put("endTime", "2019-08-28 10:45:00");
		param.put("sendNum", "100");
		param.put("receiveNum", "98");
		param.put("status", 1);
		param.put("rowSort", 300);
		param.put("remarks", "我是备注信息");
		param.put("endWord", "我是结束描述信息");
		test(isLocal, service, method, param);
	}
	
	public static void addRequirementsMet(boolean isLocal,String service){
		String method = "addRequirementsMet";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("classifyId", "676837688023715840");
		param.put("formName", "2018年03班毕业达成调查");
		param.put("ext1", "培养方案版本001");
		param.put("ext2", "16级");
		param.put("startTime", "2019-08-01 10:30:00");
		param.put("endTime", "2019-08-28 10:45:00");
		param.put("sendNum", "100");
		param.put("receiveNum", "98");
		param.put("status", 1);
		param.put("rowSort", 300);
		param.put("remarks", "我是备注信息");
		param.put("endWord", "我是结束描述信息");
		test(isLocal, service, method, param);
	}
	
	public static void addCourseQuality(boolean isLocal,String service){
		String method = "addCourseQuality";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("classifyId", "676837836564992000");
		param.put("formName", "2018年03班毕业达成调查");
		param.put("ext1", "培养方案版本001");
		param.put("ext2", "16级");
		param.put("ext3", "我是评价课程");
		param.put("ext4", "1");
		param.put("startTime", "2019-08-01 10:30:00");
		param.put("endTime", "2019-08-28 10:45:00");
		param.put("sendNum", "100");
		param.put("receiveNum", "98");
		param.put("status", 1);
		param.put("rowSort", 300);
		param.put("remarks", "我是备注信息");
		param.put("endWord", "我是结束描述信息");
		test(isLocal, service, method, param);
	}
	
	public static void addByTemplate(boolean isLocal,String service){
		String method = "addByTemplate";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("templateId", 123);
	 
		test(isLocal, service, method, param);
	}
	
	public static void modify(boolean isLocal,String service){
		String method = "modify";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("id", "676840006328782848");
		param.put("classifyId", "676810636247699456");
		param.put("formName", "毕业生就业岗位调查");
		param.put("startTime", "2019-08-01 10:30:00");
		param.put("endTime", "2019-08-29 10:45:00");
		param.put("sendNum", "100");
		param.put("receiveNum", "98");
		param.put("status", 1);
		param.put("rowSort", 300);
		param.put("remarks", "我是备注信息");
		param.put("endWord", "我是结束说明");
		test(isLocal, service, method, param);
	}
	
	public static void modifyRequirementsMet(boolean isLocal,String service){
		String method = "modifyRequirementsMet";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("id", "676846902393704448");
		param.put("classifyId", "676837688023715840");
		param.put("formName", "2018年03班毕业达成调查");
		param.put("ext1", "培养方案版本001");
		param.put("ext2", "16级");
		param.put("startTime", "2019-08-01 10:30:00");
		param.put("endTime", "2019-08-29 10:45:00");
		param.put("sendNum", "100");
		param.put("receiveNum", "98");
		param.put("status", 1);
		param.put("rowSort", 300);
		param.put("remarks", "我是备注信息");
		param.put("endWord", "我是结束描述信息");
		test(isLocal, service, method, param);
	}
	
	public static void modifyCourseQuality(boolean isLocal,String service){
		String method = "modifyCourseQuality";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("id", "676847243390619648");
		param.put("classifyId", "676837836564992000");
		param.put("formName", "2018年03班毕业达成调查");
		param.put("ext1", "培养方案版本001");
		param.put("ext2", "16级");
		param.put("ext3", "我是评价课程");
		param.put("ext4", "1");
		param.put("startTime", "2019-08-01 10:30:00");
		param.put("endTime", "2019-08-29 10:45:00");
		param.put("sendNum", "100");
		param.put("receiveNum", "98");
		param.put("status", 1);
		param.put("rowSort", 300);
		param.put("remarks", "我是备注信息");
		param.put("endWord", "我是结束描述信息");
		test(isLocal, service, method, param);
	}
	
	public static void query(boolean isLocal,String service){
		String method = "query";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("id", 674389438385623040l);
		test(isLocal, service, method, param);
	}
	
	public static void list(boolean isLocal,String service){
		String method = "listUnSubmit";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("page", 1);
		param.put("pageSize", 15);
		test(isLocal, service, method, param);
	}

	public static void remove(boolean isLocal,String service){
		String method = "remove";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("id", 674389438385623040l);
		test(isLocal, service, method, param);
	}
	
	public static void test(boolean isLocal,String service,String method,Map<String, Object> param) {
		if(isLocal){
			BusinessRequest request = new BusinessRequest();
			request.setService(service);
			request.setMethod(method);
			request.setVersion("v1");
			if(param!=null && param.size()>0){
				for(Entry<String, Object> entry:param.entrySet()){
					request.putData(entry.getKey(), entry.getValue());
				}
			}
			BusinessResponse response = FwUtils.getBusinessService().doHandler(request);
			System.out.println("code:"+response.getCode());
			System.out.println("msg:"+response.getMsg());
			System.out.println("data:"+response.getData());
		}else{
			String url = "/"+service+"/"+method+"/v1";
			String str = HttpUtils.doHandler(url, param);
			System.out.println(str);
		}
	}

}
