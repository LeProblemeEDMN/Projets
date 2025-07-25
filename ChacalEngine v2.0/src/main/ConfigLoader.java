package main;

import toolbox.maths.Vector3;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigLoader {
/*
Load a config file
 */
Map<String, String>parameters=new HashMap<>();
	public ConfigLoader(String configPath) {
		FileReader reader=null;
		try {
			reader=new FileReader(new File(configPath));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedReader bufferedReader=new BufferedReader(reader);
		try {
			String line=bufferedReader.readLine();
			while (line!=null) {
				if(!line.startsWith("//") && line.length()>2) {
				String[]param=line.split(":");
				//System.out.println(line);
				parameters.put(param[0], param[1]);
				}
				line=bufferedReader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public static List<String> getLine(String path) {
		FileReader reader=null;
		try {
			reader=new FileReader(new File(path));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedReader bufferedReader=new BufferedReader(reader);
		List<String> lines=new ArrayList<>();
			try {
				String line=bufferedReader.readLine();
				while (line!=null) {
					lines.add(line);
					line=bufferedReader.readLine();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return lines;
	}
	
	
	public String getStringParameter(String key) {
		return parameters.get(key);	
	}
	public float getFloatParameter(String key) {
		String value=getStringParameter(key);
		if(value!=null) {
			return Float.parseFloat(value);
		}else {
			return 0;
		}
	}
	public int getIntParameter(String key) {
		String value=getStringParameter(key);
		if(value!=null) {
			return Integer.parseInt(value);
		}else {
			return 0;
		}
	}
	public Vector3 getVector3Parameter(String key) {
		String value=getStringParameter(key);
		if(value!=null) {
			String[]values=value.split(" ");
			return StringToVector(values, 0);
		}else {
			return new Vector3();
		}
	}
	public boolean getBooleanParameter(String key) {
		String value=getStringParameter(key);
		if(value!=null&&value.equals("true")) {
			return true;
		}else {
			return false;
		}
	} 
	public static Vector3 StringToVector(String[] nb,int index) {
		return new Vector3(Float.parseFloat(nb[index]),Float.parseFloat(nb[index+1]),Float.parseFloat(nb[index+2]));
	}
	public static String vectorToString(Vector3 v) {
		return v.x+" "+v.y+" "+v.z+" ";
	}
}
