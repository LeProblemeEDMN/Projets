package toolbox.maths;

public class MathHelper {
	public static int floor_double(double value)
    {
        int i = (int)value;
        return value < (double)i ? i - 1 : i;
    }
	public static int floor_float(float value)
    {
        int i = (int)value;
        return value < (float)i ? i - 1 : i;
    }

    /**
     * Long version of floor_double
     */
    public static long floor_double_long(double value)
    {
        long i = (long)value;
        return value < (double)i ? i - 1L : i;
    }
    public static float sqrt_float(float value)
    {
        return (float)Math.sqrt((double)value);
    }

    public static float sqrt_double(double value)
    {
        return (float)Math.sqrt(value);
    }
    public static double denormalizeClamp(double lowerBnd, double upperBnd, double slide)
    {
        return slide < 0.0D ? lowerBnd : (slide > 1.0D ? upperBnd : lowerBnd + (upperBnd - lowerBnd) * slide);
    }
    public static float abs(float value)
    {
        return value >= 0.0F ? value : -value;
    }
    public static int ceiling_float_int(float value)
    {
        int i = (int)value;
        return value > (float)i ? i + 1 : i;
    }
    public static int ceiling_double_int(double value)
    {
        int i = (int)value;
        return value > (double)i ? i + 1 : i;
    }

    public static double mod(double a,double b){
        return a-floor_double(a/b)*b;
    }

    public static Vector3 getRacines(float a,float b,float c) {
    	
    	float delta=(b*b)-(4*a*c);
    	if(delta<0 ||a==0) {
    		return new Vector3();
    	}else if(delta==0) {
    		float x1=-b/(2*a);
    		return new Vector3(1, x1, x1);
    	}else {
    		float root=(float)Math.sqrt(delta);
    		float x1=(-b-root)/(2*a);
    		float x2=(-b+root)/(2*a);
    		return new Vector3(2, Math.min(x1, x2), Math.max(x1, x2));
    	}
    }
}
