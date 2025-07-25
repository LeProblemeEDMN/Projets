package GUI.guis;

import GUI.GUI;
import GUI.elements.*;
import GUI.font.FontManager;
import main.Constantes;
import main.Pipeline;
import org.joml.Vector2f;
import rendering.capture_object.Fbo;
import rendering.shadow.CascadedShadowMap;
import toolbox.maths.Vector3;

public class OptionGuis extends GUI {
    GuiSlider slider_exposure;
    GuiSliderDiscrete slider_z_pre_pass;
    GuiSliderDiscrete slider_shadow_quality;
    ButtonGUI button_apply;
    GuiSliderDiscrete slider_ANTIALISING;

    GuiTextBar text_bar;

    public boolean modif=false;

    public OptionGuis() {
        this.getElements().add(new GUIImage(new Vector2f(0.5f,0.5f),new Vector2f(1f,1f),new Vector3(0.2,0.2,0.2),0.5f));

        slider_exposure=new GuiSlider(new Vector2f(0.25f,0.95f),new Vector2f(0.3f,0.05f),0.25f,0.8f, FontManager.fonts.get("cambria"),new Vector3(1,1,1),new Vector3(0.5,0.5,0.5),0.1f,5f, null);
        this.getElements().add(slider_exposure);
        slider_z_pre_pass=new GuiSliderDiscrete(new Vector2f(0.25f,0.9f),new Vector2f(0.3f,0.05f),0.25f,0.8f,FontManager.fonts.get("cambria"),new Vector3(1,1,1),new Vector3(0.5,0.5,0.5),new String[]{"true","false"}, null);
        this.getElements().add(slider_z_pre_pass);
        slider_shadow_quality=new GuiSliderDiscrete(new Vector2f(0.25f,0.85f),new Vector2f(0.3f,0.05f),0.25f,0.8f,FontManager.fonts.get("cambria"),new Vector3(1,1,1),new Vector3(0.5,0.5,0.5),new String[]{"Low","Medium","High","Ultra"}, null);
        this.getElements().add(slider_shadow_quality);
        slider_ANTIALISING=new GuiSliderDiscrete(new Vector2f(0.25f,0.8f),new Vector2f(0.3f,0.05f),0.25f,0.8f,FontManager.fonts.get("cambria"),new Vector3(1,1,1),new Vector3(0.5,0.5,0.5),new String[]{"None","FXAA"}, null);
        this.getElements().add(slider_ANTIALISING);

        this.getElements().add(new TextGUI(new Vector2f(0.04f,0.95f),new Vector2f(0.08f,0.05f),"Exposure",FontManager.fonts.get("cambria"),new Vector3(1,1,1)));
        this.getElements().add(new TextGUI(new Vector2f(0.04f,0.9f),new Vector2f(0.08f,0.05f),"Pre-pass",FontManager.fonts.get("cambria"),new Vector3(1,1,1)));
        this.getElements().add(new TextGUI(new Vector2f(0.04f,0.85f),new Vector2f(0.08f,0.05f),"Shadows",FontManager.fonts.get("cambria"),new Vector3(1,1,1)));
        this.getElements().add(new TextGUI(new Vector2f(0.04f,0.8f),new Vector2f(0.08f,0.05f),"Antialiasing",FontManager.fonts.get("cambria"),new Vector3(1,1,1)));

        button_apply=new ButtonGUI(new Vector2f(0.15f,0.07f),new Vector2f(0.2f,0.1f),new Vector3(106.0f/255,87.0f/255,133.0f/255), FontManager.fonts.get("cambria"),"Apply",new Vector3(1,1,1));
        button_apply.setOnRelease(()-> button_apply.color=new Vector3(106.0f/255,87.0f/255,133.0f/255));
        button_apply.setOnClick(()->modif=true);
        this.getElements().add(button_apply);

        text_bar=new GuiTextBar(new Vector2f(0.15f,0.5f),new Vector2f(0.2f,0.1f),new Vector3(106.0f/255,87.0f/255,133.0f/255), FontManager.fonts.get("cambria"),"Test",new Vector3(1,1,1));
        this.getElements().add(text_bar);
        this.setActive(false);

    }

    public void apply_change(){
        button_apply.color=new Vector3(106.0f/255,87.0f/255,133.0f/255).getMul(0.5f);

        Constantes.EXPOSURE.value=slider_exposure.getValue();
        Constantes.Z_PRE_PASS.value_string=slider_shadow_quality.getValueString();
        String resol_shadow=slider_shadow_quality.getValueString();
        Constantes.ANTIALIASING.value_string=slider_ANTIALISING.getValueString();

        if(resol_shadow.equals("Low") && CascadedShadowMap.instance.S_y!=512){
            CascadedShadowMap.instance.cleanUp();
            Constantes.CASCADED_SHADOW_R.value=3f;
            Constantes.SHADOW_MAP_SIZE.value=512;
            CascadedShadowMap newCSM=new CascadedShadowMap();
            Pipeline.replaceWorkflow(newCSM);
            CascadedShadowMap.instance=newCSM;
        }else if(resol_shadow.equals("Medium") && CascadedShadowMap.instance.S_y!=1024){
            CascadedShadowMap.instance.cleanUp();
            Constantes.CASCADED_SHADOW_R.value=2f;
            Constantes.SHADOW_MAP_SIZE.value=1024;
            CascadedShadowMap newCSM=new CascadedShadowMap();
            Pipeline.replaceWorkflow(newCSM);
            CascadedShadowMap.instance=newCSM;
        }else if(resol_shadow.equals("High") && CascadedShadowMap.instance.S_y!=2048){
            CascadedShadowMap.instance.cleanUp();
            Constantes.CASCADED_SHADOW_R.value=1.5f;
            Constantes.SHADOW_MAP_SIZE.value=2048;
            CascadedShadowMap newCSM=new CascadedShadowMap();
            Pipeline.replaceWorkflow(newCSM);
            CascadedShadowMap.instance=newCSM;
        }else if(resol_shadow.equals("Ultra") && CascadedShadowMap.instance.S_y!=4096){
            CascadedShadowMap.instance.cleanUp();
            Constantes.CASCADED_SHADOW_R.value=1.2f;
            Constantes.SHADOW_MAP_SIZE.value=4096;
            CascadedShadowMap newCSM=new CascadedShadowMap();
            Pipeline.replaceWorkflow(newCSM);
            CascadedShadowMap.instance=newCSM;
        }
        modif=false;
        this.setActive(false);
    }
}
;