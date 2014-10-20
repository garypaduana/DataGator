package datagator.processor;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class FileProcessor {

	private String encodeDir;
	private String youtubeDownloadDir;
	private String youtubeMp3Dir;
	
	// Encoding properties
	private String completed;
	private String encodeExtension;
	private String handbrakeCli;
	private String encodingPreset;
	private String regex;
	
	// MP3
	private String mp3Quality;
	
	private String sep = System.getProperty("file.separator");
	private String rotateParent;
	
	private String scrapeRegex;
	
	private static final Logger log = Logger.getLogger(FileProcessor.class);
	/**
	 * Encode a file using HandBrakeCLI and a configurable preset encoding profile.
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public File encode(File file) throws IOException, InterruptedException{
		
		String toPath = completed + sep +
				file.getName().substring(0, file.getName().lastIndexOf(".")) +
				encodeExtension;
		
		new File(completed).mkdirs();
				
		List<String> command = new ArrayList<String>();
		command.add("HandBrakeCLI");
		command.add("-i");
		command.add(file.getAbsolutePath());
		command.add("-o");
		command.add(toPath);
		command.add(encodingPreset);
	    executeCommand(command);
	    return file;
	}
	
	/**
	 * Downloads a YouTube video given the contents of a .url bookmark file.
	 * 
	 * @param contents
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void youtubeDownload(String contents) throws IOException, InterruptedException{
		String url = contents.split("URL=")[1];		
		List<String> command = new ArrayList<String>();
		
		command.clear();
		command.add("youtube-dl");
		command.add(url);
		command.add("-o");
		command.add(youtubeDownloadDir + sep + "%(title)s.%(ext)s");
		executeCommand(command);
	}
	
	/**
	 * Downloads a YouTube video to mp3 given the contents of a .url bookmark file.
	 * 
	 * @param contents
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void youtubeMp3(String contents) throws IOException, InterruptedException{
		String url = contents.split("URL=")[1];		
		List<String> command = new ArrayList<String>();
		
		command.clear();
		command.add("youtube-dl");
		command.add(url);
		command.add("-x");
		command.add("--audio-format");
		command.add("mp3");
		command.add("--audio-quality");
		command.add("2");
		command.add("-o");
		command.add(youtubeMp3Dir + sep + "%(title)s.%(ext)s");
		executeCommand(command);
	}
	
	/**
	 * Reencodes an mp3 to a lower variable bitrate.
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public File encodeMp3(File file) throws IOException, InterruptedException{
		List<String> command = new ArrayList<String>();
		new File(file.getParent() + "/output/").mkdirs();
		
		command.clear();
		command.add("lame");
		command.add("-h");
		command.add(mp3Quality);
		command.add(file.getAbsolutePath());
		command.add(file.getParent() + "/output/" + file.getName());
		
		executeCommand(command);
		return file;
	}
	
	/**
	 * Rotates a movie file 90 degrees clockwise using ffmpeg.
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public File rotateMovie(File file) throws IOException, InterruptedException{
		List<String> command = new ArrayList<String>();
		new File(rotateParent + "/output/").mkdirs();
		
		command.clear();
		command.add("ffmpeg");
		command.add("-i");
		command.add(file.getAbsolutePath());
		command.add("-vf");
		command.add("transpose=1");
		command.add("-vcodec");
		command.add("libx264");
		command.add(rotateParent + "/output/" + file.getName());
		
		executeCommand(command);
		return file;
	}
	
	/**
	 * The input file is a bookmark.  It should contain "URL=" followed by an 
	 * Internet address pointing to an html file.  The contents of the file are
	 * read and parsed looking for links to various media files.  Each link
	 * is then downloaded to the current working directory.
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public File scrapeUrl(File file) throws IOException, URISyntaxException{
		String contents = readFile(file);
		String url = contents.split("URL=")[1];
		Matcher httpRootMatcher = Pattern.compile("(http(s?)://.+?)/.+").matcher(url);
		String httpRoot = null;
		if(httpRootMatcher.find()){
			httpRoot = httpRootMatcher.group(1);
		}
		
		String pageSource = getUrlContents(url);
		
		Matcher matcher = Pattern.compile(scrapeRegex).matcher(pageSource);
		Set<String> links = new HashSet<String>();
		while(matcher.find()){
			links.add(matcher.group(1));
		}
		
		for(String link : links){
			log.debug("Downloading... " + link);
			
			File destination = new File(file.getParent(), link.substring(link.lastIndexOf("/") + 1, link.length()));
			downloadFile(link, destination, httpRoot);
		}
		
		return file;
	}
	
	/**
	 * Downloads the contents of a url to the specified destination.
	 * 
	 * @param url
	 * @param destination
	 * @throws IOException
	 */
	public void downloadFile(String url, File destination, String httpRoot) throws IOException{
		FileOutputStream fos = null;
		URL site = null;
		
		try{
			try{
				site = new URL(url);
			}
			catch(MalformedURLException ex){
				site = new URL(httpRoot + url);
			}
			ReadableByteChannel rbc = Channels.newChannel(site.openStream());
			fos = new FileOutputStream(destination);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		}
		finally{
			if(fos != null){
				fos.close();
			}
		}
	}
	
	/**
	 * Reads the contents of a File.
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public String readFile(File file) throws IOException, URISyntaxException{
		StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		String line;
		try{
			br = new BufferedReader(new FileReader(file));
			while((line = br.readLine()) != null){
				sb.append(line);
			}
		}
		finally{
			if(br != null){
				br.close();
			}
		}
		return sb.toString();
	}
	
	/**
	 * Reads the contents of a URL.
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public String getUrlContents(String url) throws IOException{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		URL site = new URL(url);
		ReadableByteChannel rbc = Channels.newChannel(site.openStream());
		WritableByteChannel out = Channels.newChannel(baos);
		
		copy(rbc, out);
		
		return new String(baos.toByteArray(), Charset.defaultCharset());
	}
	
	/**
	 * Read all available bytes from one channel and copy them to the other.
	 * 
	 * @param in
	 * @param out
	 * @throws IOException
	 */
	public static void copy(ReadableByteChannel in, WritableByteChannel out) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocateDirect(32 * 1024);
		while (in.read(buffer) != -1) {
			buffer.flip();
			out.write(buffer);
			buffer.compact();
		}
		buffer.flip();
		while (buffer.hasRemaining()) {
			out.write(buffer);
		}
	}
		
	/**
	 * Executes a system process and prints InputStream and ErrorStream to
	 * console as well as returning all output as a List<String>.
	 * 
	 * @param command
	 * @return List<String> of all collected output from InputStream and ErrorStream.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static List<String> executeCommand(List<String> command) throws 
		IOException, InterruptedException{
		
		ProcessBuilder builder = new ProcessBuilder(command);
		builder.redirectErrorStream(true);
	    builder.directory(new File(System.getenv("temp")));
	    List<String> results = new ArrayList<String>();
	    
	    final Process process = builder.start();
	    
	    try(BufferedReader bufferedReader = new BufferedReader(
	    		new InputStreamReader(process.getInputStream()))){
		    String line;
		    
		    while((line = bufferedReader.readLine()) != null){
			    System.out.println(line);
				results.add(line);
		    }
		    process.waitFor();
	    }
	    
	    return results;
	}
	
	public String getCompleted() {
		return completed;
	}

	public void setCompleted(String completed) {
		this.completed = completed;
	}

	public String getEncodeExtension() {
		return encodeExtension;
	}

	public void setEncodeExtension(String encodeExtension) {
		this.encodeExtension = encodeExtension;
	}

	public String getHandbrakeCli() {
		return handbrakeCli;
	}

	public void setHandbrakeCli(String handbrakeCli) {
		this.handbrakeCli = handbrakeCli;
	}

	public String getEncodingPreset() {
		return encodingPreset;
	}

	public void setEncodingPreset(String encodingPreset) {
		this.encodingPreset = encodingPreset;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public String getEncodeDir() {
		return encodeDir;
	}

	public void setEncodeDir(String encodeDir) {
		this.encodeDir = encodeDir;
	}

	public String getYoutubeDownloadDir() {
		return youtubeDownloadDir;
	}

	public void setYoutubeDownloadDir(String youtubeDownloadDir) {
		this.youtubeDownloadDir = youtubeDownloadDir;
	}

	public String getYoutubeMp3Dir() {
		return youtubeMp3Dir;
	}

	public void setYoutubeMp3Dir(String youtubeMp3Dir) {
		this.youtubeMp3Dir = youtubeMp3Dir;
	}

	public String getMp3Quality() {
		return mp3Quality;
	}

	public void setMp3Quality(String mp3Quality) {
		this.mp3Quality = mp3Quality;
	}

	public String getRotateParent() {
		return rotateParent;
	}

	public void setRotateParent(String rotateParent) {
		this.rotateParent = rotateParent;
	}
	
	public String getScrapeRegex() {
		return scrapeRegex;
	}

	public void setScrapeRegex(String scrapeRegex) {
		this.scrapeRegex = scrapeRegex;
	}
}
