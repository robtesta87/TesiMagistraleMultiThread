package CutterText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Configuration.Configuration;

public class CutterText {

	final static String infoboxRegex = "\\{\\{[Infobox]+[\\w+\\s\\W]*\n\\}\\}";
	final static String regex = "\n\\}\\}";
	final static String startRefRegex = "<ref";
	final static String endRefRegex = "</ref>";
	final static String endRefRegex2 = "/>";
	final static String boldRegex = "'''[\\w+\\s]*'''";



	
	public static String CutInfobox (String text){

		Pattern pattern = Pattern.compile(infoboxRegex);
		Matcher matcher = pattern.matcher(text);
		int startInfobox = 0;
		int endInfobox = 0;
		while(matcher.find()){
			String mentionString = matcher.group();
			System.out.println("infobox: "+mentionString);
			startInfobox = matcher.start();
			endInfobox = matcher.end();
			System.out.println("start: "+matcher.start()+" end: "+matcher.end());


			System.out.println(text);
		}

		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(text);
		int endGraffe = 0;
		boolean graffe =false;
		while(matcher.find()&&(graffe==false)){
			String mentionString = matcher.group();
			System.out.println(mentionString);
			endGraffe = matcher.end();
			System.out.println("start2: "+matcher.start()+" end: "+matcher.end());
			graffe=true;
		}
		if ((endInfobox>=endGraffe)&&(startInfobox<endGraffe)){
			System.out.println("start info:"+startInfobox+" end info: "+endGraffe);
			String partText1=text.substring(0, startInfobox);
			String partText2=text.substring(endGraffe, text.length());
			text = partText1+" "+partText2;
		}
		System.out.println("TESTO TAGLIATO: "+text);
		return text;
	}

	public static String cutRef (String text){
		int startRef = 0;
		int endRef1 = 0;
		int endRef2 = 0;
		boolean exit = false;
		while ((startRef!=-1)&&((endRef1!=Integer.MAX_VALUE)||(endRef2!=Integer.MAX_VALUE))&&(exit==false)){
			startRef = text.indexOf(startRefRegex);
			endRef1 = text.indexOf(endRefRegex);
			endRef2 = text.indexOf(endRefRegex2);

			if (endRef1==-1)
				endRef1=Integer.MAX_VALUE;
			if (endRef2==-1)
				endRef2=Integer.MAX_VALUE;
			if (startRef!=-1){

				if (endRef2<startRef)
					endRef2=text.indexOf(endRefRegex2, startRef);
				//System.out.println("start: "+startRef+" end1: "+endRef1+" end2: "+endRef2);
				if (endRef2<startRef){
					endRef2=text.indexOf(endRefRegex2, startRef);
					if (endRef2==-1)
						endRef2=Integer.MAX_VALUE;
				}
				if (endRef1<startRef){
					endRef1=text.indexOf(endRefRegex, startRef);
					if (endRef1==-1)
						endRef1=Integer.MAX_VALUE;
				}
				if (endRef1>endRef2){
					//if (endRef2>startRef){
					String partText1=text.substring(0, startRef);
					String partText2=text.substring(endRef2+2, text.length());
					//System.out.println("cancello ref1: "+text.substring(startRef,endRef2+2));
					text = partText1+" "+partText2;
					//System.out.println(text);
					//}
				}
				else{
					if (endRef1!=endRef2){

						String partText1=text.substring(0, startRef);
						String partText2=text.substring(endRef1+6, text.length());
						//System.out.println("cancello ref2: "+text.substring(startRef,endRef1+6));
						text = partText1+" "+partText2;
						//System.out.println(text);

					}
					else 
						exit=true;
				}
			}	

		}

		return text;
	}
	public static String cutText (String text){

		//togli la parte iniziale dell'articolo

		Pattern pattern = Pattern.compile(boldRegex);
		Matcher matcher = pattern.matcher(text);
		boolean trovato=false;
		int i=0;
		while(matcher.find()&&(trovato==false)){
			//System.out.println("bold trovato per tagliare: "+matcher.group());
			i= matcher.start();
			trovato=true;
		}	

		text = text.substring(i);

		//togli la parte finale dell'articolo
		String[] textSpitted = text.split("==See also==");
		if (textSpitted.length>1){
			text=textSpitted[0];
		}
		textSpitted = text.split("== See also ==");
		if (textSpitted.length>1){
			text=textSpitted[0];
		}
		textSpitted = text.split("==References==");
		if (textSpitted.length>1){
			text=textSpitted[0];
		}
		textSpitted = text.split("== References ==");
		if (textSpitted.length>1){
			text=textSpitted[0];
		}

		textSpitted = text.split("==Related pages==");
		if (textSpitted.length>1){
			text=textSpitted[0];
		}

		textSpitted = text.split("== Related pages ==");
		if (textSpitted.length>1){
			text=textSpitted[0];
		}
		textSpitted = text.split("== Notes ==");
		if (textSpitted.length>1){
			text=textSpitted[0];
		}
		textSpitted = text.split("==Notes==");
		if (textSpitted.length>1){
			text=textSpitted[0];
		}
		return text;

	}
	

}
