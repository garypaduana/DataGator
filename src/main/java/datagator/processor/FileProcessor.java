package datagator.processor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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
}
