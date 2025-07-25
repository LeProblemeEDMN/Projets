package loading.LOD;

import loading.Loader;
import loading.NormalObjLoader.NormalMappedObjLoader;
import loading.ObjLoader.objFileLoader;
import loading.RawModel;
import main.MainLoop;

import java.io.*;
import java.util.HashMap;

public class LODManager {
/*
Cette classe manage les LOD. Elle permet de les charger simplement.
Un LOD est un dossier avec un fichier LODInfo.txt et des fichier sous le format .obj représentant les différents niveaux.
Le LODInfo.txt est de la forme suivante sur la première ligne un entier N (le nombre de modèles) puis N ligne de la forme
"nom_du_fichier" seuil
ou le seuil maximum au dela duquel le modèle est remplace par sa version simplifié
les seuil doievnt être CROISSANT.
 */

    public static HashMap<String, RawModel[]> modelsLOD=new HashMap<>();
    public static HashMap<String, float[]> percentsLOD=new HashMap<>();

    public static void loadAllLOD(String dirPath){
        File dir = new File(dirPath);
        for (File file : dir.listFiles()) {
            if(file.isDirectory()) {
                //System.out.println(dirPath + "/" + file.getName());
                loadLOD(dirPath + "/" + file.getName());
            }
        }
    }

    public static void loadLOD(String dirPath){
        BufferedReader reader= null;
        try {
            reader = new BufferedReader(new FileReader(dirPath+"/LODInfo.txt"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            int number=Integer.parseInt(reader.readLine());
            RawModel[] models=new RawModel[number];
            float[] percents=new float[number];
            for(int i=0;i<number;i++){
                String[] line=reader.readLine().split(" ");
                percents[i]=Float.parseFloat(line[1]);
                models[i]=NormalMappedObjLoader.loadOBJ(dirPath+"/"+line[0], MainLoop.LOADER);
                //models[i]=  objFileLoader;
            }
            modelsLOD.put(dirPath,models);
            percentsLOD.put(dirPath,percents);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement du modèle "+dirPath);
            throw new RuntimeException(e);
        }

    }
}
