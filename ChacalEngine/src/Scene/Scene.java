package Scene;

import Entity.Ball;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scene {
    Map<Float, List<Ball>>mapBall=new HashMap<>();

    public Scene() {

    }

    public Map<Float, List<Ball>> getMapBall() {
        return mapBall;
    }
}
