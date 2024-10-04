package Entity;

import Loader.TexturedModel;
import toolbox.maths.Vector3;

public class EntityHerbe extends EntityWall{
    public Vector3 originalPos;


    public EntityHerbe(TexturedModel texturedModel, Vector3 position, float rotX, float rotY, float rotZ, Vector3 scale, int textureindex, String name) {
        super(texturedModel, position, rotX, rotY, rotZ, scale, textureindex, name);
        this.originalPos=position.getMul(1);
    }

    public EntityHerbe(TexturedModel texturedModel, Vector3 position, float rotX, float rotY, float rotZ, Vector3 scale, String name) {
        super(texturedModel, position, rotX, rotY, rotZ, scale, name);
        this.originalPos=position.getMul(1);
    }



}
