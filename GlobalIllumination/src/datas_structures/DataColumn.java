package datas_structures;

public class DataColumn {
    private DataFrame.VAR_TYPE type;
    private float[] values_float;
    private String[] values_string;
    private int[] values_int;
    private String columnName;
    private int length;

    public DataColumn(String cn, DataFrame.VAR_TYPE type, int nb_values) {

        this.columnName=cn;
        this.type = type;
        this.length=nb_values;
        if(DataFrame.VAR_TYPE.FLOAT==type)values_float=new float[nb_values];
        else if(DataFrame.VAR_TYPE.INT==type)values_int=new int[nb_values];
        else if(DataFrame.VAR_TYPE.STRING==type){
            values_string=new String[nb_values];
            for (int i = 0; i < nb_values; i++) {
                values_string[i]="";
            }
        }
    }

    public DataColumn(String cn, float[] values) {
        this.columnName=cn;
        this.type = DataFrame.VAR_TYPE.FLOAT;
        values_float=values;
    }

    public DataColumn(String cn, int[] values) {
        this.columnName=cn;
        this.type = DataFrame.VAR_TYPE.INT;
        values_int=values;
    }

    public DataColumn(String cn, String[] values) {
        this.columnName=cn;
        this.type = DataFrame.VAR_TYPE.STRING;
        values_string=values;
    }

    public float getFloat(int index){
        if(type!= DataFrame.VAR_TYPE.FLOAT){
            System.err.println("The column"+columnName+" is not a float column");
            return Float.NaN;
        }
        return values_float[index];
    }

    public int getInt(int index){
        if(type!= DataFrame.VAR_TYPE.INT){
            System.err.println("The column"+columnName+" is not an integer column");
            return Integer.MAX_VALUE;
        }
        return values_int[index];
    }
    public String getString(int index){
        if(type!= DataFrame.VAR_TYPE.STRING){
            System.err.println("The column"+columnName+" is not a string column");
            return "NaN";
        }
        return values_string[index];
    }

    public void setFloat(int index,float value){
        if(type!= DataFrame.VAR_TYPE.FLOAT){
            System.err.println("The column"+columnName+" is not a float column");
        }
        values_float[index]=value;
    }

    public void setInt(int index,int value){
        if(type!= DataFrame.VAR_TYPE.INT){
            System.err.println("The column"+columnName+" is not an integer column");
        }
        values_int[index]=value;
    }

    public void setString(int index,String value){
        if(type!= DataFrame.VAR_TYPE.STRING){
            System.err.println("The column"+columnName+" is not a string column");
        }
        values_string[index]=value;
    }

    public String valueToString(int index){
        if(type== DataFrame.VAR_TYPE.STRING)return values_string[index];
        if(type== DataFrame.VAR_TYPE.INT)return ""+values_int[index];
        return ""+values_float[index];

    }

    public float[] getValuesFloat(){
        if(type== DataFrame.VAR_TYPE.FLOAT) return values_float;
        else if(type== DataFrame.VAR_TYPE.STRING) return null;
        else {
            float[] v =new float[length];
            for (int i = 0; i < length; i++) {
                v[i] = values_int[i];
                if(Integer.MAX_VALUE == values_int[i]) v[i] =Float.NaN;
            }
            return v;
        }
    }

    public float[] getValues_float() {
        return values_float;
    }

    public int[] getValues_int() {
        return values_int;
    }

    public String[] getValues_string() {
        return values_string;
    }

    public int getLength() {
        return length;
    }

    public String getColumnName() {
        return columnName;
    }

    public DataFrame.VAR_TYPE getType() {
        return type;
    }
}
