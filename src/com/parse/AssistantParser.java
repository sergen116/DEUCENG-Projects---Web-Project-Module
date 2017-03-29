package com.parse;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@ViewScoped
@ManagedBean
public class AssistantParser {
	
	public AssistantParser(){
		
	}
	
	static public List<String> extractResearchAssistants(){
		
		Document doc;
		List<String> researchAssistants = new ArrayList<String>();
		
		try {
			
			doc = Jsoup.connect("http://ceng.deu.edu.tr/?q=tr/araþtýrma-görevlileri.html").get();
			
			
			Element table = doc.select("table").get(0); //select the first table.
			Elements rows = table.select("tr");

			for (int i = 1; i < rows.size(); i+=2) { //first row is the col names so skip it.
			    Element row = rows.get(i);
			    Elements cols = row.select("td");
			    
			    System.out.println(cols.get(1).text());
			    
			    String column_1 = cols.get(1).text();
			    if(column_1 != null)
			    	researchAssistants.add(column_1);
			    
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return researchAssistants;
		
	}

}
