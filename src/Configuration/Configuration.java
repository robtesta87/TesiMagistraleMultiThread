package Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {
	static private Configuration _instance = null;
	static public String classificatore = null;
	static public String articlesPath = null;
	static public String analysisPath = null;
	static public String version = null;
	static public String quantitativeanalysisPath = null;
	static public String articleBasePath = null;
	static public String articleIntermediaPath = null;
	static public String articleCompletaPath = null;
	static public String mentionBasePath = null;
	static public String mentionIntermediaPath = null;
	static public String mentionCompletaPath = null;
	static public String dumpWikiPath = null;
	
	public  Configuration(){
		try{
			//FileInputStream file =new FileInputStream( "util/config.properties" );
			InputStream file = new FileInputStream(new File("util/config.properties")) ;

			Properties props = new Properties();
			props.load(file);
			classificatore = props.getProperty("classificatore").toString();
			articlesPath = props.getProperty("articlesPath").toString();
			analysisPath = props.getProperty("analysisPath").toString();
			version = props.getProperty("version").toString();
			quantitativeanalysisPath =  props.getProperty("quantitativeanalysisPath").toString();
			articleBasePath =  props.getProperty("articleBasePath").toString();
			articleIntermediaPath =  props.getProperty("articleIntermediaPath").toString();
			articleCompletaPath =  props.getProperty("articleCompletaPath").toString();
			mentionBasePath =  props.getProperty("mentionBasePath").toString();
			mentionIntermediaPath =  props.getProperty("mentionIntermediaPath").toString();
			mentionCompletaPath =  props.getProperty("mentionCompletaPath").toString();
			dumpWikiPath =  props.getProperty("dumpWikiPath").toString();
			
		} 
		catch(Exception e){
			System.out.println("error" + e);
		}	 
	}

	static public synchronized Configuration instance(){
		if (_instance == null) {
			_instance = new Configuration();
		}
		return _instance;
	}
}
