package datas_structures;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataFrame {
    public static enum VAR_TYPE{STRING,INT,FLOAT};

    public List<String>names_columns=new ArrayList<>();
    public List<Integer>hash_columns=new ArrayList<>();
    public List<VAR_TYPE>types_columns=new ArrayList<>();
    public List<DataColumn>data_columns=new ArrayList<>();

    public int number_line=-1;

    public DataFrame(int number_line){
        this.number_line=number_line;
    }

    public void printDataFrame(String PATH,String delim){
        try {
            BufferedWriter writer=new BufferedWriter(new FileWriter(PATH));
            String label="";
            for (int i = 0; i < getNumber_col(); i++) {
                label+=names_columns.get(i);
                if(i<getNumber_col()-1)label+=delim;
            }
            writer.write(label);
            writer.newLine();
            for (int i = 0; i < number_line; i++) {
                String line="";
                for (int j = 0; j < getNumber_col(); j++) {
                    line+=data_columns.get(j).valueToString(i);
                    if(j<getNumber_col()-1)line+=delim;
                }
                writer.write(line);
                if(i<getNumber_line()-1)writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static DataFrame readCSV(String PATH,String delim){
        try {
            BufferedReader reader=new BufferedReader(new FileReader(PATH));
            String[] label=reader.readLine().split(delim);

            //Lit le csv et stock le slignes
            List<String[]>values=new ArrayList<>();
            String line=reader.readLine();
            while (line!=null){
                String[] tab=line.split(delim);
                String[] tabV=new String[label.length];
                //lit et complète les données
                for (int i = 0; i < Math.min(tab.length,label.length); i++) tabV[i]=tab[i];
                for (int i = Math.min(tab.length,label.length); i < label.length; i++)tabV[i]="";
                values.add(tabV);
                line=reader.readLine();
            }
            DataFrame dataFrame=new DataFrame(values.size());
            VAR_TYPE[]types=new VAR_TYPE[label.length];
            for (int i = 0; i < label.length; i++) {
                boolean decision=false;
                int j=0;
                types[i] = VAR_TYPE.STRING;
                while(!decision && j<values.size()) {
                    if(values.get(j)[i]!=null && values.get(j)[i].length()>0) {
                        decision=true;
                        if (isNumeric(values.get(j)[i])) {
                            types[i] = VAR_TYPE.FLOAT;
                            if (isInteger(values.get(j)[i])) types[i] = VAR_TYPE.FLOAT;
                        }
                    }
                    j++;
                }
                dataFrame.addColumn(label[i], types[i]);
            }
            for (int i = 0; i < values.size(); i++) {
                DataLine dataLine=new DataLine(label,types);
                for (int j = 0; j < label.length; j++) {
                    if(values.get(i)[j]!=null){
                        if(types[j]==VAR_TYPE.STRING)dataLine.addString(j,values.get(i)[j]);
                        //teste si il ya une valeur sinon met un  NaN
                        else if(values.get(i)[j].length()>0 && types[j]==VAR_TYPE.INT) {
                            if (values.get(i)[j].length() > 0) dataLine.addInt(j, Integer.parseInt(values.get(i)[j]));
                            else dataLine.addInt(j, Integer.MAX_VALUE);
                        }else if(types[j]==VAR_TYPE.FLOAT){
                            if(values.get(i)[j].length()>0)dataLine.addFloat(j,Float.parseFloat(values.get(i)[j]));
                            else dataLine.addFloat(j,Float.NaN);
                        }
                    }
                }
                dataFrame.setLine(i,dataLine);
            }
            reader.close();
            return dataFrame;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public float[][] getValues(boolean withoutStringColumns){
        if(! withoutStringColumns) {
            float[][] values = new float[names_columns.size()][number_line];
            for (int i = 0; i < names_columns.size(); i++) {
                values[i] = data_columns.get(i).getValuesFloat();
            }
            return values;
        }else {
            int nbC = 0;
            for (int i = 0; i < names_columns.size(); i++) if(types_columns.get(i) != VAR_TYPE.STRING) nbC++;

            float[][] values = new float[nbC][number_line];
            nbC = 0;
            for (int i = 0; i < names_columns.size(); i++) {
                if (types_columns.get(i) != VAR_TYPE.STRING) {
                    values[nbC] = data_columns.get(i).getValuesFloat();
                    nbC++;
                }
            }
            return values;
        }
    }

    public DataFrame subPart(int[]index,boolean onLine){
        if(onLine) {
            DataFrame subData = new DataFrame(index.length);
            for (int i = 0; i < getNumber_col(); i++) {
                subData.addColumn(names_columns.get(i), types_columns.get(i));
            }
            for (int i = 0; i < index.length; i++) {
                subData.setLine(i, this.getLine(index[i]));
            }
            return subData;
        }else {
            DataFrame subData=new DataFrame(number_line);
            for (int i:index)  {
                if(types_columns.get(i)==VAR_TYPE.FLOAT)subData.addColumn(names_columns.get(i),data_columns.get(i).getValues_float().clone());
                else if(types_columns.get(i)==VAR_TYPE.INT)subData.addColumn(names_columns.get(i),data_columns.get(i).getValues_int().clone());
                else if(types_columns.get(i)==VAR_TYPE.STRING)subData.addColumn(names_columns.get(i),data_columns.get(i).getValues_string().clone());
            }
            return subData;
        }
    }



    public DataColumn getColumn(String name){
        int index=names_columns.indexOf(name);
        if(index==-1){
            System.err.println("There is no column with a name:"+name);
            return null;
        }
        return getColumn(index);
    }
    public DataColumn getColumn(int index){
        return data_columns.get(index);
    }

    public DataLine getLine(int indexLine){
        DataLine line=new DataLine(Arrays.copyOf(names_columns.toArray(), names_columns.size(), String[].class),Arrays.copyOf(types_columns.toArray(), types_columns.size(), VAR_TYPE[].class));
        for (int i = 0; i <names_columns.size() ; i++) {
            if(types_columns.get(i)==VAR_TYPE.FLOAT)line.addFloat(i,data_columns.get(i).getFloat(indexLine));
            else if(types_columns.get(i)==VAR_TYPE.INT)line.addInt(i,data_columns.get(i).getInt(indexLine));
            else if(types_columns.get(i)==VAR_TYPE.STRING)line.addString(i,data_columns.get(i).getString(indexLine));
        }
        return line;
    }

    public DataLine setLine(int indexLine,DataLine line){
        for (int i = 0; i <names_columns.size() ; i++) {
            if(types_columns.get(i)==VAR_TYPE.FLOAT)data_columns.get(i).setFloat(indexLine,line.getFloat(i));
            else if(types_columns.get(i)==VAR_TYPE.INT)data_columns.get(i).setInt(indexLine,line.getInt(i));
            else if(types_columns.get(i)==VAR_TYPE.STRING)data_columns.get(i).setString(indexLine,line.getString(i));
        }
        return line;
    }

    @Override
    public String toString() {
        int lineToRender=number_line+1;
        boolean skip=false;
        if(lineToRender>12){
            lineToRender=12;
            skip=true;
        }

        String[][]tabString=new String[lineToRender][names_columns.size()+1];

        //set the label
        for (int i = 0; i < names_columns.size(); i++) tabString[0][i]=names_columns.get(i);
        tabString[0][names_columns.size()]="index";
        //load the values
        int nb_line_first_section=skip?6:lineToRender;
        for (int i = 1; i < nb_line_first_section; i++) {
            for (int j = 0; j < names_columns.size(); j++) tabString[i][j]=data_columns.get(j).valueToString(i-1);
            tabString[i][names_columns.size()]=""+(i-1);
        }
        if(skip){
            for (int j = 0; j < names_columns.size()+1; j++) tabString[6][j]="...";
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < names_columns.size(); j++) tabString[i+7][j]=data_columns.get(j).valueToString(number_line-5+i);
                tabString[i+7][names_columns.size()]=""+(number_line-5+i);
            }
        }

        //set the value the same length per column
        for (int j = 0; j < names_columns.size()+1; j++){
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
            for (int j = 0; j < names_columns.size()+1; j++){
                line+=tabString[i][j]+"| ";
            }
            line+="\n";
            finalLine+=line;
        }
        finalLine+="Column: "+names_columns.size()+", Lines: "+number_line;
        return finalLine;
    }

    public void addColumn(String key, VAR_TYPE type){
        DataColumn column=new DataColumn(key,type,number_line);
        names_columns.add(key);
        types_columns.add(type);
        data_columns.add(column);
    }

    public void addColumn(String key,float[] values){
        if(values.length!=number_line){
            System.err.println("The column "+key+" can't be added because its length is "+values.length+" instead of "+number_line);
            return;
        }
        DataColumn column=new DataColumn(key,values);
        names_columns.add(key);
        types_columns.add(VAR_TYPE.FLOAT);
        data_columns.add(column);
    }
    public void addColumn(String key,int[] values){
        if(values.length!=number_line){
            System.err.println("The column "+key+" can't be added because its length is "+values.length+" instead of "+number_line);
            return;
        }
        DataColumn column=new DataColumn(key,values);
        names_columns.add(key);
        types_columns.add(VAR_TYPE.INT);
        data_columns.add(column);
    }
    public void addColumn(String key,String[] values){
        if(values.length!=number_line){
            System.err.println("The column "+key+" can't be added because its length is "+values.length+" instead of "+number_line);
            return;
        }
        DataColumn column=new DataColumn(key,values);
        names_columns.add(key);
        types_columns.add(VAR_TYPE.STRING);
        data_columns.add(column);
    }



    public List<String> getNames_columns() {
        return names_columns;
    }

    public List<VAR_TYPE> getTypes_columns() {
        return types_columns;
    }

    public List<DataColumn> getData_columns() {
        return data_columns;
    }

    public int getNumber_line() {
        return number_line;
    }

    public int getNumber_col(){
        return data_columns.size();
    }

    public static boolean isNumeric(String str) {
        try {
            Float.parseFloat(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
}
