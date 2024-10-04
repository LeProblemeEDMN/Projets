package Init.Clouds;

import Main.MainGame;
import toolbox.maths.Vector3;

import java.nio.FloatBuffer;

public class CloudKernel {
    public static FloatBuffer generate(int x, int y, int z, float[]points_red, float[]points_green, float[]points_blue, int max){
        MainCL.Init();
        CLObject cloudKernel=new CLObject("clouds.cls","clouds");

        cloudKernel.setResult(x*y*z*3,4,0);
        cloudKernel.addEntry(points_red,1);
        cloudKernel.addEntry(points_green,2);
        cloudKernel.addEntry(points_blue,3);
        cloudKernel.getMemoryInt().put(4,points_red.length/3);
        cloudKernel.getMemoryInt().put(5,x);
        cloudKernel.getMemoryInt().put(6,y);
        cloudKernel.getMemoryInt().put(7,z);
        cloudKernel.getMemoryInt().put(8,max);
        cloudKernel.getMemoryInt().put(9,4);

        cloudKernel.execute(x*y*z);
        MainCL.stop();

        return cloudKernel.getWriteTo();
    }
}
