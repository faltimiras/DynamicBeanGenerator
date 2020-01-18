package cat.altimiras.dynamic;

import java.util.ArrayList;
import java.util.List;

public class FieldDef {

	private String name;
	private String type;
	private ClassDef nested;
	private ClassDef parent;
	private List<AnnotationDef<FieldDef>> annotations = new ArrayList<>();

	public FieldDef() {
	}


	FieldDef(String name, String type, ClassDef parent) {
		this.name = name;
		this.type = Types.getFullName(type);
		this.parent = parent;
	}

	FieldDef(String name, ClassDef def, ClassDef parent) {
		this.name = name;
		this.nested = def;
		this.parent = parent;
	}

	public List<AnnotationDef<FieldDef>> getAnnotations() {
		return annotations;
	}

	public AnnotationDef<FieldDef> addAnnotation(String fullname){
		AnnotationDef annotationDef = new AnnotationDef(fullname, this);
		this.annotations.add(annotationDef);
		return annotationDef;
	}

	public boolean isSimple() {
		return this.nested == null;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public ClassDef getNested() {
		return nested;
	}

	public ClassDef build(){
		return parent;
	}

}
