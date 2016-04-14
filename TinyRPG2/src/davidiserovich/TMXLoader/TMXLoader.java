package davidiserovich.TMXLoader;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.site40.rodit.tinyrpg.game.render.ResourceManager;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

/**
 * Loader for .tmx XML map file
 */
public class TMXLoader {

	/**
	 * Create a bitmap based on the data in the TileMapData structure.
	 * 
	 * @param	t 	data structure describing the TileMap
	 * @param	c 	application context
	 * @param	startLayer	index of the first layer to render
	 * @param	endLayer	index of the last layer to render
	 * @return		bitmap of the map
	 */
	public static Bitmap[] createBitmap(TileMapData t, ResourceManager resources, int startLayer, int endLayer){

		try{
			// Create a bitmap of the size of the map.
			// Straight up creating a bitmap of arbitrary size is huge in memory, but this is
			// sufficient for small demo purposes.

			// In a production engine, map size should either be restricted,
			// or the map should be loaded to memory on the fly.
			Log.i("TMXLoader", "Building bitmap from TMX. { width=" + (t.width * t.tilewidth) + " height=" + (t.height * t.tileheight) + " }");
			Bitmap mapImage = Bitmap.createBitmap(t.width * t.tilewidth, t.height * t.tileheight, Bitmap.Config.RGB_565);
			// Load all tilesets that are used into memory. Again, not
			// very efficient, but loading the image, dereferencing, and running
			// the gc for each image is not a fast or good option.
			// Still, a better way is forthcoming if I can think of one.
			Bitmap[] tilesets = new Bitmap[t.tilesets.size()];

			for (int i = 0; i < tilesets.length; i++){
				tilesets[i] = resources.readBitmap("map/" + t.tilesets.get(i).ImageFilename);
			}

			// Create a Canvas reference to our map Bitmap
			// so that we can blit to it.

			Canvas mapCanvas = new Canvas(mapImage);
			Bitmap rotImage = Bitmap.createBitmap(mapImage.getWidth(), mapImage.getHeight(), Bitmap.Config.ARGB_4444);
			Canvas rotCanvas = new Canvas(rotImage);


			// Loop through all layers and x and y-positions
			// to render all the tiles.

			// Later I'll add in an option for specifying which layers
			// to display, in case some hold invisible or meta-tiles

			long currentGID;
			Long localGID;
			Integer currentTileSetIndex;
			Rect source = new Rect(0, 0, 0, 0);
			Rect dest = new Rect(0, 0, 0, 0);

			for (int i = startLayer; i < endLayer; i++){

				for(int j = 0; j < t.layers.get(i).height; j++){

					for (int k = 0; k < t.layers.get(i).width; k++){

						boolean rot = t.layers.get(i).name.equals("RenderOnTop");

						currentGID = t.getGIDAt(k, j, i);
						localGID = t.getLocalID(currentGID);
						// debug
						//if (localGID == null) Log.d("GID", "Read problem");
						//Log.d("Tilegid", String.valueOf(localGID));
						currentTileSetIndex  = t.getTileSetIndex(currentGID);

						// The row number is the number of tiles wide the image is divided by
						// the tile number

						// Check that this space isn't buggy or undefined, and 
						// if everything's fine, blit to the current x, y position
						if (localGID != null){
							source.top = (((localGID).intValue())/((t.tilesets.get(currentTileSetIndex).imageWidth)/t.tilewidth))*t.tileheight; 
							source.left = (((localGID).intValue())%((t.tilesets.get(currentTileSetIndex).imageWidth)/t.tilewidth))*t.tilewidth;
							source.bottom = source.top + t.tileheight;
							source.right = source.left + t.tilewidth;


							dest.top = j*t.tileheight;
							dest.left = k*t.tilewidth;
							dest.bottom = dest.top + t.tileheight;
							dest.right = dest.left + t.tilewidth;

							if(rot)
								rotCanvas.drawBitmap(tilesets[currentTileSetIndex], source, dest, new Paint());
							else
								mapCanvas.drawBitmap(tilesets[currentTileSetIndex], source, dest, new Paint());
						}

					}

				}    			    			

			}

			for (int i = 0; i < tilesets.length; i++){
				if(tilesets[i] != null){
					tilesets[i].recycle();
					tilesets[i] = null;
				}
			}
			tilesets = null;
			
			mapCanvas = rotCanvas = null;
			
			System.gc();

			return new Bitmap[] { mapImage, rotImage };

		}catch(Exception e){
			Log.d("Exception", e.toString());
			e.printStackTrace();
		}
		
		System.exit(2);
		return null;
	}


	/**
	 * Reads XML file from assets and returns a TileMapData structure describing its contents
	 * 
	 * @param	filename	path to the file in assets
	 * @param	c			context of the application to resolve assets folder
	 * @return				data structure containing map data
	 */
	public static TileMapData readTMX(String filename, ResourceManager resources){
		TileMapData t = null;

		// Initialize SAX
		try{

			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser parser = spf.newSAXParser();
			XMLReader reader = parser.getXMLReader();

			// Create an instance of the TMX XML handler
			// that will create a TileMapData object
			TMXHandler handler = new TMXHandler();

			reader.setContentHandler(handler);
			InputStream in = resources.openAsset(filename);
			InputSource source = new InputSource(in);
			reader.parse(source);

			// Extract the created object
			t = handler.getData();


		} catch(ParserConfigurationException pce) { 
			Log.e("SAX XML", "sax parse error", pce); 
		} catch(SAXException se) { 
			Log.e("SAX XML", "sax error", se); 
		} catch(IOException ioe) { 
			Log.e("SAX XML", "sax parse io error", ioe); 
		}finally{

		}
		
		return t;
	}
}
