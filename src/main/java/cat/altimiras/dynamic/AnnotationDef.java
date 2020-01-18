package cat.altimiras.dynamic;

import java.util.ArrayList;
import java.util.List;

public class AnnotationDef<T> {

	private String fullName; //must exist
	private List<AnnotationFieldsDef> fieldsDef = new ArrayList<>();
	private T parent;

	AnnotationDef(String fullName, T parent){
		this.fullName = fullName;
		this.parent = parent;
	}

	/*public AnnotationDef(String fullName) {
		this.fullName = fullName;
	}
*/
	public List<AnnotationFieldsDef> getFields() {
		return fieldsDef;
	}

	public AnnotationDef<T> addAnnotationField(AnnotationFieldsDef annotationFieldsDef){
		this.fieldsDef.add(annotationFieldsDef);
		return this;
	}

	public AnnotationDef<T> addAnnotationField(String name, Object value){
		this.fieldsDef.add(new AnnotationFieldsDef(name, value));
		return this;
	}

	public String getFullName() {
		return fullName;
	}

	public T build(){
		return parent;
	}
}
