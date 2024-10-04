import entity.*;

import loading.Loader;
import org.lwjgl.util.vector.Vector2f;
import rendering_raytracing.Renderer;
import rendering_raytracing.RendererOpti;
import rendering_raytracing.Scene;
import utils.Constants;
import utils.MousePicker;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainRT {
    public static void main(String[] args) throws IOException, InterruptedException {
        //RC=5
        MousePicker picker = new MousePicker(new Vector3(0,5,2),new Vector3(2.8,5.5,5));

       // Renderer renderer = new Renderer(Constants.WIDTH,Constants.HEIGHT);
        RendererOpti rendereropti = new RendererOpti(Constants.WIDTH,Constants.HEIGHT);

        Scene sc=RenderingTest.scene();

        Droite d=new Droite();
        d.O=new Vector3(5,5,-10);
        d.D=new Vector3(0.4054,-0.4064304,0.8188248).normalize();
       ;
        rendereropti.simpleRender(sc,picker,"res/optiNormal2");
       // renderer.simpleRender(sc,picker,"res/optiLog");
        //renderer.renderPixel(sc,picker);
        SamplingEvaluation.evaluate("res/optiNormal","res/optiNormal2","res/reference.png","res");

    }
}
