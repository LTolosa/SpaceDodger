import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;


public class Main {
	static String windowTitle = "SpaceDodger";
	
	//Lights
	private static float[] ambient = {0.4f, 0.4f, 1.0f, 1.0f};
	private static float[] position = {5.0f, 5.0f, 5.0f, 1.0f};
	private static float[] diffuse = {0.7f, 0.7f, 0.7f, 1.0f};
	private static float[] specular = {1.0f, 1.0f, 1.0f, 1.0f};
	public static Random rg;
	public static boolean closeRequested = false;
	public static long lastFrameTime;
    public static int playerList;
    public static Player player;
    public static Texture center, sText;
    
    public static ArrayList<Vector3f> obst;
	
    public static int skyList, cometList;
    public static Sphere sphere;
    
	public static void initDisplay(){
		try {
			Display.setDisplayMode(new DisplayMode(960, 540));
			Display.setVSyncEnabled(true);
			Display.setTitle(windowTitle);
			Display.create();
			
			
		} catch (LWJGLException e) {
			Sys.alert("Error", "Initialization failed!\n\n" + e.getMessage());
            System.exit(0);
		}
	}
	
	public static void lights(){
		FloatBuffer ambBuf = BufferUtils.createFloatBuffer(ambient.length);
		ambBuf.put(ambient);
		ambBuf.flip();
		FloatBuffer posBuf = BufferUtils.createFloatBuffer(position.length);
		posBuf.put(position);
		posBuf.flip();
		FloatBuffer mDiffuseBuf = BufferUtils.createFloatBuffer(diffuse.length);
		mDiffuseBuf.put(diffuse);
		mDiffuseBuf.flip();
		FloatBuffer mSpecBuf = BufferUtils.createFloatBuffer(specular.length);
		mSpecBuf.put(specular);
		mSpecBuf.flip();
		
		glLightModel(GL_LIGHT_MODEL_AMBIENT, ambBuf);
		glColorMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE);
		glLight(GL_LIGHT0, GL_AMBIENT, ambBuf);
		glLight(GL_LIGHT0, GL_POSITION, posBuf);
		glLight(GL_LIGHT0, GL_DIFFUSE, mDiffuseBuf);
	}

	private static void initGL(){
		int height = Display.getDisplayMode().getHeight();
		int width = Display.getDisplayMode().getWidth();
		
		glViewport(0, 0, width, height);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		GLU.gluPerspective(45f, (float)width/(float)height, 4f, 500f);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		
		glShadeModel(GL_SMOOTH);
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glClearDepth(1.0f);
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);
		
		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
		glEnable(GL_LIGHTING);
		glEnable(GL_LIGHT0);
		glEnable(GL_COLOR_MATERIAL);
		glEnable(GL_NORMALIZE);
		Camera.create();
		player = new Player("ship.obj");
		player.loadPlayer();
		rg = new Random();
		
		obst = new ArrayList<Vector3f>();
		sphere = new Sphere();
		
		try {
			center = TextureLoader.getTexture("JPG", new FileInputStream(new File("center.jpg")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		loadSkybox();
		loadComets();
		
	}
	
	
	public static void gameLoop(){
	//Game loop keeps game running
		long time = getTime();
		try {
			sText = TextureLoader.getTexture("BMP", ResourceLoader.getResourceAsStream("stone1.bmp"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	 
		while(!closeRequested){
			long nTime = getTime();
			int delta = getDelta();
			glPushMatrix();
				glLoadIdentity();
				Camera.apply();
				lights();
			glPopMatrix();
			pollInput(delta);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			renderSky();
			player.update(delta);
			player.render(nTime);
			if(nTime > time + 750){
				time = getTime();
				createComets(-50);
			}
			moveComets(delta);
			renderSky();
			Display.update();
		}
	}
	
	public static void createComets(int z){
		for(int i = 0; i < 20; i++){
			Vector3f newPos = new Vector3f(rg.nextInt(20)-10, rg.nextInt(20)-10, z);
			boolean add = true;
			for(Vector3f o : obst){
				if(collision(o, newPos)){
					i--;
					add = false;
				}
			}
			if(add) obst.add(newPos);
		}


	}
	
	public static void moveComets(int delta){
		for(int i = 0; i < obst.size();){
			Vector3f sq = obst.get(i);
			
			renderComets(sq);
			
			sq.z += 0.01*delta*(i+1)/5;
			if(sq.z > 10f)
				obst.remove(i);
			else 
				i++;
			playerCollider(player, sq);
		}
	}

	public static long getTime(){
		return (Sys.getTime() * 1000)/Sys.getTimerResolution();
	}
	
	public static void cleanUp(){
		Display.destroy();
	}
	
	public static void playerCollider(Player p, Vector3f v){
		if(!p.hit && collision(p.getPos(), v)){
			p.hit = true;
			p.timeHit = getTime();
		}
			
	}
	
	//Uses sphere collision detection
	public static boolean collision(Vector3f ppos, Vector3f opos){
		float left = (ppos.x-opos.x)*(ppos.x-opos.x)+(ppos.y-opos.y)*(ppos.y-opos.y) + (ppos.z-opos.z)*(ppos.z-opos.z);
		return left < (1.5*1.5);
	}
	
    /**
     * Poll Input
     */
    public static void pollInput(int delta) {
        Camera.acceptInput(delta);
        // scroll through key events
        while (Keyboard.next()) {
            if (Keyboard.getEventKeyState()) {
                if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE)
                    closeRequested = true;
            }
        }

        if (Display.isCloseRequested()) {
            closeRequested = true;
        }
    }
    
    public static void loadComets(){
    	cometList = glGenLists(1);
    	glNewList(cometList, GL_COMPILE);
    		sphere.setTextureFlag(true);
			sphere.setDrawStyle(GLU.GLU_FILL);
			sphere.setNormals(GLU.GLU_SMOOTH);
			sphere.draw(1f, 50, 50);
		glEndList();
    }
    
    public static void renderComets(Vector3f sq){
		glPushMatrix();
			glEnable(GL_TEXTURE_2D);
			glLoadIdentity();
			Camera.apply();
			glTranslatef(sq.x, sq.y, sq.z);
			glRotated(90, 0, 1, 0);
			glColor3f(1f, 1f, 1f);
			sText.bind();
			glCallList(cometList);
			glDisable(GL_TEXTURE_2D);
		glPopMatrix();
    }
    
    public static void loadSkybox(){
    	skyList = glGenLists(1);
    	glNewList(skyList, GL_COMPILE);
			glBegin(GL_QUADS);
			   	glTexCoord2f(0f, 1f);
			    glVertex3f(-100.0f, -100.0f, -100.0f); // Bottom Right Of The Quad (Back)
			    glTexCoord2f(1f, 1f);
			    glVertex3f(100.0f, -100.0f, -100.0f); // Bottom Left Of The Quad (Back)
			    glTexCoord2f(1f, 0f);
			    glVertex3f(100.0f, 100.0f, -100.0f); // Top Left Of The Quad (Back)
			    glTexCoord2f(0f, 0f);
			    glVertex3f(-100.0f, 100.0f, -100.0f); // Top Right Of The Quad (Back)
			glEnd();
        glEndList();
    }
    
    public static void renderSky(){
    	glPushMatrix();
    		glLoadIdentity();
    		Camera.apply();
    		glTranslatef(Camera.getX(), Camera.getY(), Camera.getZ());
    		glEnable(GL_TEXTURE);
    		center.bind();
    		glCallList(skyList);
    		glDisable(GL_TEXTURE);
    	glPopMatrix();
    		
    }
    
    /** 
     * Calculate how many milliseconds have passed 
     * since last frame.
     * 
     * @return milliseconds passed since last frame 
     */
    public static int getDelta() {
        long time = (Sys.getTime() * 1000) / Sys.getTimerResolution();
        int delta = (int) (time - lastFrameTime);
        lastFrameTime = time;
     
        return delta;
    }

	public static void main(String[] args) {
		initDisplay();
		initGL();
		gameLoop();
		cleanUp();

	}

}
