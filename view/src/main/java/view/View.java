package view;

import java.awt.Graphics;
import java.awt.image.BufferStrategy;

import java.sql.SQLException;

import display.Display;
import graphics.Assets;
import graphics.GameCamera;
import input.KeyManager;

import states.GameState;
import states.MenuState;
import states.State;

/*---THIS CLASS WILL PUT SOMETHING ON THE CREATED FRAME AND MANAGE OUR PROJECT---*/

public class View implements Runnable {
	
    private Display display;
    public int width, height;
    public String title;
    private boolean running = false;
    private Thread thread;
    private BufferStrategy bs;
    private Graphics g;

    //States
    private State gameState;
    private State menuState;

    //Input
    private KeyManager keyManager;

    //Camera
    private GameCamera gameCamera;

    //Handler
    private Handler handler;

    public View(String title, int width, int height){
        this.width = width;
        this.height = height;
        this.title = title;
        
        keyManager = new KeyManager();
    }
    
    private void init() throws SQLException {
        display = new Display(title, width, height);
        display.getFrame().addKeyListener(keyManager);

        Assets.init();
        handler = new Handler(this);
        gameCamera = new GameCamera(handler, 0,0);
        
        gameState = new GameState(handler);
        menuState = new MenuState(handler);
        State.setState(gameState);
    }

    private void tick() {
        keyManager.tick();
        if(State.getState() != null)
            State.getState().tick();
    }

    private void render() {
        bs = display.getCanvas().getBufferStrategy();
        if(bs == null){
            display.getCanvas().createBufferStrategy(3);
            return;
        }
        g = bs.getDrawGraphics();
        //Clear Screen
        g.clearRect(0, 0, width, height);
        //Draw Here !
        if(State.getState() != null)
            State.getState().render(g);

        //End Drawing !
        bs.show();
        g.dispose();

    }
	
	@Override
	public void run() {

		try {
			init();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		int fps = 60;
		double timePerTick = 1000000000 / fps;
		double delta = 0;
		long now;
		long lastTime = System.nanoTime();
		long timer = 0;
		int ticks = 0;
		
		while(running){
			now = System.nanoTime();
			delta += (now - lastTime) / timePerTick;
			timer += now - lastTime;
			lastTime = now;
			
			if(delta >= 1){
				tick();
				render();
				ticks++;
				delta--;
			}
			
			if(timer >= 1000000000){
				System.out.println("Ticks and Frames " + ticks);
				ticks = 0;
				timer = 0;
			}
		}
		stop();
	}
	
	public synchronized void start() throws SQLException{
		if(running)
			return;
		
		running = true;
		thread = new Thread(this);
		thread.start();
	}
	
	public synchronized void stop(){
		if(!running)
			return;
		running = false;
		
			try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	

	/*/GETTERS AND SETTERS/*/
	public KeyManager getKeyManager(){
		return keyManager;
	}
	
	public GameCamera getGameCamera(){
		return gameCamera;
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public String getTitle() {
		return title;
	}
}