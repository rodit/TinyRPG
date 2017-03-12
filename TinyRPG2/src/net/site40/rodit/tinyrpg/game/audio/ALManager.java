package net.site40.rodit.tinyrpg.game.audio;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import net.site40.rodit.rlib.util.AsyncCallback;
import net.site40.rodit.tinyrpg.game.render.ResourceManager;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xiph.vorbis.decoder.DecodeFeed;

import paulscode.android.sound.ALAN;
import android.util.Log;

public class ALManager extends SoundEffectManager{

	public static final int BUFFER_COUNT = 512;
	public static final int MAX_SOURCE_COUNT = 16;

	private int[] buffers;
	private int cBuffWriteIndex = 0;
	private int[] sources;

	private ResourceManager resources;
	private HashMap<String, SoundEffectGroup> groups;

	public ALManager(ResourceManager resources){
		super();
		this.resources = resources;
		this.groups = new HashMap<String, SoundEffectGroup>();

		if(!ALAN.create())
			throw new RuntimeException("OpenAL failed to initialize.");
		checkALError("Creating OpenAL context.");
		this.buffers = new int[BUFFER_COUNT];
		ALAN.alGenBuffers(BUFFER_COUNT, buffers);
		checkALError("Allocating buffers.");
		this.sources = new int[MAX_SOURCE_COUNT];
		ALAN.alGenSources(MAX_SOURCE_COUNT, sources);
		checkALError("Allocating sources.");
	}

	public int getNextBuffer(){
		return buffers[cBuffWriteIndex++];
	}

	private int[] sourceState = new int[1];
	public int getNextSource(){
		int cSource = 0;
		for(int i = 0; i < MAX_SOURCE_COUNT; i++){
			cSource = sources[i];
			ALAN.alGetSourcei(cSource, ALAN.AL_SOURCE_STATE, sourceState);
			if(sourceState[0] == ALAN.AL_STOPPED || sourceState[0] == ALAN.AL_INITIAL)
				return cSource;
		}
		return -1;
	}

	private int lastError = 0;
	private void checkALError(String message){
		if((lastError = ALAN.alGetError()) != ALAN.AL_NO_ERROR)
			throw new RuntimeException(message + " - OpenAL error " + Integer.toHexString(lastError));
	}

	public void buildCache(){
		this.groups = new HashMap<String, SoundEffectGroup>();

		Document document = resources.readDocument("sound/sounds.xml");
		NodeList groups = document.getElementsByTagName("group");
		int totalSounds = 0;
		for(int i = 0; i < groups.getLength(); i++){
			Node n = groups.item(i);
			if(n.getNodeType() != Node.ELEMENT_NODE)
				continue;
			Element e = (Element)n;
			String name = e.getAttribute("name");
			String base = e.getAttribute("base");
			SoundEffectGroup group = new SoundEffectGroup(name, base);
			String groupLoaded = resources.readString(base + "group.txt");
			for(String file : groupLoaded.split(";")){
				file = file.replace(";", "").trim();
				group.putSound(file, loadSync(base + file));
				Log.d("SoundEffectManager", "Loaded sound " + file + " from group " + name + " with base " + base + ".");
				totalSounds++;
			}
			this.groups.put(name, group);
		}
		Log.d("SoundEffectManager", "Loaded " + totalSounds + " sound effects from " + groups.getLength() + " groups.");
	}

	private volatile boolean loadHolder = false;
	private volatile int decodeBuffer = -1;
	private int loadSync(final String file){
		load(file, new AsyncCallback(){
			@Override
			public void callback(Object[] args){
				int status = (Integer)args[0];
				if(status == DecodeFeed.SUCCESS)
					decodeBuffer = (Integer)args[1];
				else
					throw new RuntimeException("Failed to read PCM data from OGG file: " + file + ".");
				loadHolder = true;
			}
		});
		while(!loadHolder){
			try{
				Thread.sleep(10L);
			}catch(InterruptedException e){}
		}
		loadHolder = false;
		return decodeBuffer;
	}

	private void load(String file, final AsyncCallback onLoaded){
		InputStream in = resources.openAsset(file);
		if(in != null){
			final int nextBuffer = getNextBuffer();
			OGGUtil.readPCM(in, nextBuffer, new AsyncCallback(){
				@Override
				public void callback(Object[] args){
					int status = (Integer)args[0];
					if(status == DecodeFeed.SUCCESS)
						onLoaded.callback(new Object[] { status, Integer.valueOf(nextBuffer) });
					else
						onLoaded.callback(args);
				}
			});
			try{
				in.close();
			}catch(IOException e){}
		}
	}

	protected int play(int bufferId, boolean repeatUnused){
		int source = getNextSource();
		if(source == -1){
			Log.w("ALManager", "No sources available to play sound.");
			return source;
		}
		ALAN.alSourcei(source, ALAN.AL_BUFFER, bufferId);
		ALAN.alSourcePlay(source);
		return source;
	}

	public int play(String group, String sound){
		return play(group, sound, false);
	}

	public int play(String group, String sound, boolean repeat){
		SoundEffectGroup segroup = groups.get(group);
		if(segroup == null){
			Log.e("SoundEffects", "Sound effect group not found: " + group + ".");
			return -1;
		}
		return play(segroup.getId(sound), repeat);
	}

	public int playRandom(String group){
		return playRandom(group, false);
	}

	public int playRandom(String group, boolean repeat){
		SoundEffectGroup segroup = groups.get(group);
		if(segroup == null){
			Log.e("SoundEffects", "Sound effect group not found: " + group + ".");
			return -1;
		}
		return play(segroup.getRandom(), repeat);
	}

	public boolean hasGroup(String name){
		return groups.containsKey(name);
	}

	public SoundEffectGroup getGroup(String name){
		return groups.get(name);
	}

	public void stop(int id){
		ALAN.alSourceStop(id);
	}

	public void dispose(){
		ALAN.destroy();
		buffers = null;
		sources = null;
		groups.clear();
	}
}
