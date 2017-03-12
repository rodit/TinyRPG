package net.site40.rodit.tinyrpg.game.audio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import net.site40.rodit.rlib.util.AsyncCallback;

import org.xiph.vorbis.decoder.DecodeFeed;
import org.xiph.vorbis.decoder.DecodeStreamInfo;
import org.xiph.vorbis.decoder.VorbisDecoder;

import paulscode.android.sound.ALAN;
import android.util.Log;

public class OGGUtil{

	private static int lastStatus;

	private static int cSampleRate;
	private static int cChannels;
	private static int cFormat;

	private static int cCount;

	private static ByteArrayOutputStream allData;

	public static void readPCM(final InputStream in, final int alBufferId, final AsyncCallback callback){
		lastStatus = VorbisDecoder.startDecoding(new DecodeFeed(){
			@Override
			public int readVorbisData(byte[] buffer, int amountToWrite){
				try{
					int read = in.read(buffer, 0, amountToWrite);
					return read == -1 ? 0 : read;
				}catch(IOException e){}
				return 0;
			}

			@Override
			public void writePCMData(short[] pcmData, int amountRead){
				byte[] byteData = new byte[amountRead * 2];
				ByteBuffer.wrap(byteData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(pcmData, 0, amountRead);
				try{
					allData.write(byteData);
				}catch(IOException e){}
				cCount += amountRead;
			}

			@Override
			public void stop(){
				ALAN.alBufferData(alBufferId, cFormat, allData.toByteArray(), allData.size(), cSampleRate);
				try{
					allData.close();
				}catch(IOException e){
					e.printStackTrace();
				}
				allData = null;
				Log.d("OGGUtil", "Decoded " + cCount + " shorts.");
				cCount = 0;
				callback.callback(new Object[] { Integer.valueOf(lastStatus) });
			}
			
			@Override
			public void startReadingHeader(){}

			@Override
			public void start(DecodeStreamInfo decodeStreamInfo){
				allData = new ByteArrayOutputStream();
				cSampleRate = (int)decodeStreamInfo.getSampleRate();
				cChannels = (int)decodeStreamInfo.getChannels();
				cFormat = cChannels == 2 ? ALAN.AL_FORMAT_STEREO16 : ALAN.AL_FORMAT_MONO16;
			}
		});
	}
}
