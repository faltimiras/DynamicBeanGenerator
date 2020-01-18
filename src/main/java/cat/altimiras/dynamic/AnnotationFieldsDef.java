package cat.altimiras.dynamic;

public class AnnotationFieldsDef {

	private String name;
	private Object value;

	public AnnotationFieldsDef(String name, Object value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public Object getValue() {
		return value;
	}
}
