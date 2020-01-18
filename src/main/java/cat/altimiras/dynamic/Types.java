package cat.altimiras.dynamic;

public class Types {

	public static String getFullName(String name){
		if (name.equalsIgnoreCase("String")){
			return String.class.getName();
		}
		else if (name.equalsIgnoreCase("int")){
			return int.class.getName();
		}
		else if (name.equals("long")){
			return long.class.getName();
		}
		else if (name.equals("double")){
			return double.class.getName();
		}
		else if (name.equals("float")){
			return float.class.getName();
		}
		else if (name.equals("boolean")){
			return boolean.class.getName();
		}
		else if (name.equals("byte")){
			return byte.class.getName();
		}
		else if (name.equalsIgnoreCase("integer")){
			return Integer.class.getName();
		}
		else if (name.equals("Long")){
			return Long.class.getName();
		}
		else if (name.equals("Double")){
			return Double.class.getName();
		}
		else if (name.equals("Float")){
			return Float.class.getName();
		}
		else if (name.equals("Boolean")){
			return Boolean.class.getName();
		}
		else if (name.equals("Byte")){
			return Byte.class.getName();
		}
		return name;
	}
}
