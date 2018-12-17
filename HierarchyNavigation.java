import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;

import com.leapmotion.leap.*;
import com.leapmotion.leap.Gesture.Type;

class LeapListener extends Listener{
	
	public Robot robot;
		
	public void onConnect(Controller controller) {
		System.out.println("Leap Motion Connected");
		
		controller.enableGesture(Gesture.Type.TYPE_KEY_TAP);
		controller.enableGesture(Gesture.Type.TYPE_CIRCLE);
		controller.enableGesture(Gesture.Type.TYPE_SCREEN_TAP);
	}
		
	public void onFrame(Controller controller) {
		try {
			robot = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		Frame frame = controller.frame();
		InteractionBox box = frame.interactionBox();
		
		for(Finger finger:frame.fingers()) {
			if(finger.type() == Finger.Type.TYPE_INDEX) {
				Vector fingerPos = finger.stabilizedTipPosition();
				Vector boxFingerPos = box.normalizePoint(fingerPos);
				Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
				
				int mouseX = (int)(screen.width * boxFingerPos.getX());
				int mouseY = (int)(screen.height - boxFingerPos.getY()*screen.height);
				robot.mouseMove(mouseX, mouseY);
			}
		}
		
		for(Gesture gesture:frame.gestures()) {
			if(gesture.type() == Type.TYPE_CIRCLE) {
				CircleGesture circleGesture = new CircleGesture(gesture);
				
				if( circleGesture.pointable().stabilizedTipPosition().angleTo(circleGesture.normal()) <= Math.PI / 2) {
					robot.mouseWheel(1);
				}
				else{
					robot.mouseWheel(-1);
				}
				
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			else if(gesture.type() == Type.TYPE_SCREEN_TAP) {				
				robot.mousePress(InputEvent.BUTTON1_MASK);
				robot.mouseRelease(InputEvent.BUTTON1_MASK);
				
				robot.mousePress(InputEvent.BUTTON1_MASK);
				robot.mouseRelease(InputEvent.BUTTON1_MASK);
					
			}
			else if(gesture.type() == Type.TYPE_KEY_TAP) {
				
				robot.keyPress(KeyEvent.VK_BACK_SPACE );
				robot.keyRelease(KeyEvent.VK_BACK_SPACE );				
			}
		}
	}
}

public class HierarchyNavigation {
	
	public static void main(String[] args) {
		LeapListener listener = new LeapListener();
		Controller controller = new Controller();
		
		controller.setPolicy(Controller.PolicyFlag.POLICY_BACKGROUND_FRAMES);
		controller.setPolicy(Controller.PolicyFlag.POLICY_IMAGES);

		controller.addListener(listener);
		
		try{
			System.in.read();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		controller.removeListener(listener);
		
	}
}
