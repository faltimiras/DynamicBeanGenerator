package cat.altimiras.dynamic;

import java.util.ArrayList;
import java.util.List;

public class ClassDef {

	private String fullname;
	private List<AnnotationDef<ClassDef>> annotations = new ArrayList<>();
	private List<FieldDef> fields = new ArrayList<>();

	public ClassDef() {
	}

	public ClassDef(String fullname) {
		this.fullname = fullname;
	}

	public ClassDef(String fullname, List<FieldDef> fields) {
		this.fullname = fullname;
		this.fields = fields;
	}

	public String getFullname() {
		return fullname;
	}

	public List<FieldDef> getFields() {
		return fields;
	}

	public List<AnnotationDef<ClassDef>> getAnnotations() {
		return annotations;
	}

	public AnnotationDef<ClassDef> addAnnotation(String fullname) {
		AnnotationDef<ClassDef> annotationDef = new AnnotationDef(fullname, this);
		this.annotations.add(annotationDef);
		return annotationDef;
	}

	public FieldDef addField(String name, String type) {
		FieldDef fieldDef = new FieldDef(name, type, this);
		this.fields.add(fieldDef);
		return fieldDef;
	}

	public FieldDef addField(String name, ClassDef def) {
		FieldDef fieldDef = new FieldDef(name, def, this);
		this.fields.add(fieldDef);
		return fieldDef;
	}

	public FieldDef addField(FieldDef fieldDef) {
		this.fields.add(fieldDef);
		return fieldDef;
	}
}