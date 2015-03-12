import static org.lwjgl.opengl.GL11.*;

import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

public class Player {
	private Vector3f pos;
	private Vector3f rotation;
	private Model model;
	public long timeHit= 0;
	public boolean hit = false;
	
	private float moveSpeed = 0.01f;
	
	public Player(String modelFile){
		Vector3f cPos = new Vector3f(Camera.getPos());
		cPos.z -= 7f;
		cPos.y -= 1f;
		pos = cPos;
		try {
			model = new Model(modelFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		rotation = new Vector3f(0f, 0f, 0f);
	}

	public void update(long delta){
        boolean keyRight = Keyboard.isKeyDown(Keyboard.KEY_D);
        boolean keyLeft = Keyboard.isKeyDown(Keyboard.KEY_A);
        boolean keyFlyUp = Keyboard.isKeyDown(Keyboard.KEY_W);
        boolean keyFlyDown = Keyboard.isKeyDown(Keyboard.KEY_S);

        float speed = moveSpeed*delta;

        if (keyFlyUp) {
            pos.y += speed;
            rotation.x += 2f;
        }
        if (keyFlyDown) {
            pos.y -= speed;
            rotation.x -= 2f;
        }
        if (keyLeft) {
            pos.x -= speed;
            rotation.z += 2f;
        }
        if (keyRight) {
            pos.x += speed;
            rotation.z -= 2f;
        }
        pos.x = Math.max(-5, Math.min(5, pos.x));
        pos.y = Math.max(-6, Math.min(4, pos.y));
        rotation.x = Math.max(-20, Math.min(20, rotation.x));
        rotation.z = Math.max(-20, Math.min(20, rotation.z));
        if(!keyLeft && !keyRight && rotation.z != 0f){
        	if(rotation.z > 0f)
        		rotation.z -= 1f;
        	else 
				rotation.z += 1f;
        }
        if(!keyFlyUp && !keyFlyDown && rotation.x != 0f){
        	if(rotation.x > 0f)
        		rotation.x -= 1f;
        	else 
				rotation.x += 1f;
        }      
	}
	
	public void loadPlayer(){
		model.loadModel();
	}
	
	public void render(long currTime){
		glPushMatrix();
			glDisable(GL_TEXTURE);
			glDisable(GL_TEXTURE_2D);
			glLoadIdentity();
			Camera.apply();
			glTranslatef(pos.x, pos.y, pos.z);
			glRotatef(rotation.x, 1f, 0f, 0f);
			glRotatef(rotation.z, 0f, 0f, 1f);
			glRotatef(-90, 0f, 1f, 0f);
			glScalef(0.05f, 0.05f, 0.05f);
			if(hit)
				model.render(new Vector3f(220f/255f, 0.2f, 0.2f));
			else
				model.render(new Vector3f(150f/255f, 150f/255f, 50f/255f));
		glPopMatrix();
		
		if(hit && currTime > timeHit+2000)
			hit = false;
	}
	
	public Vector3f getPos(){
		return pos;
	}
}
