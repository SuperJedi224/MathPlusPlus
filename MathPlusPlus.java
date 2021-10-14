import java.io.*;
import java.util.*;
import java.util.regex.*;
import javax.swing.JOptionPane;
import static java.lang.Math.*;

//Math++ 1.04 Reference Implementation

public class MathPlusPlus{
	static Map<Character,Double>vars=new HashMap<>();
	static Scanner in=new Scanner(System.in);
	static final double PHI=(1+sqrt(5))/2;
	static PrintStream log;
	static Map<Integer,Double>tape=new HashMap<>();
	static double expr(String e,int line){
		try{
		e=e.replaceAll("\\s","");
		log.println(e);
		if(e.equalsIgnoreCase("$pi"))return PI;
		if(e.equalsIgnoreCase("$phi"))return PHI;
		if(e.equalsIgnoreCase("$e"))return E;
		if(e.equalsIgnoreCase("$rand"))return Math.random();
		if(e.matches("^\\{(.+)\\}$")){
			e=e.replaceAll("^\\{(.+)\\}$","$1");
			return tape.getOrDefault((int)expr(e,line),0.0);
		}
		while(e.indexOf("(")!=-1){
			Matcher k=Pattern.compile("\\(([^()]+?)\\)").matcher(e);
			k.find();
			String g=k.group(1);
			log.println(g);
			double d=expr(g,line);
			e=e.replaceFirst("\\([^()]+?\\)",d<0?"(-"+d+")":""+d);
		}
		try{return Double.parseDouble(e);}catch(Exception x){}
		if(e.matches("[a-z]"))return vars.getOrDefault(e.charAt(0),0.0);
		if(e.equals("?"))return in.nextDouble();
		if(e.indexOf("|")!=-1){
			for(String a:e.split("\\|")){
				double i=expr(a,line);
				if(i!=0)return i;
			}
			return 0;
		}
		if(e.indexOf("&")!=-1){
			for(String a:e.split("&")){
				
				if(expr(a,line)==0)return 0;
			}
			return 1;
		}
		if(e.indexOf("+")!=-1){
			double i=0;
			for(String a:e.split("\\+")){
				i+=expr(a,line);
			}
			return i;
		}
		if(e.indexOf("-")>0){
			double i=0;
			String[]a=e.split("-");
			i=expr(a[0],line);
			for(int j=1;j<a.length;j++){
				i-=expr(a[j],line);
			}
			return i;
		}
		if(e.indexOf("*")>0){
			double i=1;
			for(String a:e.split("\\*")){
				i*=expr(a,line);
			}
			return i;
		}
		if(e.indexOf("/")>0){
			String[]a=e.split("/");
			double i=expr(a[0],line);
			for(int j=1;j<a.length;j++){
				i/=expr(a[j],line);
			}
			return i;
		}
		if(e.indexOf("%")>0){
			String[]a=e.split("%");
			double i=expr(a[0],line);
			for(int j=1;j<a.length;j++){
				double b=expr(a[j],line);
				i-=b*Math.floor(i/b);
			}
			return i;
		}
		if(e.startsWith("-"))return -expr(e.substring(1),line);
		if(e.startsWith("ln"))return log(expr(e.substring(2),line));
		if(e.startsWith("log"))return log10(expr(e.substring(3),line));
		if(e.startsWith("sin"))return sin(expr(e.substring(3),line));
		if(e.startsWith("cos"))return cos(expr(e.substring(3),line));
		if(e.startsWith("tan"))return tan(expr(e.substring(3),line));
		if(e.startsWith("sec"))return 1/cos(expr(e.substring(3),line));
		if(e.startsWith("csc"))return 1/sin(expr(e.substring(3),line));
		if(e.startsWith("cot"))return 1/tan(expr(e.substring(3),line));
		if(e.startsWith("abs"))return abs(expr(e.substring(3),line));
		if(e.startsWith("sqrt"))return sqrt(expr(e.substring(4),line));
		if(e.startsWith("cbrt"))return cbrt(expr(e.substring(4),line));
		if(e.startsWith("_"))return floor(expr(e.substring(1),line));
		if(e.startsWith("!"))return expr(e.substring(1),line)==0?1:0;
		throwError("Could not evaluate expression "+e+" on line "+line);
		}catch(Exception ex){
			throwError("Unexpected error on line "+line);
		}
		return 0;
	}
	static void throwError(String message){
		JOptionPane.showMessageDialog(null,message,"",JOptionPane.ERROR_MESSAGE);
		System.exit(0);
	}
	public static void main(String[]a){
		if(a.length==0)a=new String[]{"file"};
		List<String>line=new ArrayList<>();
		try{Scanner read=new Scanner(new File(a[0]+".mpp"));
		while(read.hasNext()){
			line.add(read.nextLine());
		}
		read.close();}catch(Exception ex){
			throwError("Could not read file.");
		}
		try{log=new PrintStream("log.txt");
		for(int i=0;i<line.size();i++){
			String l=line.get(i);
			String flag="out";
			if(l.indexOf(">")!=-1){
				String[]k=l.split(">");
				flag=k[1];
				l=k[0];
			}
			double q=expr(l,i+1);
			if(flag.equals("out")){
				System.out.println(q);
			}
			if(flag.equals("$")){
				if((int)q==0)System.exit(0);
				if((int)q>line.size()||(int)q<0)throwError("Attempt to go to nonexistent line "+(int)q+" from line "+(i+1));
				i=(int)(q-2);
			}
			if(flag.matches("[a-z]"))vars.put(flag.charAt(0),q);
			if(flag.matches("^\\{(.+)\\}$")){
				flag=flag.replaceAll("^\\{(.+)\\}$","$1");
				tape.put((int)expr(flag,i+1),q);
			}
		}}catch(Exception e){
			throwError("Unidentified error.");
		}
	}
}