package datas_structures;

public class DataLine {
    private String[]names;
    private DataFrame.VAR_TYPE[] types;
    private float[] float_values;
    private String[] string_values;
    private int[] int_values;

    public DataLine(String[] names, DataFrame.VAR_TYPE[] types) {
        this.names = names;
        this.types = types;
        string_values=new String[names.length];
        float_values=new float[names.length];
        int_values=new int[names.length];
    }

    public void addFloat(int index,float value){
        if(DataFrame.VAR_TYPE.FLOAT!=types[index]){
            System.out.println("Key "+names[index]+" is not a float");
        }
        float_values[index]=value;
    }

    public void addInt(int index,int value){
        if(DataFrame.VAR_TYPE.INT!=types[index]){
            System.out.println("Key "+names[index]+" is not an integer");
        }
        int_values[index]=value;
    }

    public void addString(int index,String value){
        if(DataFrame.VAR_TYPE.STRING!=types[index]){
            System.out.println("Key "+names[index]+" is not a string");
        }
        string_values[index]=value;
    }

    public float getFloat(int index){
        if(DataFrame.VAR_TYPE.FLOAT!=types[index]){
            System.out.println("Key "+names[index]+"is not a float");
            return Float.NaN;
        }
        return float_values[index];
    }

    public int getInt(int index){
        if(DataFrame.VAR_TYPE.INT!=types[index]){
            System.out.println("Key "+names[index]+"is not an integer");
            return Integer.MAX_VALUE;
        }
        return int_values[index];
    }
    public String getString(int index){
        if(DataFrame.VAR_TYPE.STRING!=types[index]){
            System.out.println("Key "+names[index]+"is not a string");
            return "";
        }
        return string_values[index];
    }

    @Override
    public String toString() {
        int lineToRender=2;
        boolean skip=false;
        String[][]tabString=new String[lineToRender][names.length];

        //set the label
        for (int i = 0; i < names.length; i++) tabString[0][i]=names[i];

        //load the values
        int nb_line_first_section=skip?6:lineToRender;
        for (int i = 1; i < nb_line_first_section; i++) {
            for (int j = 0; j < names.length; j++){
                if(DataFrame.VAR_TYPE.STRING==types[j])tabString[i][j]=string_values[j];
                if(DataFrame.VAR_TYPE.INT==types[j])tabString[i][j]=int_values[j]+"";
                if(DataFrame.VAR_TYPE.FLOAT==types[j])tabString[i][j]=float_values[j]+"";
            }
        }

        //set the value the same length per column
        for (int j = 0; j < names.length; j++){
            int maxLength=0;
            //get the max length of the strings in the columns
            for (int i = 0; i <lineToRender; i++)maxLength= Math.max(maxLength,tabString[i][j].length());
            //set all the strings to the same length by adding space
            for (int i = 0; i <lineToRender; i++) {
                while (tabString[i][j].length()<maxLength){
                    tabString[i][j]=" "+tabString[i][j];
                }
            }
        }
        String finalLine="";
        //concatenate
        for (int i = 0; i <lineToRender; i++) {
            String line="|";
            for (int j = 0; j < names.length; j++){
                line+=tabString[i][j]+"| ";
            }
            line+="\n";
            finalLine+=line;
        }
        return finalLine;
    }
}
