package configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import freebase.FreebaseSearcher;
import multithread.Version;

public class Configuration {

	String classificatore_path = null;
	String wikipediaDump_path = null;

	//String quantitativeanalysis_path = null;
	String analysis_folder = null;

	Version version = null;
	FreebaseSearcher freebase_searcher = null;

	/*
	static String articleBasePath = null;
	static String articleIntermediaPath = null;
	static String articleCompletaPath = null;

	static String mentionBasePath = null;
	static String mentionIntermediaPath = null;
	static String mentionCompletaPath = null;
	 */



	public Configuration(String configFilePath){
		Properties props = null;
		
		try {
			InputStream file = new FileInputStream(new File(configFilePath)) ;
			props = new Properties();
			props.load(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		classificatore_path = props.getProperty("classificatore").toString();
		wikipediaDump_path = props.getProperty("articles_path").toString();
		analysis_folder = props.getProperty("analysis_folder").toString();
		version = readVersion(props);
		freebase_searcher = createSearcher(props.getProperty("freebase_index").toString());

		//quantitativeanalysis_path =  props.getProperty("quantitativeanalysisPath").toString();
		/*
			articleBasePath =  props.getProperty("articleBasePath").toString();
			articleIntermediaPath =  props.getProperty("articleIntermediaPath").toString();
			articleCompletaPath =  props.getProperty("articleCompletaPath").toString();
			mentionBasePath =  props.getProperty("mentionBasePath").toString();
			mentionIntermediaPath =  props.getProperty("mentionIntermediaPath").toString();
			mentionCompletaPath =  props.getProperty("mentionCompletaPath").toString();
		 */

	}



	/**
	 * @return the version
	 */
	public Version getVersion() {
		return version;
	}



	/**
	 * @return the freebase_searcher
	 */
	public FreebaseSearcher getFreebase_searcher() {
		return freebase_searcher;
	}



	/**
	 * read the versione from the config file and creates version enum.
	 * @param props
	 * @return
	 */
	public Version readVersion(Properties props){
		String version_name = props.getProperty("version").toString();
		Version version2use = null;
		switch(version_name){
		case "Base":
			version2use = Version.Base;
			break;
		case "Intemedia":
			version2use = Version.Intermedia;
			break;
		case "Completa":
			version2use = Version.Completa;
			break;
		default:
			System.out.println("Versione errata nel config file.");
			break;
		}
		return version2use;
	}
	
	/**
	 * 
	 * @param indexPath
	 * @return
	 */
	public FreebaseSearcher createSearcher(String indexPath){
		FreebaseSearcher freebaseSearcher = null;
		try {
			freebaseSearcher = new FreebaseSearcher(indexPath);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Index path errato nel config file.");
		}
		return freebaseSearcher;
		
	}
	

	/**
	 * @return the classificatore_path
	 */
	public String getClassificatore_path() {
		return classificatore_path;
	}



	/**
	 * @param classificatore_path the classificatore_path to set
	 */
	public void setClassificatore_path(String classificatore_path) {
		this.classificatore_path = classificatore_path;
	}



	/**
	 * @return the analysis_folder
	 */
	public String getAnalysis_folder() {
		return analysis_folder;
	}



	/**
	 * @param analysis_folder the analysis_folder to set
	 */
	public void setAnalysis_folder(String analysis_folder) {
		this.analysis_folder = analysis_folder;
	}



	/**
	 * @param freebase_searcher the freebase_searcher to set
	 */
	public void setFreebase_searcher(FreebaseSearcher freebase_searcher) {
		this.freebase_searcher = freebase_searcher;
	}
	
	/**
	 * @return the wikipediaDump_path
	 */
	public String getWikipediaDump_path() {
		return wikipediaDump_path;
	}

}
