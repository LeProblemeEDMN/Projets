package PostProcessing.DLSS;

import Main.MainRender;
import PostProcessing.ImageRenderer;
import RenderEngine.DisplayManager;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class DLSS {
	public static int SIZE=3;
	private ImageRenderer renderer;
	private DLSSShader shader;
	
	public DLSS(){
		shader = new DLSSShader();
		shader.start();
		shader.connectTextureUnits();
		shader.width.loadInt(Display.getWidth());
		shader.height.loadInt(Display.getHeight());
		shader.size.loadInt(SIZE);
		shader.halfsize.loadInt(1);
	/*	double[] red={0.05030357279043917,-0.1982293800197531,-0.003686393948153523,-0.15823968456806226,1.0081690678893562,0.13743956102207935,-0.0374267550763431,0.17292367834535863,0.02853198185774742,-0.04700220468692533,-0.15917569415010882,0.05547602518744376,0.18998536348888315,0.9871927508895657,-0.1913093480024722,-0.006954035928378774,0.19278785299545287,-0.02149408390134716,-0.021940842323273008,0.1797574238341772,-0.00727256271519484,-0.1755321577345553,0.9822702709039781,0.2069487327844755,0.05545431562656843,-0.17077850744771111,-0.049439837603486234,0.01791121624481463,0.17852883701627614,-0.045508316218153474,0.14564922512220804,1.0184167426809654,-0.15117170576630024,-0.012210313840735046,-0.19379964120843152,0.04151846882658067};
		double[] green={0.06062230317020091,-0.1992540442442316,-0.00905536560923541,-0.16915245116943226,1.0009441274320077,0.15294163207134734,-0.03493938822166206,0.180570057818702,0.017030406641051234,-0.039888601174950265,-0.1622509467241881,0.05448381514821117,0.1771642521205056,0.9952737742957377,-0.18996185531613535,0.0022349442982911886,0.1873211962755032,-0.02490411089654928,-0.026084433615624347,0.17347738726317027,0.0004966883085943727,-0.17050736133399697,0.9905527503569521,0.1965625848029041,0.053043961747345686,-0.1767706376980618,-0.04130186760368679,0.004198065288580508,0.18976543476721883,-0.04716134053284735,0.16481971328464212,1.008357719825722,-0.15718367679855594,-0.021826325402173455,-0.18945909924679702,0.047831035626689986};
		double[] blue={0.05560359454096768,-0.1892104926900193,-0.01364546723932771,-0.1714375110613356,1.001814778221003,0.1520560427044818,-0.027529963813922045,0.16881287678339843,0.022961739397281765,-0.03997262784083622,-0.15916117578493771,0.052718905853500315,0.1768063900785705,0.9926746282065749,-0.1878268619715087,0.0017546082016346228,0.1880975387833154,-0.025794544760371244,-0.025357882033867653,0.17090809485673325,0.00046264360181574394,-0.1689565335746955,0.9904232389071813,0.1966464694802174,0.05283765398511888,-0.1779721795445742,-0.039817465534216535,0.008311667471964226,0.17925738744609365,-0.04102164667230292,0.16560548156197802,1.010667486499055,-0.15893590172394634,-0.028774930665337557,-0.17702403728932442,0.04098863798987775};
*/
		double[]red={0.040636254915787776,-0.14311594617886214,0.0005274763432307037,-0.06453675475613746,0.7531397091667013,0.24924703259283945,-0.030871924669509467,0.2499083258529936,-0.05363974113613577,0.016455591300619552,-0.15744893864841283,0.034135016474671764,0.12140735244314191,0.9099692092100654,-0.10793018627190784,0.15020322988753787,0.0336122125061465,0.0011048825956547874,0.004709461358340191,0.044757832126691564,0.12544174712261746,-0.08861576483006652,0.9468071375671572,0.09411061857703824,0.03317682974702463,-0.16120915030609317,0.0017338695606446213,-0.062423157806793125,0.2864911280419639,-0.07211136959575727,0.34198200887717833,0.6695056507896322,-0.03818322903829703,-0.02373750415884958,-0.13262421899746046,0.031664456725278935};
		double[]green={0.03273892701585042,-0.12416440142128869,0.02295896509858627,-0.04179344605864818,0.7092416150553863,0.21836657232769288,-0.021531046119958173,0.24595193223818856,-0.04160863712711032,0.023537060452776967,-0.14397115115667788,0.0472888038884061,0.13265982700367546,0.8311070776798355,-0.08553442601087927,0.16767776737808085,0.020938585755857806,0.006770372579783559,0.007646076671519803,0.04550768764712058,0.12806948286751957,-0.07465924761689112,0.8920423723397127,0.09121365055477473,0.04304333493150492,-0.12393457030671266,-0.009208218245567562,-0.045494497714249904,0.27845025071295537,-0.07012133779512761,0.339251171007296,0.5845791240078224,0.006198862354139452,0.011297928651365308,-0.12467780247671961,0.020124009384207955};
		double []blue={0.04451620332814688,-0.14474203032032068,0.022327362039641366,-0.05060237015649662,0.7232297753045437,0.2234968095438266,-0.02498786912296995,0.2401961736361163,-0.03219147560329347,0.03216559910663476,-0.16682665729051704,0.0532982469649937,0.14913728177473126,0.8348096936026012,-0.09602560986165921,0.1644451591711306,0.01826842034313502,0.012467358478911533,0.02232700214502908,0.060008013708079865,0.12881763617738326,-0.0992761802161589,0.8760655231980616,0.10208651385451467,0.05282626988962067,-0.1330723427463005,-0.009351644961953771,-0.010265358039183534,0.24859600355139075,-0.048290443601907644,0.30385174615705396,0.6187566022844887,-0.01936746263368643,0.03544764533332053,-0.15463574848272396,0.026505408003951774};

		for (int i=0;i<red.length;i++){
			shader.coeffRed.loadFloat((float)red[i],i);
			shader.coeffGreen.loadFloat((float)green[i],i);
			shader.coeffBlue.loadFloat((float)blue[i],i);
		}

		shader.stop();
		renderer = new ImageRenderer(Display.getWidth(),Display.getHeight());
		//renderer = new ImageRenderer();
	}

	long t=0;
	double x=0;
	double y=0;
	double vx=0;
	double vy=0;
	public void render(int colourTexture){
		shader.start();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, colourTexture);

		renderer.renderQuad();
		shader.stop();
	}
	
	public void cleanUp(){
		renderer.cleanUp();
		shader.cleanUp();
	}
	public int getOutputTexture(){
		return renderer.getOutputTexture();
	}
	public void resize(int w,int h) {
		renderer.cleanUp();
		renderer = new ImageRenderer(w,h);
		shader.start();
		shader.width.loadInt(w);
		shader.height.loadInt(h);
		shader.stop();
	}


}
