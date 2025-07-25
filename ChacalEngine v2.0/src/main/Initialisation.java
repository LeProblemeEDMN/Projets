package main;
//This class have been automatiquely generated.

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import toolbox.XMLParser.XMLParser;
import java.util.HashMap;
import java.util.Objects;
import rendering.render_objects.RenderingWorkflow;

import postProcessingEffect.FXAA.FXAA;
import postProcessingEffect.DefferedRendering.DefferedRenderer;
import postProcessingEffect.gaussianBlur.VerticalBlur;
import postProcessingEffect.gaussianBlur.HorizontalBlur;
import postProcessingEffect.SSAO.SSAO;
import postProcessingEffect.SSGI.SSGIRenderer;
import postProcessingEffect.ToneMapping.ToneMapping;
import rendering.deffered_rendering.Deffered_workflow;
import rendering.depthWorkflow.Depth_workflow;
import rendering.shadow.Shadow;
import rendering.shadow.CascadedShadowMap;
import rendering.postprocessing.SSAOWorkflow;
import rendering.simple_rendering.Simple_workflow;



public class Initialisation {
    public static void init_rendering_workflows(){
        Document document= XMLParser.read_XML_file("src/config.xml");
        NodeList glslList = document.getElementsByTagName("runtimeWorkflow");
        if (glslList.getLength() > 0) {
            Element glslElement = (Element) glslList.item(0);

            // Accéder aux éléments directory
            NodeList directories = glslElement.getElementsByTagName("workflow");
            HashMap<Integer, RenderingWorkflow> map=new HashMap<>();
            int bigger_order=0;//we count the bigger order which si different from the number of shader
            //because we can have -1 as an order to just init the effect without using it.
            for (int i = 0; i < directories.getLength(); i++) {
                Element dirElement = (Element) directories.item(i);
                String effect_name=dirElement.getTextContent();
                int order=Integer.parseInt(dirElement.getAttribute("order"));

                if(Deffered_workflow.NAME.equals(effect_name)){
                    Deffered_workflow.instance = new Deffered_workflow();
                   map.put(order,Deffered_workflow.instance);
                }
                else if(Depth_workflow.NAME.equals(effect_name)){
                    Depth_workflow.instance = new Depth_workflow();
                   map.put(order,Depth_workflow.instance);
                }
                else if(Shadow.NAME.equals(effect_name)){
                    Shadow.instance = new Shadow();
                   map.put(order,Shadow.instance);
                }
                else if(CascadedShadowMap.NAME.equals(effect_name)){
                    CascadedShadowMap.instance = new CascadedShadowMap();
                   map.put(order,CascadedShadowMap.instance);
                }
                else if(SSAOWorkflow.NAME.equals(effect_name)){
                    SSAOWorkflow.instance = new SSAOWorkflow();
                   map.put(order,SSAOWorkflow.instance);
                }
                else if(Simple_workflow.NAME.equals(effect_name)){
                    Simple_workflow.instance = new Simple_workflow();
                   map.put(order,Simple_workflow.instance);
                }

                else throw new RuntimeException("The workflow "+effect_name+" is unknown.");
                bigger_order=Math.max(bigger_order,order);
            }

            for (int i = 0; i <= bigger_order; i++) {
                Pipeline.addRW(map.get(i));
            }
            System.out.println(Pipeline.postprocessingEffects);
        }

    }
    public static void init_post_processing(){
        Document document= XMLParser.read_XML_file("src/config.xml");
        NodeList glslList = document.getElementsByTagName("runtimePostProcessing");
        if (glslList.getLength() > 0) {
            Element glslElement = (Element) glslList.item(0);

            // Accéder aux éléments directory
            NodeList directories = glslElement.getElementsByTagName("effect");
            HashMap<Integer,String> map=new HashMap<>();
            int bigger_order=0;//we count the bigger order which si different from the number of shader
            //because we can have -1 as an order to just init the effect without using it.
            for (int i = 0; i < directories.getLength(); i++) {
                Element dirElement = (Element) directories.item(i);
                String effect_name=dirElement.getTextContent();
                int order=Integer.parseInt(dirElement.getAttribute("order"));
                map.put(order,effect_name);
                if(FXAA.NAME.equals(effect_name)) FXAA.instance = new FXAA();
                else if(DefferedRenderer.NAME.equals(effect_name)) DefferedRenderer.instance = new DefferedRenderer();
                else if(VerticalBlur.NAME.equals(effect_name)) VerticalBlur.instance = new VerticalBlur();
                else if(HorizontalBlur.NAME.equals(effect_name)) HorizontalBlur.instance = new HorizontalBlur();
                else if(SSAO.NAME.equals(effect_name)) SSAO.instance = new SSAO();
                else if(SSGIRenderer.NAME.equals(effect_name)) SSGIRenderer.instance = new SSGIRenderer();
                else if(ToneMapping.NAME.equals(effect_name)) ToneMapping.instance = new ToneMapping();

                else throw new RuntimeException("The Post processing effect "+effect_name+" is unknown.");
                bigger_order=Math.max(bigger_order,order);
            }

            for (int i = 0; i <= bigger_order; i++) {
                Pipeline.postprocessingEffects.add(map.get(i));
            }

        }

    }
}
