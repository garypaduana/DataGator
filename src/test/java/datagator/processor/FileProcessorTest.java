package datagator.processor;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:integration.xml"})
public class FileProcessorTest {

	@Test
	public void test(){
		assertTrue(true);
	}
	
	//@Test
	public void testScrape() throws IOException, URISyntaxException {
		FileProcessor fp = new FileProcessor();
		File f = new File("C:/users/gary/desktop/page.txt");
		fp.scrapeUrl(f);
	}

}
