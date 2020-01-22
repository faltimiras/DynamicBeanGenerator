package cat.altimiras.dynamic;

import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapToVar {

	private Map<String, Field> fields;

	public void init(Class theClass, List<String> fieldsName) throws Exception {
		fields = new HashMap<>(fieldsName.size());
		for (String fieldName : fieldsName) {
			Field field = theClass.getDeclaredField(fieldName);
			field.setAccessible(true);
			fields.put(fieldName, field);
		}
	}

	@RuntimeType
	public void intercept(@RuntimeType Map<String, Object> map, @This Object me) throws Exception {

		for (Map.Entry<String, Object> field : map.entrySet()) {
			Field f = this.fields.get(field.getKey());
			f.set(me, field.getValue());
		}
	}
}
