package loading.Texture;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;
import main.MainLoop;
import org.lwjgl.opengl.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;

public class TextureUtils {

	//this hash map contains the texture for PBR the int[] array contains the ids of the texture (albedo, ARM,normal,displacement);
	private static HashMap<String,int[]> textures=new HashMap<>();

	//load all of the texture from the res/textures/ directory
	public static void loadTextureDir(){
		File f=new File("res/textures/");
		String[] dir_names=f.list();
		File[] files=f.listFiles();
		for(int i=0;i<files.length;i++){
			int[] PBR_text=new int[4];
			String[] file_names=files[i].list();
			File[] file_imgs=files[i].listFiles();
			int count=0;
			for(int j=0;j<file_names.length;j++){
				if(file_names[j].contains("albedo")){
					count++;
					PBR_text[0]= MainLoop.LOADER.loadTexture(file_imgs[j].toString());
				}else if(file_names[j].contains("ARM")){
					count++;
					PBR_text[1]= MainLoop.LOADER.loadTexture(file_imgs[j].toString());
				}else if(file_names[j].contains("normal")){
					count++;
					PBR_text[2]= MainLoop.LOADER.loadTexture(file_imgs[j].toString());
				}else if(file_names[j].contains("displacement")){
					count++;
					PBR_text[3]= MainLoop.LOADER.loadTexture(file_imgs[j].toString());
				}
			}
			if(count<4){
				System.err.println("Unable to load all the PBR texture from "+dir_names[i]);
			}
			textures.put(dir_names[i],PBR_text);
		}
	}
	//method to get all of the PBR texture from the name
	public static int getAlbedo(String name){return textures.get(name)[0];}
	public static int getARM(String name){return textures.get(name)[1];}
	public static int getNormal(String name){return textures.get(name)[2];}
	public static int getDisplacement(String name){return textures.get(name)[3];}

	/**
	 * Fusionne les canaux des trois images pour créer une nouvelle image de taille (x,y).
	 * Les pixels en dehors des dimensions de l'image d'origine sont remplis avec des valeurs par défaut.
	 *
	 * @param pathImage1 Image contenant le canal rouge
	 * @param pathImage2 Image contenant le canal vert
	 * @param pathImage3 Image contenant le canal bleu
	 * @param x Largeur de l'image de sortie
	 * @param y Hauteur de l'image de sortie
	 * @param outputfile Image ou sauvegarder
	 */
	public static void mergeTexture(String pathImage1,String pathImage2,String pathImage3,int x,int y,String outputfile){
		BufferedImage img1 = null,img2 = null,img3 = null;
		try {
			img1 = ImageIO.read(new File(pathImage1));
			img2 = ImageIO.read(new File(pathImage2));
			img3 = ImageIO.read(new File(pathImage3));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		BufferedImage outputImg = new BufferedImage(x, y, BufferedImage.TYPE_INT_ARGB);


		// Déterminer les dimensions minimales des images d'entrée

		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				// Si les images d'entrée sont plus grandes que la taille cible, utiliser leurs pixels
				int pixel1 = img1.getRGB((int)((float)i/x*img1.getWidth()), (int)((float)j/y*img1.getHeight()));
				int pixel2 = img2.getRGB((int)((float)i/x*img2.getWidth()), (int)((float)j/y*img2.getHeight()));
				int pixel3 = img3.getRGB((int)((float)i/x*img3.getWidth()), (int)((float)j/y*img3.getHeight()));

				// Extraire les composantes RGB
				int red = (pixel1 >> 16) & 0xFF;
				int green = (pixel2 >> 8) & 0xFF;
				int blue = pixel3 & 0xFF;

				// Créer le nouveau pixel
				int newPixel = new Color(red,green,blue).getRGB();

				outputImg.setRGB(i, j, newPixel);
			}
		}

		// Enregistrer l'image fusionnée
		File output = new File(outputfile);
		try {
			System.out.println(outputImg);
			ImageIO.write(outputImg, "PNG", output);
			System.out.println("Image fusionnée enregistrée comme "+outputfile);
		} catch (IOException e) {
			System.out.println("Erreur lors de l'enregistrement de l'image : " + e.getMessage());
		}
	}


	public static int createEmptyCubeMap(int size) {
		int texID = GL11.glGenTextures();
		GL13.glActiveTexture(GL13.GL_TEXTURE31);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texID);
		for (int i = 0; i < 6; i++) {
			GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA8, size, size, 0, GL11.GL_RGBA,
					GL11.GL_UNSIGNED_BYTE, (ByteBuffer)null);
		}
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL12.GL_TEXTURE_WRAP_R, GL12.GL_CLAMP_TO_EDGE);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, 0);
		return texID;
	}
	
	public static int loadCubeMap(MyFile[] textureFiles) {
		int texID = GL11.glGenTextures();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texID);
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
		
		for (int i = 0; i < textureFiles.length; i++) {
			TextureData data = decodeTextureFile(textureFiles[i]);
			GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, data.getWidth(),
					data.getHeight(), 0, GL12.GL_BGRA, GL11.GL_UNSIGNED_BYTE, data.getBuffer());
		}
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, 0);
		return texID;
	}

	protected static TextureData decodeTextureFile(MyFile file) {
		int width = 0;
		int height = 0;
		ByteBuffer buffer = null;
		try {
			InputStream in = file.getInputStream();
			PNGDecoder decoder = new PNGDecoder(in);
			width = decoder.getWidth();
			height = decoder.getHeight();
			buffer = ByteBuffer.allocateDirect(4 * width * height);
			decoder.decode(buffer, width * 4, Format.BGRA);
			buffer.flip();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Tried to load texture " + file.getName() + " , didn't work");
			System.exit(-1);
		}
		return new TextureData(buffer, width, height);
	}

	protected static int loadTextureToOpenGL(TextureData data, TextureBuilder builder) {
		int texID = GL11.glGenTextures();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texID);
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, data.getWidth(), data.getHeight(), 0, GL12.GL_BGRA,
				GL11.GL_UNSIGNED_BYTE, data.getBuffer());
		if (builder.isMipmap()) {
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
			if (builder.isAnisotropic() && GL.getCapabilities().GL_EXT_texture_filter_anisotropic) {
				GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0);
				GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT,
						4.0f);
			}
		} else if (builder.isNearest()) {
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		} else {
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		}
		if (builder.isClampEdges()) {
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		} else {
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		}
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		return texID;
	}

}
