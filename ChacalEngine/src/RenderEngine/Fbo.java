
package RenderEngine;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class Fbo {

	public static final int NONE = 0;
	public static final int DEPTH_TEXTURE = 1;
	public static final int DEPTH_RENDER_BUFFER = 2;

	private  int width;
	private  int height;

	private boolean multisample=false;	
	
	private int frameBuffer;

	private int colourTexture;
	private int depthTexture;

	private int depthBuffer;
	private int colourBuffer;
	private int colourBuffer1;
	private int colourBuffer2;
	private int colourBuffer3;
	/**
	 * Creates an FBO of a specified width and height, with the desired type of
	 * depth buffer attachment.
	 * 
	 * @param width
	 *            - the width of the FBO.
	 * @param height
	 *            - the height of the FBO.
	 * @param depthBufferType
	 *            - an int indicating the type of depth buffer attachment that
	 *            this FBO should use.
	 */
	public Fbo(int width, int height, int depthBufferType) {
		this.width = width;
		this.height = height;
		initialiseFrameBuffer(depthBufferType);
	}
	public Fbo(int width, int height) {
		this.width = width;
		this.height = height;
		this.multisample=true;
		initialiseFrameBuffer(DEPTH_RENDER_BUFFER);
	}

	/**
	 * Deletes the frame buffer and its attachments when the game closes.
	 */
	public void cleanUp() {
		GL30.glDeleteFramebuffers(frameBuffer);
		GL11.glDeleteTextures(colourTexture);
		GL11.glDeleteTextures(depthTexture);
		GL30.glDeleteRenderbuffers(depthBuffer);
		GL30.glDeleteRenderbuffers(colourBuffer);
		GL30.glDeleteRenderbuffers(colourBuffer2);
		GL30.glDeleteRenderbuffers(colourBuffer3);
	}

	/**
	 * Binds the frame buffer, setting it as the current render target. Anything
	 * rendered after this will be rendered to this FBO, and not to the screen.
	 */
	public void bindFrameBuffer() {
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, frameBuffer);
		GL11.glViewport(0, 0, width, height);

	}

	/**
	 * Unbinds the frame buffer, setting the default frame buffer as the current
	 * render target. Anything rendered after this will be rendered to the
	 * screen, and not this FBO.
	 */
	public void unbindFrameBuffer() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
	}

	/**
	 * Binds the current FBO to be read from (not used in tutorial 43).
	 */
	public void bindToRead() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, frameBuffer);
		GL11.glReadBuffer(GL30.GL_COLOR_ATTACHMENT0);
	}

	/**
	 * @return The ID of the texture containing the colour buffer of the FBO.
	 */
	public int getColourTexture() {
		return colourTexture;
	}

	/**
	 * @return The texture containing the FBOs depth buffer.
	 */
	public int getDepthTexture() {
		return depthTexture;
	}

	public void resolveToFbo(int readBuffer,Fbo outputFbo) {
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, outputFbo.frameBuffer);
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, this.frameBuffer);
		GL11.glReadBuffer(readBuffer);
		GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, outputFbo.width, outputFbo.height, GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT, GL11.GL_NEAREST);
	this.unbindFrameBuffer();
	}
	public void resolveToScreen() {
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, this.frameBuffer);
		GL11.glDrawBuffer(GL11.GL_BACK);
		GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, Display.getWidth(), Display.getHeight(), GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);
	this.unbindFrameBuffer();
	}
	/**
	 
	 * Creates the FBO along with a colour buffer texture attachment, and
	 * possibly a depth buffer.
	 * 
	 * @param type
	 *            - the type of depth buffer attachment to be attached to the
	 *            FBO.
	 */
	private void initialiseFrameBuffer(int type) {
		createFrameBuffer();
		if(multisample) {
			colourBuffer =createMultiSampleColourAttachment(GL30.GL_COLOR_ATTACHMENT0);
			colourBuffer1 =createMultiSampleColourAttachment(GL30.GL_COLOR_ATTACHMENT1);
			colourBuffer2 =createMultiSampleColourAttachment(GL30.GL_COLOR_ATTACHMENT2);
			colourBuffer3 =createMultiSampleColourAttachment(GL30.GL_COLOR_ATTACHMENT3);
		}else {
		createTextureAttachment();
		}
		if (type == DEPTH_RENDER_BUFFER) {
			createDepthBufferAttachment();
		} else if (type == DEPTH_TEXTURE) {
			createDepthTextureAttachment();
		}
		unbindFrameBuffer();
	}

	/**
	 * Creates a new frame buffer object and sets the buffer to which drawing
	 * will occur - colour attachment 0. This is the attachment where the colour
	 * buffer texture is.
	 * 
	 */
	private void createFrameBuffer() {
		frameBuffer = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
		determineDrawBuffer();
	}
	
	private void determineDrawBuffer() {
		IntBuffer drawBuffer=BufferUtils.createIntBuffer(4);
		drawBuffer.put(GL30.GL_COLOR_ATTACHMENT0);
		if(this.multisample) {
			drawBuffer.put(GL30.GL_COLOR_ATTACHMENT1);	
			drawBuffer.put(GL30.GL_COLOR_ATTACHMENT2);
			drawBuffer.put(GL30.GL_COLOR_ATTACHMENT3);
		}
		drawBuffer.flip();
	GL20.glDrawBuffers(drawBuffer);
	}
	/**
	 * Creates a texture and sets it as the colour buffer attachment for this
	 * FBO.
	 */
	private void createTextureAttachment() {
		colourTexture = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, colourTexture);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE,
				(ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, colourTexture,
				0);
	}

	/**
	 * Adds a depth buffer to the FBO in the form of a texture, which can later
	 * be sampled.
	 */
	private void createDepthTextureAttachment() {
		depthTexture = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTexture);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT32, width, height, 0, GL11.GL_DEPTH_COMPONENT,
				GL11.GL_FLOAT, (ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, depthTexture, 0);
	}
	private int createMultiSampleColourAttachment(int attachement) {
		int idcolourBuffer=GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, idcolourBuffer);
		GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, 4 , GL11.GL_RGBA16, width, height);
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, attachement, GL30.GL_RENDERBUFFER,
				idcolourBuffer);
		return idcolourBuffer;
	}
	/**
	 * Adds a depth buffer to the FBO in the form of a render buffer. This can't
	 * be used for sampling in the shaders.
	 */
	private void createDepthBufferAttachment() {
		depthBuffer = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthBuffer);
		if(!multisample) {
			GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL14.GL_DEPTH_COMPONENT32, width, height);
		}else {
			GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, 4 , GL14.GL_DEPTH_COMPONENT32, width, height);
		}
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER,
				depthBuffer);
	}
public void setWidth(int width) {
	this.width = width;
}
public void setHeight(int height) {
	this.height = height;
}

}
