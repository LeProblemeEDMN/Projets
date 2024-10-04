package Main;

import Animation.animatedModel.AnimatedModel;
import Animation.animation.Animation;
import Animation.loaders.AnimatedModelLoader;
import Animation.loaders.AnimationLoader;
import Entity.Entity;
import Entity.EntityWall;
import Entity.Light.PointLight;
import Loader.LOD.LODLoader;
import Loader.Loader;
import Entity.Light.SpotLight;
import Entity.EntityHerbe;
import Loader.TexturedModel;
import Loader.NormalObjLoader.NormalMappedObjLoader;
import Loader.ObjLoader.objFileLoader;
import Loader.Texture.ModelTexture;
import toolbox.maths.Vector3;
import Loader.RawModel;
import tortue.Tortue;

import java.util.Random;

public class Register {
    public static RenderEngine.gui.font.GUIText text;
    public static RenderEngine.gui.font.FontType type;
    public static Entity dragon;
    public static TexturedModel model;
    public static AnimatedModel animatedModel;
	public static EntityWall falling;
    public static void Init(Loader loader) {

    	
    	 animatedModel= AnimatedModelLoader.loadEntity("res/model.dae", "model");
         Animation animation = AnimationLoader.loadAnimation("res/model.dae");
         animatedModel.doAnimation(animation);
         animatedModel.setPosition(new Vector3(60, 10, 20));


        MainGame.spots.add(new SpotLight(new Vector3(1f,0,0), new Vector3(1,0.001f,0.002f), new Vector3(15, 35, 25), new Vector3(90,0, 0),70,140));
        //MainGame.spots.add(new SpotLight(new Vector3(1f,0,0), new Vector3(1,0.001f,0.002f), new Vector3(15, 35, 25), new Vector3(90,0, 0),70,140));


        MainGame.pointLigths.add(new PointLight( new Vector3(1f,1f,0.8f),new Vector3(1,0,0),new Vector3(100000,150000,-100000),999999999));
       // MainGame.lights.add(MainGame.pointLigths.get(0));
        //	MainGame.pointLigths.add(new PointLight(new Vector3(0,1,0),new Vector3(1,0.0001f,0.0002f), new Vector3(0, 10, 0),40));
        MainGame.pointLigths.add(new PointLight(new Vector3(0,0,1),new Vector3(1,0.0001f,0.0002f), new Vector3(-60, 6, 0),100));
       // MainGame.pointLigths.add(new PointLight(new Vector3(0,1,1),new Vector3(1,0.001f,0.0002f), Tortue.base,100));

        RawModel model= objFileLoader.loadOBJ("res/dragon.obj", loader);
       // RawModel model= LODLoader.LOD("res/dragon.obj");
        //RawModel model= LODLoader.LOD("res/modelTest.obj");
       // model= objFileLoader.loadOBJ("res/modelTest.obj", loader);

        TexturedModel tModel=new TexturedModel(model,new ModelTexture(loader.loadTexture("jadeColor"),""));
        tModel.getTexture().setReflectionMaterial(0.55f);
        tModel.getTexture().setTransparance(true);
     //   tModel.getTexture().setNormalMap(loader.loadTexture("jadeNormal"));

        TexturedModel c=new TexturedModel(objFileLoader.loadOBJ("res/cube.txt", loader),new ModelTexture(loader.loadTexture("dragonTexture"),""));
        c.getTexture().setTransparance(true);
        c.getTexture().setReflectionMaterial(0.8f);

        dragon=new Entity(tModel, new Vector3(15,8,25), 0, 90, 0, 1, "dragon");
        MainGame.entities.add(dragon);
        MainGame.entities.add(new Entity(tModel, new Vector3(50, 0, 60), 0, 00, 0, 1, "dragon2"));
        //MainGame.entities.add(new Entity(tModel, new Vector3(30, 0, -5), 0, 90, 0, 1, "dragon3"));

        TexturedModel c2=new TexturedModel(NormalMappedObjLoader.loadOBJ("res/cube.txt", loader),new ModelTexture(loader.loadTexture("Bricks3"),""));
        c2.getTexture().setTransparance(true);
        c2.getTexture().setReflectivity(0.8f);
        c2.getTexture().setShineDamper(16f);
        c2.getTexture().setNormalMap(loader.loadTexture("Bricks3normale"));

        MainGame.entities.add(new Entity(c, new Vector3(-30f, 10f,-10f), 0, 0, 0, 10, "cube"));
        boolean add2;
        boolean add = MainGame.entities.add((Entity)(new EntityWall(c2, new Vector3(-30, 0, 0), 0,0, 0, new Vector3(80,1,80), "cubeAlea")));
        add2 = MainGame.entities.add(new EntityWall(c2, new Vector3(-10, 40, 0), 0,0, 0, new Vector3(50,1,50), "cubeAlea"));
        MainGame.entities.add(new EntityWall(c2, new Vector3(-10, 0, 0), 0,0, 0, new Vector3(1,40,50), "cubeAlea"));
        MainGame.entities.add(new EntityWall(c2, new Vector3(-10, 0, 0), 0,0, 0, new Vector3(50,40,1), "cubeAlea"));
        MainGame.entities.add(new EntityWall(c2, new Vector3(40, 0, 0), 0,0, 0, new Vector3(1,40,50), "cubeAlea"));
        MainGame.entities.add(new EntityWall(c2, new Vector3(-10, 0, 50), 0,0, 0, new Vector3(50,40,1), "cubeAlea"));

          	  TexturedModel c5=new TexturedModel(NormalMappedObjLoader.loadOBJ("res/cube.txt", loader),new ModelTexture(loader.loadTexture("Bricks3"),""));
              c5.getTexture().setTransparance(true);
              c5.getTexture().setReflectivity(1f);
              c5.getTexture().setShineDamper(32f);
              c5.getTexture().setNormalMap(loader.loadTexture("Bricks3normale"));

        TexturedModel c3=new TexturedModel(model,new ModelTexture(loader.loadTexture("Bricks3"),""));

        c3.getTexture().setReflectivity(0.8f);
        c3.getTexture().setShineDamper(16f);
        c3.getTexture().setReflectionMaterial(0.5f);
        c5.getTexture().setNormalMap(loader.loadTexture("Bricks3normale"));
        Register.model=c5;
        Random random=new Random(37);
        /*for (int i = 0; i< 3000; i++) {
           // MainGame.entities.add(new Entity(c3, new Vector3(random.nextInt(200)-100, random.nextInt(40)-20, random.nextInt(200)-100), 0,random.nextInt(360), 0, (float)Math.random()*2,"drag"));
            //MainGame.entities.add(new Entity(c2, new Vector3(random.nextInt(100)-50, random.nextInt(40)-20, random.nextInt(100)-50), 0, random.nextInt(360), 0, random.nextInt(10), "dragon"+i));
            MainGame.entities.add(new EntityWall(c5, new Vector3(random.nextInt(1000)-500, random.nextInt(40)-20, random.nextInt(1000)-500), 0, random.nextInt(360), 0, new Vector3(random.nextInt(9)+1, random.nextInt(9)+1, random.nextInt(9)+1), "cubeAlea"+i));
        }*/

        for (Entity e:MainGame.entities) {
            MainGame.processEntity(e);
        }
        TexturedModel herbe=new TexturedModel(NormalMappedObjLoader.loadOBJ("res/grass.txt", loader),new ModelTexture(loader.loadTexture("grassTexture"),""));
        herbe.getTexture().setTransparance(true);
        herbe.getTexture().setReflectivity(0.1f);
        herbe.getTexture().setShineDamper(3f);
        herbe.getTexture().setUseFakeLightning(true);
        herbe.setGrass(true);

        TexturedModel groundForest=new TexturedModel(NormalMappedObjLoader.loadOBJ("res/cube.txt", loader),new ModelTexture(loader.loadTexture("GroundForest"),""));
        groundForest.getTexture().setTransparance(true);
        groundForest.getTexture().setReflectivity(0.1f);
        groundForest.getTexture().setShineDamper(0.5f);
        groundForest.getTexture().setNormalMap(loader.loadTexture("GroundForestNRM"));

        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                EntityWall wall = new EntityWall(groundForest, new Vector3(50+i*5, -1, 50+j*5), 0, 0, 0, new Vector3(5, 1, 5), "cubeAlea" );
                MainGame.entities.add(wall);
            }
        }
        Tortue.init(MainLoop.LOADER);
        for (int k = 0; k < 80000; k++) {

            float x=random.nextInt(500)+ (float)Math.random();
            float z=random.nextInt(500)+ (float)Math.random();
            EntityHerbe wall =new EntityHerbe(herbe, new Vector3(50+x, 0, 50+z), 0, random.nextInt(360), 0, new Vector3(1+random.nextFloat()*4, 2+random.nextFloat()*4, 1+random.nextFloat()*4), "cubeAlea" );
            MainGame.entities.add(wall);
            wall.setRenderingDistance(60+((float)Math.pow(random.nextFloat(),5))*130);
        }
        	// MainGame.pointLigths.add(new PointLight( new Vector3(1f,1f,0.9f),new Vector3(1,0.2f,0.002f),new Vector3(-50,4,20),30));
            // MainGame.lights.add(MainGame.pointLigths.get(1));
    
/*
        	TexturedModel c2=new TexturedModel(NormalMappedObjLoader.loadOBJ("res/cube.txt", loader),new ModelTexture(loader.loadTexture("sol"),""));
        	c2.getTexture().setTransparance(true);
            c2.getTexture().setReflectivity(1f);
            c2.getTexture().setShineDamper(32f);
          //  c2.getTexture().setNormalMap(loader.loadTexture("normalMap"));
            
            TexturedModel armoire=new TexturedModel(NormalMappedObjLoader.loadOBJ("res/cube.txt", loader),new ModelTexture(loader.loadTexture("plafond"),""));
            TexturedModel mur=new TexturedModel(NormalMappedObjLoader.loadOBJ("res/cube.txt", loader),new ModelTexture(loader.loadTexture("Bricks3"),""));
            mur.getTexture().setTransparance(true);
            mur.getTexture().setReflectivity(1f);
            mur.getTexture().setShineDamper(32f);
            mur.getTexture().setNormalMap(loader.loadTexture("Bricks3normale"));
            armoire.getTexture().setTransparance(true);
            armoire.getTexture().setRefractionMaterial(0f);
            armoire.getTexture().setReflectivity(1f);
            armoire.getTexture().setShineDamper(32f);
         //   armoire.getTexture().setNormalMap(loader.loadTexture("normalMap"));
            
            TexturedModel vitre=new TexturedModel(NormalMappedObjLoader.loadOBJ("res/cube.txt", loader),new ModelTexture(loader.loadTexture("plafond"),""));
            vitre.getTexture().setTransparance(true);
            vitre.getTexture().setReflectivity(1f);
            vitre.getTexture().setShineDamper(32f);
            vitre.getTexture().setRefractionMaterial(0.6f);
          //  vitre.getTexture().setNormalMap(loader.loadTexture("normalMap"));
            
            TexturedModel c3=new TexturedModel(NormalMappedObjLoader.loadOBJ("res/cube.txt", loader),new ModelTexture(loader.loadTexture("meuble"),""));
            c3.getTexture().setTransparance(true);
            c3.getTexture().setReflectivity(1f);
            c3.getTexture().setShineDamper(32f);
            //c3.getTexture().setNormalMap(loader.loadTexture("normalMap"));

            MainGame.entities.add(new EntityWall(c2, new Vector3(-80, -18, 0), 0,0, 0, new Vector3(80,1,80), "sol"));
            MainGame.entities.add(new EntityWall(c2, new Vector3(-80, 5, 0), 0,0, 0, new Vector3(80,1,80), "plafond"));
        	MainGame.entities.add(new EntityWall(mur, new Vector3(-80, -18, -1), 0,0, 0, new Vector3(25.2,25,1), "mur"));
        	MainGame.entities.add(new EntityWall(armoire, new Vector3(-80+25.2, -18, -1), 0,0, 0, new Vector3(1,25,18), "armoire"));
        	MainGame.entities.add(new EntityWall(mur, new Vector3(-80+25.2, -18, 17), 0,0, 0, new Vector3(1,25,2), "armoire"));

        	MainGame.entities.add(new EntityWall(mur, new Vector3(-83, -18, 0), 0,0, 0, new Vector3(3,25,25.7), "mur"));
        //	MainGame.entities.add(new EntityWall(vitre, new Vector3(-81, -18, 25.7), 0,0, 0, new Vector3(1,25,15), "vitre"));
        	MainGame.entities.add(new EntityWall(mur, new Vector3(-83, -18, 40.7), 0,0, 0, new Vector3(3,25,9.3), "mur"));
        	MainGame.entities.add(new EntityWall(mur, new Vector3(-80, -18, 50), 0,0, 0, new Vector3(35,25,1), "mur"));
        	MainGame.entities.add(new EntityWall(armoire, new Vector3(-80+30, -18, 49), 0,0, 0, new Vector3(5,5,1), "radia"));
        	MainGame.entities.add(new EntityWall(mur, new Vector3(-80+35, -18, 50), 0,0, 0, new Vector3(5,15,1), "mur"));
        //	MainGame.entities.add(new EntityWall(vitre, new Vector3(-80+35, -3, 50), 0,0, 0, new Vector3(5,10,1), "vitre"));
        	MainGame.entities.add(new EntityWall(mur, new Vector3(-80+40, -18, 50), 0,0, 0, new Vector3(6.5,25,1), "mur"));
        	MainGame.entities.add(new EntityWall(mur, new Vector3(-80+46.5, -18, 20), 0,0, 0, new Vector3(1,25,30), "mur"));
        	MainGame.entities.add(new EntityWall(armoire, new Vector3(-80+36.5, -18, 20), 0,0, 0, new Vector3(10,25,1), "cuisine"));
        	MainGame.entities.add(new EntityWall(mur, new Vector3(-80+35.5, -18, 0), 0,0, 0, new Vector3(1,25,24), "mur"));

        	MainGame.entities.add(new EntityWall(c3, new Vector3(-80, -18, 4), 0,0, 0, new Vector3(20,6,15), "lit"));
        	MainGame.entities.add(new EntityWall(c3, new Vector3(-80+42, -18, 25.5), 0,0, 0, new Vector3(4.5,15,24.5), "commode"));
        	//MainGame.entities.add(new EntityWall(c3, new Vector3(-80, -18, 41), 0,0, 0, new Vector3(10.4,8,9), "tele"));
        	//MainGame.entities.add(new EntityWall(c3, new Vector3(-80+24.7, -10, 33.5), 0,0, 0, new Vector3(10.3,1,16.5), "table"));
        	//MainGame.entities.add(new EntityWall(c3, new Vector3(-80+24.9, -18, 33.7), 0,0, 0, new Vector3(1,8,1), "table"));
        	//MainGame.entities.add(new EntityWall(c3, new Vector3(-80+33.8, -18, 33.7), 0,0, 0, new Vector3(1,8,1), "table"));
        	//MainGame.entities.add(new EntityWall(c3, new Vector3(-80+24.9, -18, 48.7), 0,0, 0, new Vector3(1,8,1), "table"));
        	//MainGame.entities.add(new EntityWall(c3, new Vector3(-80+33.8, -18, 48.7), 0,0, 0, new Vector3(1,8,1), "table"));

        //	MainGame.entities.add(new EntityWall(c3, new Vector3(-80, -18, 25), 0,0, 0, new Vector3(20,5,10), "banq1"));
        //	MainGame.entities.add(new EntityWall(c3, new Vector3(-80, -13, 25), 0,0, 0, new Vector3(20,5,5), "banq2"));


        	MainGame.entities.add(new EntityWall(c3, new Vector3(-80, -18, 42.8), 0,0, 0, new Vector3(4.4,8,7.2), " pas commode chambre"));
        	MainGame.entities.add(new EntityWall(c3, new Vector3(-80+10, -18, 40), 0,0, 0, new Vector3(20,5,10), "banq1"));
            MainGame.entities.add(new EntityWall(c3, new Vector3(-80+10, -13, 45), 0,0, 0, new Vector3(20,5,5), "banq2"));
        	MainGame.entities.add(new EntityWall(c3, new Vector3(-80+15, -10, 25), 0,0, 0, new Vector3(10,1,10), "table"));
        	MainGame.entities.add(new EntityWall(c3, new Vector3(-80+15.2, -18, 25.1), 0,0, 0, new Vector3(1,8,1), "table"));
        	MainGame.entities.add(new EntityWall(c3, new Vector3(-80+23.8, -18, 25.1), 0,0, 0, new Vector3(1,8,1), "table"));
        	MainGame.entities.add(new EntityWall(c3, new Vector3(-80+15.2, -18, 33.7), 0,0, 0, new Vector3(1,8,1), "table"));
        	MainGame.entities.add(new EntityWall(c3, new Vector3(-80+23.8, -18, 33.7), 0,0, 0, new Vector3(1,8,1), "table"));

        	falling=new EntityWall(c2, new Vector3(10, 30, 00), 0,0, 0, new Vector3(5,5,5), "falling");
         MainGame.entities.add(falling);
*/



    }



}
