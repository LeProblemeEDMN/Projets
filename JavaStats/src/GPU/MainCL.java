package GPU;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opencl.*;

import java.nio.IntBuffer;
import java.util.List;

public class MainCL {
	public static CLPlatform platform;
	public static CLContext context;
	public static CLCommandQueue queue;
	public static IntBuffer errorBuf;
	public static List<CLDevice> devices;
	
	public static void Init() {
		//INIT---------------
				try {
					CL.create();
				} catch (LWJGLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				platform = CLPlatform.getPlatforms().get(0); 
				devices = platform.getDevices(CL10.CL_DEVICE_TYPE_GPU);
				
				try {
					context = CLContext.create(platform, devices, null);
				} catch (LWJGLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				errorBuf = BufferUtils.createIntBuffer(1);
				queue = CL10.clCreateCommandQueue(context, devices.get(0), CL10.CL_QUEUE_PROFILING_ENABLE, errorBuf);
				Util.checkCLError(errorBuf.get(0)); 
			
			}
			public static void stop() {
				// Destroy the OpenCL context
				CL10.clReleaseCommandQueue(queue);
				CL10.clReleaseContext(context);
				// And release OpenCL, after this method call we cannot use OpenCL unless we re-initialize it
				CL.destroy();
									  }
}
