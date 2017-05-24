package com.weixue.Model;
/**
 * 接口返回的结果和状态
 * @author chenjunjie
 *
 */
public class Response {
	
	private String result;
	private int status;
	private String message;
	
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

}
