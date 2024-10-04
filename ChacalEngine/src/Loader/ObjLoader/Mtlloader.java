package Loader.ObjLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Loader.Loader;
import Loader.Texture.ModelTexture;

public class Mtlloader {
	public static List<ModelTexture> loadMTL(String mtlFileName, Loader loader) {
        FileReader isr = null;
        List<ModelTexture>textures=new ArrayList<>();
        File mtlFile = new File( mtlFileName );
        try {
            isr = new FileReader(mtlFile);
        } catch (FileNotFoundException e) {
            System.err.println("File not found in res folder!");
            System.exit(-1);
        }
        BufferedReader reader = new BufferedReader(isr);
        String line;
        int id=-1;
            try {           	
            while (true) {
				line = reader.readLine();
				if(line==null) {
					break;
				}
				
				if(line.startsWith("newmtl ")) {
					id++;
					textures.add(new ModelTexture());
					textures.get(id).setName(line.substring(7));
					}
				
				if(line.startsWith("map_Kd ")) {
					String path=line.substring(7);
					path=path.substring(0,path.length()-4);
					
					textures.get(id).setTextureId(loader.loadTexture(path),path);
				}
				if(line.startsWith("map_Ks ")) {
					String path=line.substring(7);
					path=path.substring(0,path.length()-4);
					textures.get(id).setSpecularMap(loader.loadTexture(path),path);
				}
				
				if(line.startsWith("Ns ")) {
					String[] lines=line.split(" ");
					textures.get(id).setShineDamper(Float.parseFloat(lines[1])/10);
					textures.get(id).setReflectivity(Float.parseFloat(lines[1])/100);
				}
				if(line.startsWith("d ")) {
					String[] lines=line.split(" ");
					float nb=Float.parseFloat(lines[0]);
					if(nb<0.5f) {
						textures.get(id).setTransparance(true);
						textures.get(id).setUseFakeLightning(true);
					}
				}
				
				
            	  }
			} catch (IOException e) {
				
				e.printStackTrace();
			}
            
        return textures;
	}
}
