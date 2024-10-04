package Init.Clouds;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.List;

public class CLObject {
	private CLMem result;
	private int id=0,resultSize;	
	private HashMap<CLMem, Integer>memory=new HashMap<>();
	private HashMap<Integer, Integer>memoryInt=new HashMap<>();
	private FloatBuffer writeTo;
	
	private CLKernel kernel;
	private CLProgram program;
	
	
	public CLObject(String name,String function) {
		program = CL10.clCreateProgramWithSource(MainCL.context, loadText(name), null);
		int error = CL10.clBuildProgram(program, MainCL.devices.get(0), "", null);
		System.out.println(program.getBuildInfoString(MainCL.devices.get(0), CL10.CL_PROGRAM_BUILD_LOG));
		Util.checkCLError(error);
		kernel = CL10.clCreateKernel(program, function, null);
	}
	
	public void clear() {
		for (CLMem mem : memory.keySet()) {
			CL10.clReleaseMemObject(mem);
		}
		CL10.clReleaseMemObject(result);
		memory.clear();
		memoryInt.clear();
	}
	
	public void setResult(int length,int precision,int id) {
		result = CL10.clCreateBuffer(MainCL.context, CL10.CL_MEM_READ_ONLY, length*precision, MainCL.errorBuf);
		Util.checkCLError(MainCL.errorBuf.get(0));
		this.resultSize=length;
		this.id=id;
	}
	
	public void addEntry(FloatBuffer buffer,int id) {
		CLMem aMemory = CL10.clCreateBuffer(MainCL.context, CL10.CL_MEM_WRITE_ONLY | CL10.CL_MEM_COPY_HOST_PTR, buffer, MainCL.errorBuf);
		Util.checkCLError(MainCL.errorBuf.get(0));
		memory.put(aMemory, id);
	}
	public void addEntry(float[] fb,int id) {
		FloatBuffer buffer= BufferUtils.createFloatBuffer(fb.length);
		buffer.put(fb);
		buffer.rewind();
		CLMem aMemory = CL10.clCreateBuffer(MainCL.context, CL10.CL_MEM_WRITE_ONLY | CL10.CL_MEM_COPY_HOST_PTR, buffer, MainCL.errorBuf);
		Util.checkCLError(MainCL.errorBuf.get(0));
		memory.put(aMemory, id);

	}

	public void addEntry(List<Float> fb, int id) {
		FloatBuffer buffer= BufferUtils.createFloatBuffer(fb.size());
		float[] f=new float[fb.size()];
		for (int i = 0; i < f.length; i++) {
			f[i]=fb.get(i);
		}
		buffer.put(f);
		buffer.rewind();
		CLMem aMemory = CL10.clCreateBuffer(MainCL.context, CL10.CL_MEM_WRITE_ONLY | CL10.CL_MEM_COPY_HOST_PTR, buffer, MainCL.errorBuf);
		Util.checkCLError(MainCL.errorBuf.get(0));
		memory.put(aMemory, id);
	}
	
	public  void execute(int taille) {
		kernel.setArg(id, result);
		System.out.println(id+" "+result);
		for (CLMem mem : memory.keySet()) {
			//System.out.println(mem+" "+memoryInt.get(mem));
			kernel.setArg(memory.get(mem), mem);
		}
		for (Integer mem : memoryInt.keySet()) {
		//	System.out.println(mem+" "+memoryInt.get(mem));
			kernel.setArg(mem, memoryInt.get(mem));
		}
	
		final int dimensions = 1; 
		PointerBuffer globalWorkSize = BufferUtils.createPointerBuffer(dimensions);
		globalWorkSize.put(0, taille);
		// Run the specified number of work units using our OpenCL program kernel
		long time=System.currentTimeMillis();
		CL10.clEnqueueNDRangeKernel(MainCL.queue, kernel, dimensions, null, globalWorkSize, null, null, null);
		CL10.clFinish(MainCL.queue);
		time=System.currentTimeMillis()-time;
		System.out.println("TIME:"+time);
		writeTo = BufferUtils.createFloatBuffer(resultSize); //Remember size is the number of elements.
		CL10.clEnqueueReadBuffer(MainCL.queue, result, CL10.CL_TRUE, 0, writeTo, null, null);
		
	}
	
	public  void close() {
		CL10.clReleaseKernel(kernel);
		CL10.clReleaseProgram(program);
		// Destroy our memory objects
		clear();
	}
	public static String loadText(String name) {
		if(!name.endsWith(".cls")) {
			name += ".cls";
		}
		BufferedReader br = null;
		String resultString = null;
		try {
			// Get the file containing the OpenCL kernel source code
			File clSourceFile =new File("res//"+name);
			//File clSourceFile = new File(Main.class.getClassLoader().getResource(name).toURI());
			// Create a buffered file reader to read the source file
			br = new BufferedReader(new FileReader(clSourceFile));
			// Read the file's source code line by line and store it in a string buffer
			String line = null;
			StringBuilder result = new StringBuilder();
			while((line = br.readLine()) != null) {
				result.append(line);
				result.append("\n");
			}
			// Convert the string builder into a string containing the source code to return
			resultString = result.toString();
		} catch(NullPointerException npe) {
			// If there is an error finding the file
			System.err.println("Error retrieving OpenCL source file: ");
			npe.printStackTrace();
		}
		catch(IOException ioe) {
			
			System.err.println("Error reading OpenCL source file: ");
			ioe.printStackTrace();
		} finally {
			// Finally clean up any open resources
			try {
				br.close();
			} catch (IOException ex) {
				// If there is an error closing the file after we are done with it
				System.err.println("Error closing OpenCL source file");
				ex.printStackTrace();
			}
		}

		// Return the string read from the OpenCL kernel source code file
		return resultString;
	}

	public CLMem getResult() {
		return result;
	}

	public int getId() {
		return id;
	}

	public int getResultSize() {
		return resultSize;
	}

	public HashMap<CLMem, Integer> getMemory() {
		return memory;
	}

	public FloatBuffer getWriteTo() {
		return writeTo;
	}

	public CLKernel getKernel() {
		return kernel;
	}

	public CLProgram getProgram() {
		return program;
	}
	public HashMap<Integer, Integer> getMemoryInt() {
		return memoryInt;
	}
}	
