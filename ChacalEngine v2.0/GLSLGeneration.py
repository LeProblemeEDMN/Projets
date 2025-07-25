"""
Documentation of the Shader generation code
This code will read the config.xml file in the src directory.
In this file it will look for tags directory and in which one of them it will look for their own config.xml files
In all the config file it also look for shader tags. For each tag three informations must be provided:
- vertex having the value of the relative path of the vertex shader file
- fragment having the value of the relative path of the fragment shader file
- name which will be the name of the auto-generated file
This code will create .java class for each shader. This file will have ShaderAttrib and ShaderAttribArray variables for
each uniform used in the shader.

It is possible to use the tag INIT_VALUE:smthg to initialize the uniform variable with the value smthg
For example uniform float test;//INIT_VALUE:1.5 will initialize the uniform test with the value 1.5.
If the uniform is an array the array index will be i (i.e. to put the value i² on the i-th element on the array
one should use INIT_VALUE:i*i

The tag INDEX_BINDING:integer is used to bind the inputs of the vertex shader to a VBO index of a VAO.
For example in vec3 position;//INDEX_BINDING:0 will bind position to the first VBO.

The tags //BEGINNING_INIT_CODE:args and //END_INIT_CODE must be put in the fragment shader the text in between
will be put after the start(); int during the init. The args will be the argument of the init function
example:
//BEGINNING_INIT_CODE:int a
//int b=a*a;
//END_INIT_CODE
will produce
public void init(int a){
    start();
    int b=a*a;

The same thing can be done for the resize function of the shader. with the tag BEGINNING_RESIZE_CODE/END_RESIZE_CODE and
with the tags //RESIZE_VALUE:smthg if both init and resize must be defined for the uniform variable //RESIZE_INIT_VALUE
or //INIT_RESIZE_VALUE: can be used. It is not possible to use different initialisation for the init and resize functions


DOCUMENTATION OF THE Initialisation.java generator
The Initialisation.java class allow to only create instances of the wanted workflow and postprocessing (W&P) effects.
The W&P used in the rendering must be put in the config file in the src directory. In this directory, the Postprocessign effect can
be created with:
<runtimePostProcessing>
        <effect order="0">DefferedRendering</effect>
        <effect order="1">FXAA</effect>
        <effect order="2">ToneMapping</effect>
        <effect order="-1">HorizontalBlur</effect>
        <effect order="-1">VerticalBlur</effect>
    </runtimePostProcessing>
If order is <0 then the effect is just instanced but never directly used in the pipeline. If the order>0 then all the number
must not have gaps (i.e. 0,1,2,3...) and the postprocessing pipeline will apply the effects one by one on the specified order

The same thing can be done for the workflow pipeline with
<runtimeWorkflow>
        <workflow order="0">CascadeShadowMap</workflow>
        <workflow order="1">DefferedRendering</workflow>
</runtimeWorkflow>
Here the order cannot be negative (i.e. all the workflow must be used in the pipeline).

The Initialisation.java is used to instanced the W&P from their name. To do that this class is automatiquely generated
upon compilation. The different W&P must but indicated in local config.xml files with the tags:
 -<postProcessing name=""/> for post processing effects
 -<workflow name="SSAOWorkflow"/> for workflows
The code of this files will not be read but the config.xml MUST be located in the same directory as the file called "name".java
because the import will use the path of the config.xml file.
"""

import xml.etree.ElementTree as ET
import os
import re
#EXPLORE THE CONFIG FILE ADN LIST ALL OF THE FILES TO PROCESS
files_to_process_for_shader_generation=[]
files_to_process_for_workflow_generation=[]
files_to_process_for_post_processing_generation=[]
path_explored=[]

def deterministic_hash(text):
  hash=0
  for ch in text:
    hash = ( hash*281  ^ ord(ch)*997) & 0xFFFFFFFF
  return hash


#read the GLSLGenerationCache.txt file that contains pair of elements (shader_name hash)
#if the hash of the sahder is unchanged then no need to recompile it.
cached_shader={}
cached_file=open("GLSLGenerationCache.txt","r")
for line in cached_file:
    parts=line.split(' ')
    if len(parts)==2:
        cached_shader[parts[0]]=int(parts[1])
cached_file.close()
cached_file=open("GLSLGenerationCache.txt","w")
def read_config(directory_path):
    if(path_explored.__contains__(directory_path)):
        return
    path_explored.append(directory_path)
    #check if files exist
    if not os.path.exists(directory_path) or not os.path.isfile(directory_path+'/config.xml'):
        print("Cannot find directory",directory_path," or the config.xml file does not exist")
        return
    tree = ET.parse(directory_path+'/config.xml')
    root = tree.getroot()
    #get the part corresponding to this parser
    glsl_part=root.find('GLSLGeneration')
    for child in glsl_part.iter('directory'):
        read_config(directory_path+"/"+child.attrib["name"])
    #list the file to process
    for child in glsl_part.iter('shader'):
        files_to_process_for_shader_generation.append([directory_path, child.find("vertex").text, child.find("fragment").text, child.find("name").text])

    for child in glsl_part.iter('postProcessing'):
        files_to_process_for_post_processing_generation.append(
            [directory_path, child.attrib["name"]])
    # list the file to process
    for child in glsl_part.iter('workflow'):
        files_to_process_for_workflow_generation.append(
            [directory_path, child.attrib["name"]])
read_config("src")
#TODO generer et permettre de mettre des valeurs par défaut (i.e. signature de lafonction init ou hardcode)
for id in range(len(files_to_process_for_shader_generation)):
    directory_path,vertex_file_name,fragment_file_name,shader_name=files_to_process_for_shader_generation[id]
    f=open(directory_path+"/"+vertex_file_name, "r")
    vertex_content=f.read()
    f.close()
    f = open(directory_path + "/" + fragment_file_name, "r")
    fragment_content = f.read()
    f.close()
    total_text = vertex_content + "\n" + fragment_content

    shader_hash = deterministic_hash(total_text)
    cached_file.write(shader_name + " " + str(shader_hash) + "\n")
    if(cached_shader.keys().__contains__(shader_name)):
        if(cached_shader[shader_name]==shader_hash):
            print(shader_name+" dont need to be generated!")
            continue

    #Get the init function code
    init_function_argument=""
    init_function_code=""
    if(fragment_content.__contains__("BEGINNING_INIT_CODE:")):
        init_function_argument=fragment_content.split("BEGINNING_INIT_CODE:")[1]
        init_function_argument=init_function_argument.split("\n")[0]
        parts=fragment_content.split("//BEGINNING_INIT_CODE")[1].split("//END_INIT_CODE")[0].split("\n")
        for p in parts[1:]:
            init_function_code+="        "+p[2:]+"\n"

    resize_function_argument = ""
    resize_function_code = ""
    if (fragment_content.__contains__("BEGINNING_RESIZE_CODE:")):
        resize_function_argument = fragment_content.split("BEGINNING_RESIZE_CODE:")[1]
        resize_function_argument = resize_function_argument.split("\n")[0]
        parts = fragment_content.split("//BEGINNING_RESIZE_CODE")[1].split("//END_RESIZE_CODE")[0].split("\n")
        for p in parts[1:]:
            resize_function_code += "        " + p[2:] + "\n"


    #first extract all of the struct present
    struct_parts=total_text.split("struct ")
    #dictionnary containing all of the informations (variables names and types) of the different structures
    structures_shader={}
    if(len(struct_parts)>1):
        for index in range(1,len(struct_parts)):
            #part contains name_struc { variables1; variable2; ...
            part=struct_parts[index].split("}")[0]
            name,variable=part.split("{")
            #name of the struct
            name=name.replace(" ","")
            variables=variable.split(";")
            structure_vars=[[],[]]#list containing the list of name and list of type of the variables
            for variable_string in variables:
                v=re.sub(' +', ' ',variable_string.strip())
                split=v.split(" ")
                if len(split)==2:
                    structure_vars[0].append(split[1])
                    structure_vars[1].append(split[0])
            structures_shader[name]=structure_vars

    #suppose the the line contains a unique uniform variable
    uniform_lines=[]#get all the lines with an uniform variable
    for line in vertex_content.splitlines():
        if(line.strip().startswith("uniform")):
            uniform_lines.append([line,"vertex"])
    for line in fragment_content.splitlines():
        if(line.strip().startswith("uniform")):
            uniform_lines.append([line,"fragment"])


    #create the java variables associated with the uniform variables of the shader
    java_variables_declaration=""
    java_init=""
    java_resize=""

    #dictionnary mapping the type of uniform varaible to their functions to load data
    type_to_input_functions={"mat4":"loadMatrix4f","vec4":"loadVector4f","vec3":"loadVector3","vec2":"loadVector2D","int":"loadInt"
        ,"float":"loadFloat","sampler2D":"loadInt","sampler2DMS":"loadInt","samplerCube": "loadInt"}
    type_to_attrib_array_definiton = {"mat4": "UNIFORM_TYPE.MAT4", "vec4": "UNIFORM_TYPE.VEC4", "vec3": "UNIFORM_TYPE.VEC3",
        "vec2": "UNIFORM_TYPE.VEC2", "int": "UNIFORM_TYPE.INT", "float": "UNIFORM_TYPE.FLOAT",
        "sampler2D": "UNIFORM_TYPE.TEXTURE", "sampler2DMS": "UNIFORM_TYPE.MULTISAMPLED_TEXTURE","samplerCube": "UNIFORM_TYPE.TEXTURE"}

    #process the uniform variable by either adding
    for line,type_shader in uniform_lines:
        l=line.replace("uniform","").split(";")[0].strip()
        l=re.sub(' +', ' ',l)#remove mutliple spaces
        is_an_array=l.__contains__('[')
        #two cases if the uniform is unique or is it an array
        if(is_an_array):
            array_size=l.split('[')[1].split(']')[0]
            l = re.sub(r'\[\d+\]', '', l)
            parts = l.split(' ')  # 0: type of the variable 1: name of the variable
            #if the uniform is a structure we need to create a java variable for each attribute of the struct.

            if structures_shader.keys().__contains__(parts[0]):
                current_declaration = "    //Array of uniform structure of type " + parts[
                    0] + " of the " + type_shader + " shader\n"
                for index in range(len(structures_shader[parts[0]][0])):
                    name=structures_shader[parts[0]][0][index]
                    current_declaration += ("    public ShaderAttribArray " + parts[1]+"_"+name + "=new ShaderAttribArray(\"" + \
                                           parts[1] + "[\", \"]."+name+"\", " + array_size + ", this, "+
                                            type_to_attrib_array_definiton[structures_shader[parts[0]][1][index]]+");\n")
            else:
                current_declaration = "    //Array of uniform variable of type " + parts[0] + " of the " + type_shader + " shader\n"
                current_declaration +=("    public ShaderAttribArray "+parts[1]+"=new ShaderAttribArray(\""+parts[1]+"[\", \"]\", "
                                       +array_size+", this, "+type_to_attrib_array_definiton[parts[0]]+");\n")
                #create the init for every element of the array
                if (line.__contains__("VALUE")):
                    apply_to_resize=line.__contains__("RESIZE_")
                    apply_to_init = line.__contains__("INIT_")
                    java_line = "        for (int i = 0; i < "+array_size+"; i++){\n"
                    java_line += "           " + parts[1] + "." + type_to_input_functions[parts[0]] + "(" + \
                                 line.split("VALUE:")[1] + ", i);\n        }\n"
                    if(apply_to_init):
                        java_init+=java_line
                    if (apply_to_resize):
                        java_resize += java_line
        #the uniform is not an array
        else:
            parts = l.split(' ')  # 0: type of the variable 1: name of the variable
            #the uniform cannot be a structure and not an array.
            if structures_shader.keys().__contains__(parts[0]):
                print("Cannot generate code for a single instance of a structure stop the program")
                raise Exception("Cannot generate code for a single instance of a structure")

            current_declaration="    //uniform variable of type "+parts[0]+" of the "+type_shader+" shader\n"
            current_declaration+="    public ShaderAttrib "+parts[1]+"=new ShaderAttrib(\""+parts[1]+"\", this,"+type_to_attrib_array_definiton[parts[0]]+");\n"

            #if need make the init
            if(line.__contains__("VALUE")):
                apply_to_resize = line.__contains__("RESIZE_")
                apply_to_init = line.__contains__("INIT_")
                java_line = "        " + parts[1]+"."+type_to_input_functions[parts[0]]+"("+line.split("VALUE:")[1]+");\n"
                if (apply_to_init):
                    java_init += java_line
                if (apply_to_resize):
                    java_resize += java_line

        java_variables_declaration+=current_declaration

    #load the binding of the inputs of the vertex shader
    bindings={}
    for line in vertex_content.splitlines():
        if(line.__contains__("INDEX_BINDING")):

            #get the name of the input
            name=re.sub(' +', ' ',line.split(";")[0].strip())
            # now the line is smthg like: in type name or layout (location=int) in type name
            if(name.__contains__(")")):
                name=name.split(")")[1].strip()
            #now the line is smthg like: in type name
            name=name.split(" ")[2]
            bindings[name]=line.split("INDEX_BINDING:")[1]


    file=open(directory_path+"/"+shader_name+".java","w")
    #write the package line
    print(directory_path)
    file.write("package "+directory_path.split("src/")[1].replace("/",".")+";\n\n")
    file.write("//This class have been automatiquely generated from the vertex shader: "+vertex_file_name+" and fragment shader: "+fragment_file_name+"\n")
    #write the imports
    file.write("import ShaderEngine.ShaderAttrib;\nimport ShaderEngine.ShaderAttribArray;\nimport ShaderEngine.ShaderProgram;"+\
        "import main.Pipeline;\nimport main.Constantes;\nimport main.DisplayManager;\nimport org.joml.*;\nimport toolbox.maths.Vector3;"+\
        "\nimport java.lang.Math;\n")

    file.write("public class "+shader_name+" extends ShaderProgram{\n")
    file.write(java_variables_declaration)

    #constructor the shader path are beginning from src so we need to remove everything before
    directory_path_shader_format=directory_path.split("src")[1]
    file.write("    public static String VERTEX_PATH = \""+directory_path_shader_format+"/"+vertex_file_name+"\";\n")
    file.write("    public static String FRAGMENT_PATH = \"" + directory_path_shader_format+"/"+fragment_file_name + "\";\n")
    file.write("\n    public "+shader_name+"() {\n")
    file.write("        super(VERTEX_PATH, FRAGMENT_PATH);\n")
    file.write("	    getAllUniformLocations();\n    }\n")

    #init function
    file.write("    public void init("+init_function_argument+") {\n        start();\n"+init_function_code)
    file.write(java_init)
    file.write("       stop();\n    }\n")

    #init binding
    file.write("    @Override\n")
    file.write("    protected void bindAttributes() {\n")
    for key in bindings.keys():
        file.write("        super.bindAttribute("+bindings[key]+", \""+key+"\");\n")
    file.write("    }\n")
    file.write("    public void resize("+resize_function_argument+"){\n        start();\n")
    file.write(resize_function_code)
    file.write(java_resize)
    file.write("        stop();\n    }\n")

    file.write("}")

    file.close()
cached_file.close()


#generate the file Ininialisation.java which manage the initilisation of the postprocessing effect and workflows.

import_string=""
workflow_string=""
post_processing_string=""
decalage="                "
for id in range(len(files_to_process_for_post_processing_generation)):
    directory_path,file_name=files_to_process_for_post_processing_generation[id]
    import_string+="import "+directory_path.replace("/",".").replace("src.","")+"."+file_name+";\n"
    post_processing_string+=decalage+["if","else if"][id>0]+"("+file_name+".NAME.equals(effect_name)) "+file_name+".instance = new "+file_name+"();\n"

for id in range(len(files_to_process_for_workflow_generation)):
    directory_path,file_name=files_to_process_for_workflow_generation[id]
    import_string+="import "+directory_path.replace("/",".").replace("src.","")+"."+file_name+";\n"
    workflow_string+=decalage+["if","else if"][id>0]+"("+file_name+".NAME.equals(effect_name)){\n"\
                             +decalage+"    "+file_name+".instance = new "+file_name+"();\n"+decalage+\
                            "   map.put(order,"+file_name+".instance);\n"+decalage+"}\n"

replaces={"#IMPORTS":import_string,"#WORKFLOWS_IF_ELSE":workflow_string,"#POST_PROCESSING_IF_ELSE":post_processing_string}
f=open("Initialisation.template","r")
text=f.read()
f.close()
for k in replaces.keys():
    text=text.replace(k,replaces[k])
f=open("src/main/Initialisation.java","w")
f.write(text)
f.close()



