package toolbox.maths;

import java.util.Iterator;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Lists;


public class Vector3 {
	public float x=0,y=0,z=0;
		public Vector3(float x,float y,float z) {
			this.x=x;
			this.y=y;
			this.z=z;
		}	
		public Vector3(double x,double y,double z) {
			this.x=(float)x;
			this.y=(float)y;
			this.z=(float)z;
		}	
		public Vector3(Vector3 vec) {
			this.x=vec.x;
			this.y=vec.y;
			this.z=vec.z;
		}	
		public Vector3(Vector3f vec) {
			this.x=vec.x;
			this.y=vec.y;
			this.z=vec.z;
		}
		public Vector3() {
			this(0, 0, 0);
		}
		

		public Vector3 add(Vector3 vec) {
			this.x+=vec.getX();
			this.y+=vec.getY();
			this.z+=vec.getZ();
			
			return this;
			
		}

	public float dotProduct(Vector3 vec) {
		return (this.x*vec.x+this.y*vec.y+this.z*vec.z);
	}
	public float squarelength() {
		return this.x*this.x+this.y*this.y+this.z*this.z;
	}

		public Vector3 getAdd(float x,float y,float z) {
			return new Vector3(this.x+x, this.y+y, this.z+z);
		}
		public void add(float x,float y,float z) {
			this.x+=x;
			this.y+=y;
			this.z+=z;
		}
		public Vector3 mul(Vector3 vec) {
			this.x*=vec.getX();
			this.y*=vec.getY();
			this.z*=vec.getZ();
			
			return this;
			
		}
		public Vector3 mul(float vec) {
			this.x*=vec;
			this.y*=vec;
			this.z*=vec;
			
			return this;
			
		}
		public Vector3 div(Vector3 vec) {
			this.x/=vec.getX();
			this.y/=vec.getY();
			this.z/=vec.getZ();
			
			return this;
			
		}
		public Vector3 sub(Vector3 vec) {
			this.x-=vec.getX();
			this.y-=vec.getY();
			this.z-=vec.getZ();
			
			return this;
			
		}
		
		public Vector3 addX(float v) {
			this.x+=v;
			return this;
		}
		public Vector3 subX(float v) {
			this.x-=v;
			return this;
		}
		
		public Vector3 addY(float v) {
			this.y+=v;
			return this;
		}
		public Vector3 subY(float v) {
			this.y-=v;
			return this;
		}
		
		public Vector3 addZ(float v) {
			this.z+=v;
			return this;
		}
		public Vector3 subZ(float v) {
			this.z-=v;
			return this;
		}
		
		public void setX(float x) {
			this.x = x;
		}
		public void setY(float y) {
			this.y = y;
		}
		public void setZ(float z) {
			this.z = z;
		}
		public float getY() {
			return y;
		}
		public float getX() {
			return x;
		}
		public float getZ() {
			return z;
		}
		
		public float length() {
			return (float)Math.sqrt(this.x*this.x+this.y*this.y+this.z*this.z);
		}
	
		public float squareDistanceTo(Vector3 vec) {
			float x=this.x-vec.x;
			float y=this.y-vec.y;
			float z=this.z-vec.z;
			return (x*x+y*y+z*z);
		}
		public Vector3 normalize() {
			float length=this.length();
			this.x/=length;
			this.y/=length;
			this.z/=length;
			
			return this;
			
		}
		public Vector3 getNormalize() {
			float length=this.length();
			float x=this.x/length;
			float y=this.y/length;
			float z=this.z/length;
			
			return new Vector3(x, y, z);
			
		}
		
		public Vector3f getOglVec() {
			return new Vector3f(x, y, z); 
		}
		public long toChunkId() {
			long pre=0;
			if(this.x<0) {
				pre=1;
			}
			if(this.y<0) {
				pre+=10;
			}
			if(this.z<0) {
				pre+=100;
			}
			long nb=(long)Math.abs(this.x)+(long)Math.abs(this.y)*1000+(long)Math.abs(this.z)*1000000+pre*1000000000;
			return nb;
		}
		public static Vector3 getVectorFromId(long id) {
			long pre=Math.floorDiv(id, 1000000000);
			long z=Math.floorDiv((id-pre*1000000000), 1000000);
			long y=Math.floorDiv((id-pre*1000000000-z*1000000), 1000);
			long x=(id-pre*1000000000-z*1000000-y*1000);
			if(pre>=100) {
				z=-z;
				pre-=100;
			}
			if(pre>=10) {
				y=-y;
				pre-=10;
			}
			if(pre>=1) {
				x=-x;
			}
			return new Vector3(x, y, z);
		}
		public Vector3 getMul(float nb) {
			return new Vector3(x*nb, y*nb, z*nb);
		}
		public Vector3 getAdd(Vector3 vec) {
			return new Vector3(x+vec.x, y+vec.y, z+vec.z);
		}
		public Vector3 getSub(Vector3 vec) {
			return new Vector3(x-vec.x, y-vec.y, z-vec.z);
		}
		@Override
		public String toString() {
			return "Vector3 x="+x+" y="+y+" z="+z;
		}
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof Vector3) {
				Vector3 vector3=(Vector3)obj;
				if(vector3.getX()==this.getX() && vector3.getY()==this.getY() && vector3.getZ()==this.getZ() ) {
					return true;
				}
			}
			return false;
		}
		
		public Vector3 getIntermediateWithZValue(Vector3 vec, double z)
	    {
	        double d0 = vec.x - this.x;
	        double d1 = vec.y - this.y;
	        double d2 = vec.z - this.z;

	        if (d2 * d2 < 1.0000000116860974E-7D)
	        {
	            return null;
	        }
	        else
	        {
	            double d3 = (z - this.z) / d2;
	            return d3 >= 0.0D && d3 <= 1.0D ? new Vector3(this.x + d0 * d3, this.y + d1 * d3, this.z + d2 * d3) : null;
	        }
	    }
		 public Vector3 getIntermediateWithXValue(Vector3 vec, double x)
		    {
		        double d0 = vec.x - this.x;
		        double d1 = vec.y - this.y;
		        double d2 = vec.z - this.z;

		        if (d0 * d0 < 1.0000000116860974E-7D)
		        {
		            return null;
		        }
		        else
		        {
		            double d3 = (x - this.x) / d0;
		            return d3 >= 0.0D && d3 <= 1.0D ? new Vector3(this.x + d0 * d3, this.y + d1 * d3, this.z + d2 * d3) : null;
		        }
		    }
		    public Vector3 getIntermediateWithYValue(Vector3 vec, double y)
		    {
		    	 double d0 = vec.x - this.x;
			        double d1 = vec.y - this.y;
			        double d2 = vec.z - this.z;

		        if (d1 * d1 < 1.0000000116860974E-7D)
		        {
		            return null;
		        }
		        else
		        {
		            double d3 = (y - this.y) / d1;
		            return d3 >= 0.0D && d3 <= 1.0D ? new Vector3(this.x + d0 * d3, this.y + d1 * d3, this.z + d2 * d3) : null;
		        }
		    }
}