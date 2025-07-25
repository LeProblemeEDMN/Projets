package rendering.capture_object;

import main.DisplayManager;
import org.lwjgl.opengl.*;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.nio.IntBuffer;

public class MultisampleFBO {
    private final int fboId;
    private final int[] textureIds = new int[4];
    private final int width, height;
    private final int samples;

    public MultisampleFBO(int width, int height) {
        this.width = width;
        this.height = height;
        this.samples = 4;

        // Créer le FBO multisample
        fboId = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fboId);

        // Créer les textures multisample
        for (int i = 0; i < samples-1; i++) {
            //crre la texture
            textureIds[i] = GL11.glGenTextures();
            GL32.glBindTexture(GL32.GL_TEXTURE_2D_MULTISAMPLE, textureIds[i]);
            //inique qu'elle fait partie d'un FBO multisample
            GL32.glTexImage2DMultisample(
                    GL32.GL_TEXTURE_2D_MULTISAMPLE,
                    samples,
                    GL11.GL_RGB8,
                    width,
                    height,
                    true
            );
            //indique ou elle sera binde
            GL32.glFramebufferTexture2D(
                    GL30.GL_FRAMEBUFFER,
                    GL30.GL_COLOR_ATTACHMENT0 + i,
                    GL32.GL_TEXTURE_2D_MULTISAMPLE,
                    textureIds[i],
                    0
            );
        }

        // 1. Générer l’ID de la texture
        int depthTextureId = GL11.glGenTextures();
        textureIds[3]=depthTextureId;
        // 2. Créer une texture 2D multisample pour la profondeur
        GL32.glBindTexture(GL32.GL_TEXTURE_2D_MULTISAMPLE, depthTextureId);
        GL32.glTexImage2DMultisample(
                GL32.GL_TEXTURE_2D_MULTISAMPLE,
                samples,                      // Ex : 4 ou 8
                GL30.GL_DEPTH_COMPONENT24,    // Format interne
                width, height,
                true                          // fixedSampleLocations
        );

        // 3. Attacher la texture au FBO en tant que buffer de profondeur
        GL30.glFramebufferTexture2D(
                GL30.GL_FRAMEBUFFER,
                GL30.GL_DEPTH_ATTACHMENT,
                GL32.GL_TEXTURE_2D_MULTISAMPLE,
                depthTextureId,
                0
        );

        // Définir les color attachments
        IntBuffer drawBuffers = BufferUtils.createIntBuffer(4);
        drawBuffers.put(GL30.GL_COLOR_ATTACHMENT0);
        drawBuffers.put(GL30.GL_COLOR_ATTACHMENT1);
        drawBuffers.put(GL30.GL_COLOR_ATTACHMENT2);
        //drawBuffers.put(GL30.GL_DEPTH_ATTACHMENT);
        drawBuffers.flip();
        GL20.glDrawBuffers(drawBuffers);

        // Vérification du FBO
        if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException("Multisample FBO not complete!");
        }

        // Unbind
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }

    public void bind() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fboId);
        GL11.glViewport(0, 0, width, height);
    }

    public void unbind() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }

    public void resolveToFBO(int attachmentIndex, Fbo targetFBO) {
        if (attachmentIndex < 0 || attachmentIndex >= 4) {
            throw new IllegalArgumentException("Invalid attachment index: " + attachmentIndex);
        }

        // Lire depuis notre multisample FBO
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, fboId);
        GL11.glReadBuffer(GL30.GL_COLOR_ATTACHMENT0 + attachmentIndex);

        // Dessiner vers un FBO simple contenant une texture 2D
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, targetFBO.frameBuffer);

        // Blit
        GL30.glBlitFramebuffer(
                0, 0, width, height,
                0, 0, width, height,
                GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST
        );

        // Unbind les framebuffers
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }

    public void resolveDepthToFBO( Fbo targetFBO) {


        // Lire depuis notre multisample FBO
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, fboId);
        GL11.glReadBuffer(GL30.GL_COLOR_ATTACHMENT0 + (samples-1));

        // Dessiner vers un FBO simple contenant une texture 2D
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, targetFBO.frameBuffer);

        // Blit
        GL30.glBlitFramebuffer(
                0, 0, width, height,
                0, 0, width, height,
                GL11.GL_DEPTH_BUFFER_BIT, GL11.GL_NEAREST
        );

        // Unbind les framebuffers
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }

    public int getTextureId(int index) {
        if (index < 0 || index >= 4) throw new IllegalArgumentException("Invalid texture index");
        return textureIds[index];
    }

    public void cleanup() {
        for (int tex : textureIds) {
            GL11.glDeleteTextures(tex);
        }
        GL30.glDeleteFramebuffers(fboId);
    }
}

