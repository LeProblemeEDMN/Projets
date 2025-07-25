package simulation;

import entity.Entity;
import loading.TexturedModel;
import main.World;
import toolbox.maths.Vector3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SoftBody {


    public static List<Entity> body_entity=new ArrayList<>();

    public SoftBody(TexturedModel model,int nx,int ny) {
        Random rand = new Random(1);

        for (int i = 0; i < nx; i++) {
            for (int j = 0; j < ny; j++) {

                Entity e=new Entity(model,new Vector3( rand.nextDouble(-150,150), rand.nextDouble(-0,150),rand.nextDouble(-150,150)),0,0,0,8);
                World.entities.add(e);
                body_entity.add(e);
            }
        }
    }

    public void update(double dt){
        for(Entity e:body_entity){
            e.setRotY(e.getRotY()-(float) dt*100);
            e.update();
        }
    }
}
