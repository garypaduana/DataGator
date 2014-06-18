package datagator.processor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
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
	
	// Youtube properties
	private String youtubedl;
	
	private String sep = System.getProperty("file.separator");
	
	/**
	 * Encode a file using HandBrakeCLI and a configurable preset encoding profile.
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public boolean encode(File file) throws IOException, InterruptedException{
		
		String toPath = completed + sep +
				file.getName().substring(0, file.getName().lastIndexOf(".")) +
				encodeExtension;
		
		new File(completed).mkdirs();
				
		List<String> command = new ArrayList<String>();
	    command.addAll(Arrays.asList(new String[]{
	    	handbrakeCli,
			"-i",
			file.getAbsolutePath(),
			"-o",
			toPath,
			encodingPreset}));
	    
	    executeCommand(command);
	    
	    return true;
	}
	
	/**
	 * Downloads a YouTube video given the contents of a .url bookmark file.
	 * 
	 * @param contents
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void youtubeDownload(String contents) throws IOException, InterruptedException{
		// TODO: check inputs to avoid ArrayIndexOutOfBoundsException
		String url = contents.split("URL=")[1];		
		List<String> command = new ArrayList<String>();
		
		command.clear();
		command.add(youtubedl);
		command.add(url);
		//command.add("--restrict-filenames");  // This prevents non-alphanumeric chars
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
		// TODO: check inputs to avoid ArrayIndexOutOfBoundsException
		String url = contents.split("URL=")[1];		
		List<String> command = new ArrayList<String>();
		
		command.clear();
		command.add(youtubedl);
		command.add(url);
		//command.add("--restrict-filenames");
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

	public String getYoutubedl() {
		return youtubedl;
	}

	public void setYoutubedl(String youtubedl) {
		this.youtubedl = youtubedl;
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
}
