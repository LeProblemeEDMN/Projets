package loading.LOD;

import toolbox.maths.Vector3;

public class Vertex {
    public boolean isSet=false;
    public Vector3 pos;
    public Vector3 normal;
    public Vector3 uv;

    public Vertex(Vector3 pos) {
        this.pos = pos;
    }

    public void set(Vector3 normal, Vector3 uv) {
        this.normal = normal;
        this.uv = uv;
        this.isSet=true;
    }
}
