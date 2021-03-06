package fr.umv.java.inside;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main {

     private static String propertyName(String name) {
       return Character.toLowerCase(name.charAt(3)) + name.substring(4);
     }

     private static Object callGetter(Object o, Method g) {
     	Object object = Objects.requireNonNull(o);
     	Method getter = Objects.requireNonNull(g);
     	try {
     		return getter.invoke(object);
     	}
     	catch (IllegalAccessException e) {
     		throw new IllegalStateException(e);
     	}
     	catch (InvocationTargetException e) {
     		var cause = e.getCause();
     		if (cause instanceof RuntimeException) {
     			throw (RuntimeException) cause;
     		}
     		else if (cause instanceof Error) {
     			throw (Error) cause;
     		}
     		else {
     			throw new UndeclaredThrowableException(cause);
     		}
     	}
     }

     public static String getMethodName(Method g) {
     	Method getter = Objects.requireNonNull(g);
     	JSONProperty annotation = getter.getAnnotation(JSONProperty.class);
     	if (annotation.value().equals(""))
     		return propertyName(getter.getName());
     	else
     		return annotation.value();
     }
     
     private static final ClassValue<Function<Object, String>> cache = new ClassValue<>() {
    	 
    	 @Override
    	 protected Function<Object, String> computeValue(Class<?> myClass) {
    		 
    		 return myObject -> Arrays.stream(myClass.getMethods())
    				 				.filter(method -> method.isAnnotationPresent(JSONProperty.class))
    				 				.sorted(Comparator.comparing(method -> method.getName()))
    				 				.map((method) -> (getMethodName(method) + " : " + callGetter(object, method)))
    				 				.collect(Collectors.joining(",", "{","}"));
    	 }
    	 
    };

	  public static String toJSON(Object o) {
		  
		  Object object = Objects.requireNonNull(o);
		  return cache
				  .get(object.getClass())
				  .apply(object);
	}
}



/*
public static String toJSON(Object o) {
  	Object object = Objects.requireNonNull(o);
  	
  	final ClassValue<Method[]> classValue = new ClassValue<Method[]>() {
		@Override
		protected Method[] computeValue(Class<?> type) {
			return type.getMethods();
		}
  	};
  	
  	Method[] methods = classValue.get(object.getClass());

  	return Arrays
  			.stream(methods)
  				.filter(method -> method.isAnnotationPresent(JSONProperty.class))
  				.map(method -> getMethodName(method) + " : " + callGetter(object, method))
  				.collect(Collectors.joining(", ", "{ ", " }"));

<<<<<<< HEAD
	  	final ClassValue<Method[]> classValue = new ClassValue<Method[]>() {
			@Override
			protected Method[] computeValue(Class<?> type) {
				return type.getMethods();
			}
	  	};

	  	Method[] methods = classValue.get(object.getClass());

	  	return Arrays.stream(methods)
	  			.filter(method -> method.getName().startsWith("get") && 
	  					method.isAnnotationPresent(JSONProperty.class))
	  			.map(method -> getMethodName(method) + " : " + callGetter(object, method))
	  			.collect(Collectors.joining(", ", "{ ", " }"));

	  	}
=======
}

>>>>>>> 5dc15b643b113004a7c8424ef5c24e0ae9766e5a

    		 var methods = Arrays.stream(myClass.getMethods())
    	             		.filter(method -> method.isAnnotationPresent(JSONProperty.class))
    	             		.sorted(Comparator.comparing(m -> m.getName()))
    	             		.collect(Collectors.toList());
    		 
*/