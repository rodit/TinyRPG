package net.site40.rodit.tinyrpg.game.gui2;

import java.lang.reflect.Method;
import java.util.HashMap;

import net.site40.rodit.tinyrpg.game.Game;
import net.site40.rodit.tinyrpg.game.Values;
import net.site40.rodit.tinyrpg.game.gui2.IComponentListener.ComponentListener;
import net.site40.rodit.util.Util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.graphics.Color;
import android.graphics.Paint.Align;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class LinkedGui extends Gui{

	private static HashMap<String, LinkedGui> linkCache = new HashMap<String, LinkedGui>();

	public static class LinkedComponentListener extends ComponentListener{

		protected HashMap<String, Method> linkedMethods;

		public LinkedComponentListener(){
			this.linkedMethods = new HashMap<String, Method>();
		}

		public void link(String action, String method, LinkedGui gui){
			try{
				Class<? extends LinkedGui> cls = gui.getClass();
				Method m = null;
				if(action.startsWith("TOUCH"))
					m = cls.getDeclaredMethod(method, Game.class, MotionEvent.class, Component.class);
				else if(action.startsWith("KEY"))
					m = cls.getDeclaredMethod(method, Game.class, KeyEvent.class, Component.class);
				else
					return;
				linkedMethods.put(action, m);
			}catch(Exception e){
				Log.e("LinkedGui", "Error while linking method - " + e.getMessage());
			}
		}

		public boolean invoke(String eventId, Game game, Object event, Component component){
			try{
				Method m = linkedMethods.get(eventId);
				if(m != null){
					m.invoke(component.getParent(), game, event, component);
					return true;
				}
			}catch(Exception e){
				Log.e("LinkedGui", "Error while invoking linked method for event id " + eventId + " - " + e.getMessage());
			}
			return false;
		}

		public void touchDown(Game game, MotionEvent event, Component component){
			invoke("TOUCH_DOWN", game, event, component);
		}

		public void touchUp(Game game, MotionEvent event, Component component){
			invoke("TOUCH_UP", game, event, component);
		}

		public void keyDown(Game game, KeyEvent event, Component component){
			invoke("KEY_DOWN", game, event, component);			
		}

		public void keyUp(Game game, KeyEvent event, Component component){
			invoke("KEY_UP", game, event, component);			
		}
	}

	protected HashMap<Component, LinkedComponentListener> linkedListeners;
	
	public void postInit(){
		
	}

	public static LinkedGui load(Game game, String resource){
		LinkedGui g = linkCache.get(resource);
		if(g == null){
			Document document = game.getResources().readDocument(resource);
			if(document == null)
				Log.e("LinkedGui", "Failed to load linked gui xml file @ " + resource + ".");

			Element guiElement = (Element)document.getElementsByTagName("gui").item(0);
			String linkedClass = guiElement.getAttribute("link");

			LinkedGui gui = null;
			try{
				Class<?> cls = Class.forName(linkedClass);
				gui = (LinkedGui)cls.newInstance();
				gui.setName(guiElement.getAttribute("name"));
				gui.setBackground(guiElement.getAttribute("background"));
			}catch(Exception e){
				Log.e("LinkedGui", "Error while linking gui - " + e.getMessage());
				return null;
			}

			gui.linkedListeners = new HashMap<Component, LinkedComponentListener>();

			NodeList compNodes = document.getElementsByTagName("component");
			for(int i = 0; i < compNodes.getLength(); i++){
				Node node = compNodes.item(i);
				if(node.getNodeType() != Node.ELEMENT_NODE)
					continue;
				Element element = (Element)node;

				Component c = new Component();
				LinkedComponentListener listener = new LinkedComponentListener();
				gui.linkedListeners.put(c, listener);
				c.attach(listener);

				c.setName(element.getAttribute("name"));
				c.setText(element.getAttribute("text"));
				c.setX(Util.tryGetInt(element.getAttribute("x"), 0));
				c.setY(Util.tryGetInt(element.getAttribute("y"), 0));
				c.setWidth(Util.tryGetInt(element.getAttribute("width"), 0));
				c.setHeight(Util.tryGetInt(element.getAttribute("height"), 0));
				c.setActive(Util.tryGetBool(element.getAttribute("active"), true));
				c.setVisible(Util.tryGetBool(element.getAttribute("visible"), true));
				c.getPaint().setColor(Util.tryGetColor(element.getAttribute("color"), Color.BLACK));
				c.getPaint().setTextAlign(Util.tryGetAlign(element.getAttribute("align"), Align.CENTER));
				c.getPaint().setTextSize(Util.tryGetTextSize(element.getAttribute("textSize"), Values.FONT_SIZE_MEDIUM));
				c.getPaint().setAlpha(Util.tryGetInt(element.getAttribute("alpha"), c.getPaint().getAlpha()));
				c.flag = Util.tryGetInt(element.getAttribute("flag"), 0);
				c.setTag(element.getAttribute("tag"));

				NodeList stateNodes = element.getElementsByTagName("state");
				for(int j = 0; j < stateNodes.getLength(); j++){
					Node stateNode = stateNodes.item(j);
					if(stateNode.getNodeType() != Node.ELEMENT_NODE)
						continue;
					Element stateElement = (Element)stateNode;
					String id = stateElement.getAttribute("id");
					String background = stateElement.getAttribute("background");
					c.setBackground(Util.tryGetInt(id, 0), background);
				}

				NodeList eventNodes = element.getElementsByTagName("event");
				for(int j = 0; j < eventNodes.getLength(); j++){
					Node eventNode = eventNodes.item(j);
					if(eventNode.getNodeType() != Node.ELEMENT_NODE)
						continue;
					Element eventElement = (Element)eventNode;
					String id = eventElement.getAttribute("id");
					String method = eventElement.getAttribute("method");
					listener.link(id, method, gui);
				}

				gui.add(c);
			}
			linkCache.put(resource, g = gui);
			g.postInit();
		}
		return g;
	}
}
