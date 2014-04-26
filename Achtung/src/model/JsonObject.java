package model;
import com.google.gson.annotations.Expose;

public class JsonObject {
	@Expose
	private Object data;
	@Expose
	private String event;

	public JsonObject(String event, Object data) {
		this.data = data;
		this.event = event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getEvent() {
		return event;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Object getData() {
		return data;
	}

}
