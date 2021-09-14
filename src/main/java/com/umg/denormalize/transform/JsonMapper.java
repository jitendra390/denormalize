package com.umg.denormalize.transform;

import java.io.Serializable;
import java.lang.reflect.Field;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.values.KV;
import com.google.gson.Gson;

public class JsonMapper<T extends Serializable> extends DoFn<String, KV<String, T>> {

  /**
  * 
  */
  private static final long serialVersionUID = -8073913381876607585L;
  private transient Gson gson;
  private String className;
  private String fieldName;

	public JsonMapper(String className, String fieldName) {
      this.className = className;
		this.fieldName = fieldName;
	}
	
    @Setup
    public void setUp() {
      gson = new Gson();
    }
	
    @SuppressWarnings("unchecked")
    @ProcessElement
    public void processElement(ProcessContext c) throws ClassNotFoundException,
        NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
      Class<?> className = Class.forName(this.className);
      Field field = className.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      String value = (String) field.get(fieldName);
      Class<T> tracks = (Class<T>) gson.fromJson(c.element().toString(), className.getClass());
      c.output((KV<String, T>) KV.of(value, tracks));
	}
}
