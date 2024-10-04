package entity;

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
    public Vector3() {
        this(0, 0, 0);
    }

    public Vector3 add(Vector3 vec) {
        this.x+=vec.getX();
        this.y+=vec.getY();
        this.z+=vec.getZ();

        return this;
    }

    public float norm1(){
        return Math.abs(x)+ Math.abs(y)+ Math.abs(y);
    }

    public float dotProduct(Vector3 vec) {
        return (this.x*vec.x+this.y*vec.y+this.z*vec.z);
    }
    public Vector3 crossProduct(Vector3 right){
        return new Vector3(this.y * right.z - this.z * right.y, right.x * this.z - right.z * this.x, this.x * right.y - this.y * right.x);
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

    public Vector3 getMul(float nb) {
        return new Vector3(x*nb, y*nb, z*nb);
    }
    public Vector3 getMul(Vector3 nb) {
        return new Vector3(x*nb.x, y*nb.y, z*nb.z);
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
}
