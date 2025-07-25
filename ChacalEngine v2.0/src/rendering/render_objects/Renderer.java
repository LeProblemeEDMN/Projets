package rendering.render_objects;

import entity.Entity;

import java.util.List;

public abstract class Renderer {
    /*
    Used by the workflow to render objects.

     */
    public abstract void render(List<Entity> entities,int vertexCount);

    public abstract void resize(int w,int h);

    public abstract void cleanUp();
}
