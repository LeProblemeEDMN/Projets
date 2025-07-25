package rendering.postprocessing;

import rendering.capture_object.Fbo;

public interface PostProcessingEffect {
    public void render(int texture_input);
    public int getOutputTexture();
    public String getName();
    public Fbo getOutFbo();
    public void cleanUp();
    public void resize(int width, int height);

}
