package loading.ObjLoader;

import loading.Loader;
import loading.Texture.ModelTexture;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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

					}
				
				if(line.startsWith("map_Kd ")) {
					String path=line.substring(7);
					path=path.substring(0,path.length()-4);
					
					textures.get(id).setTextureId(loader.loadTexture(path));
				}
				if(line.startsWith("map_Ks ")) {
					String path=line.substring(7);
					path=path.substring(0,path.length()-4);
					textures.get(id).setARMMap(loader.loadTexture(path));
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
