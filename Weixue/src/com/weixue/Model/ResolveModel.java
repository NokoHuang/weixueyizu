package com.weixue.Model;

import java.io.Serializable;
import java.util.List;

public class ResolveModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1490698661175795251L;

	private String Message;
	
	private List<ResultModel> Result;
	
	private int Status;

	public String getMessage() {
		return Message;
	}

	public void setMessage(String message) {
		Message = message;
	}

	public List<ResultModel> getResult() {
		return Result;
	}

	public void setResult(List<ResultModel> result) {
		Result = result;
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}
	
}
