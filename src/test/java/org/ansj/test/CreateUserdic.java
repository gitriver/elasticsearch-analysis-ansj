package org.ansj.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.nlpcn.commons.lang.util.IOUtil;

public class CreateUserdic {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String outpath = "G:/userDefine.dic";
		File path = new File("G:/userdefinedword");
		Set<String> set = new HashSet<String>();
		if (path.isDirectory()) {
			File[] files = path.listFiles();
			for(File file : files){
				BufferedReader br = IOUtil.getReader(file.getAbsolutePath(), "UTF-8");
				String temp = null;
				while ((temp = br.readLine()) != null) {
					if(temp.trim() != ""){
						if(temp.contains("#")){
							String[] temps = temp.split("#");
							for(String t: temps){
								set.add(t);
							}
						}else{
							set.add(temp);
						}
					}
				}
				br.close();
			}
		}
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outpath)), "utf-8"));
		Iterator<String> it = set.iterator();
		while(it.hasNext()){
			String term = it.next();
			bw.write(term+"\t"+"ns"+"\t"+"2");
			bw.newLine();
		}
		bw.flush();
		bw.close();
	}

}
