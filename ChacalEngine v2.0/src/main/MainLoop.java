package main;

import GUI.GuiManager;
import loading.Loader;
import org.joml.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import toolbox.InputManager;
import toolbox.XMLParser.XMLParser;
import toolbox.maths.Maths;
import toolbox.maths.Vector3;

import java.io.IOException;
import java.util.Random;

public class MainLoop {
    /*
    + Important:
        -> gerer la taille max des carac pr pas qu'il prennent tt l'ecran
        -> finir SSGI et faire Screen space relfection
        -> charger les GUI et les scènes a partir de fichier et créer un éditeur
        -> volumetric lighning (god rays et brouillard->sample le long du rayon la lumiere) -> nuages??
        -> GUI -> Performance GUI, gerer le resize, ajout structure pour gestion (type colonne ligne ...)
        -> autres lumières (ombres et gestion desquelles sont rendues).
                -> faire les ombres
                -> choisir qui a des ombres
        -> terrain gerer LOD-> perlin noise
        -> outil mesure performances
        -> particules
        -> utiliser une skybox par scene et pas une par shader
        -> faire compilateur shader avec macro (remplacer taille tableau a la compilation par exemple) et fournir fonctions de base.
        -> meilleur ssao utilisant les normales. ou HBAO voir le top SSBO

    A regarder plus en détail:
        -> lumieres locale pr rendu realiste (light probes)
        -> DLSS
        -> sperical harmonics
        -> bindless texture
        -> tesselation
        -> HDR et bloom
        -> eviter la recompilation des shader GLSL
        -> voir arealight pr les lumieres non ponctuelles (type neon)

    TODO
     -doc

     -Ameliorer ombres
        cascaded shadowmap : faire attention aux modele chevauchant deux box-> optimiser detection quelle box a partir distance plus grande et plus  petite a la cam
        varaiance shadow ma pfor other cascaded shadow map
     -transparence
     -collisions modèles
     -> optim chargement texture pr ne pas tjr utiliser les 4 channels/ fusionner texture pr utiliser tt les channels
     -> FXAA corriger pixel qui n'est pas blend et fait dent de couteau ou autre methode anti aliasing (MLAA)
     -> .obj en binaire et texture en binaire?
     -> Eau
     -> animations

     Moins important
     -> forward + choisit quelles lumières sont utilise pr chaque pixel dynamiquement
            Pr chaque groupe de pixel on calcule une bounding box3D et on va regarder quelles lumières l'influence.
     -stencil buffer-> shadow volume (semble ne plus exister), ne pas rendre sous les UI,choisir ou ne pas rendre une partie de l'ecran
     -LOD edge collapse (quadratic error) et seuil automatiques
     -instance rendering changer lumières du modèle
     ->bloom
     */
    /*
    DEJA fait:
    parallax, displacement mapping,FXAA,SSAO,deffered rendering SSGI,SSr,LOD

    SPEED UP
    -> depth map avec 32 bit et reduire offset pr enlever plus de triangles.
    -> ameliorer generation des matrices de transformation 8% a 10% (si translation alors seulement modifer ce qu'il faut
    ->optim can be seen de camera ->15%
    -> texture atlas
     */
    public static Loader LOADER;
    public static void main(String[] args) throws IOException {


        ConfigLoader config = new ConfigLoader("res/config.txt");

        DisplayManager.init(config);
        //82 fps ss depth
        // 76 fps avec depth ss test
        //LOD

        //TextureUtils.mergeTexture("res/red.png","res/textures/montagne/roughness.jpg","res/textures/montagne/metallic.jpg",2048,2048,"res/textures/montagne/ARM.png");
        //LOD.LOD("res/LOD/montagne/montagne6.obj","res/LOD/montagne/montagne7.obj",7500);
        Loader loader = new Loader();
        LOADER=loader;

        /*BufferedImage img= ImageIO.read(new File("res/hmbase.png"));
        float[][] heights=new float[img.getWidth()][img.getHeight()];
        float[][] coeffs=new float[img.getWidth()][img.getHeight()];
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                heights[i][j]=new Color(img.getRGB(i, j)).getRed()/255.0f;
                coeffs[i][j]=new Color(img.getRGB(i, j)).getGreen()/255.0f;
            }
        }

        int iter=10000;
        for (int ite = 0; ite < iter; ite++) {
            for (int i = 1; i < img.getWidth()-1; i++) {
                for (int j = 1; j < img.getHeight()-1; j++) {
                    if(coeffs[i][j]>0){
                        float ave = heights[i][j]+heights[i - 1][j] + heights[i + 1][j] + heights[i][j + 1] + heights[i][j - 1];
                        ave *= 0.2f;
                        float diff = ave - heights[i][j];
                        heights[i][j] += coeffs[i][j] * diff;
                    }
                }
            }
        }

        BufferedImage out=new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                out.setRGB(x, y, new Color(heights[x][y],heights[x][y],heights[x][y]).getRGB());
            }
        }
        ImageIO.write(out, "jpg", new File("res/height_map.png"));*/

        //charge le monde
        InputManager.init();

        World.fill(loader);

        Pipeline.init(config);


        System.out.println("Debut");
        int frames=0;
        long last_update=System.currentTimeMillis();
        while (!DisplayManager.isClosed()){
            World.update();
            Pipeline.render();
            DisplayManager.update();
            frames++;
            if(System.currentTimeMillis()-last_update>1000){
                System.out.println("FPS: "+frames);
                last_update=System.currentTimeMillis();
                frames=0;
            }

            //redimenssionement
            if(DisplayManager.must_resize)
                Pipeline.resize(DisplayManager.getWidth(), DisplayManager.getHeight());
            if(GuiManager.guiOptions.modif)GuiManager.guiOptions.apply_change();
            DisplayManager.must_resize=false;
        }
        Pipeline.cleanup();
    }
    public static void test(){
        Random r=new Random();
        for (int i = 0; i < 100000000; i++) {
            float rx= r.nextFloat()*360;
            float ry= r.nextFloat()*360;
            float rz= r.nextFloat()*360;

            Maths.createTransfromationMatrix(new Vector3f(r.nextFloat(),r.nextFloat(),r.nextFloat()*5-10),rx,ry,rz,new Vector3(1.0f,1.0f,1.0f));
            Maths.rotation(new Vector3(r.nextFloat(),r.nextFloat(),r.nextFloat()*5-10),rx,ry,rz,1);

        }
    }
}

